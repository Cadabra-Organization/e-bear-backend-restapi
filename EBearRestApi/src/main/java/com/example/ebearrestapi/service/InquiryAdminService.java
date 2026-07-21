package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.InquiryAdminAnswerDto;
import com.example.ebearrestapi.dto.response.InquiryAdminDetailDto;
import com.example.ebearrestapi.dto.response.InquiryAdminDto;
import com.example.ebearrestapi.entity.BoardEntity;
import com.example.ebearrestapi.entity.InquiryEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.repository.BoardRepository;
import com.example.ebearrestapi.repository.InquiryRepository;
import com.example.ebearrestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<InquiryAdminDto> list(User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<InquiryEntity> inquiryEntityList;

        if (userEntity.isAdmin()) {
            inquiryEntityList = inquiryRepository.findByParentIsNullAndBoard_DelYNOrderByInquiryNoDesc("N");
        } else {
            inquiryEntityList = inquiryRepository.findByParentIsNullAndBoard_DelYNAndProduct_UserOrderByInquiryNoDesc("N", userEntity);
        }

        List<InquiryAdminDto> inquiries = new ArrayList<>();
        for (InquiryEntity inquiryEntity : inquiryEntityList) {
            BoardEntity boardEntity = inquiryEntity.getBoard();

            InquiryEntity replyEntity = inquiryEntity.getChildrenList().isEmpty() ? null : inquiryEntity.getChildrenList().get(0);
            BoardEntity replyBoardEntity = replyEntity == null ? null : replyEntity.getBoard();

            LocalDateTime respondDt = null;
            if (replyBoardEntity != null) {
                respondDt = replyBoardEntity.getUpdateDate() == null ? replyBoardEntity.getRegDate() : replyBoardEntity.getUpdateDate();
            }

            InquiryAdminDto inquiryAdminDto = new InquiryAdminDto(
                    inquiryEntity.getInquiryNo(),
                    inquiryEntity.getProduct().getProductName(),
                    boardEntity.getTitle(),
                    boardEntity.getUser().getUserName(),
                    boardEntity.getRegDate(),
                    respondDt,
                    replyBoardEntity == null ? null : replyBoardEntity.getUser().getUserName()
            );

            inquiries.add(inquiryAdminDto);
        }

        return inquiries;
    }

    @Transactional(readOnly = true)
    public InquiryAdminDetailDto detail(Long inquiryNo, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        InquiryEntity inquiryEntity = inquiryRepository.findRootInquiryDetail(inquiryNo).orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        if (!userEntity.isAdmin() && !inquiryEntity.getProduct().getUser().getUserNo().equals(userEntity.getUserNo())) {
            throw new RuntimeException("해당 문의를 조회할 권한이 없습니다.");
        }

        BoardEntity boardEntity = inquiryEntity.getBoard();
        InquiryEntity replyEntity = inquiryRepository.findReplyByParentInquiryNo(inquiryNo).orElse(null);
        BoardEntity replyBoardEntity = replyEntity == null ? null : replyEntity.getBoard();

        LocalDateTime respondDt = null;
        if (replyBoardEntity != null) {
            respondDt = replyBoardEntity.getUpdateDate() == null ? replyBoardEntity.getRegDate() : replyBoardEntity.getUpdateDate();
        }

        return new InquiryAdminDetailDto(
                inquiryEntity.getInquiryNo(),
                inquiryEntity.getProduct().getProductName(),
                replyEntity != null,
                boardEntity.getUser().getUserName(),
                boardEntity.getUser().getUserId(),
                boardEntity.getRegDate(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                replyBoardEntity == null
                        ? null
                        : replyBoardEntity.getContent(),
                respondDt,
                replyBoardEntity == null
                        ? null
                        : replyBoardEntity.getUser().getUserName()
        );
    }

    @Transactional
    public InquiryAdminDetailDto answer(Long inquiryNo, InquiryAdminAnswerDto answerDto, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        InquiryEntity inquiryEntity = inquiryRepository.findRootInquiryDetail(inquiryNo).orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        if (!userEntity.isAdmin() && !inquiryEntity.getProduct().getUser().getUserNo().equals(userEntity.getUserNo())) {
            throw new RuntimeException("해당 문의에 답변할 권한이 없습니다.");
        }

        InquiryEntity replyEntity = inquiryRepository.findReplyByParentInquiryNo(inquiryNo).orElse(null);
        if (replyEntity == null) {
            // 기존 답변이 없으면 새 답변 생성
            BoardEntity replyBoardEntity = BoardEntity.builder()
                    .title(inquiryEntity.getBoard().getTitle())
                    .content(answerDto.getContent().trim())
                    .user(userEntity)
                    .build();

            boardRepository.save(replyBoardEntity);

            replyEntity = InquiryEntity.builder()
                    .stateCode(inquiryEntity.getStateCode())
                    .board(replyBoardEntity)
                    .product(inquiryEntity.getProduct())
                    .build();

            inquiryEntity.addReply(replyEntity);

            inquiryRepository.save(replyEntity);
        } else {
            // 기존 답변이 있으면 내용과 답변자를 수정
            BoardEntity replyBoardEntity = replyEntity.getBoard();

            replyBoardEntity.setContent(answerDto.getContent().trim());
            replyBoardEntity.setUser(userEntity);
        }
        boardRepository.flush();

        BoardEntity boardEntity = inquiryEntity.getBoard();
        BoardEntity replyBoardEntity = replyEntity.getBoard();

        LocalDateTime respondDt = replyBoardEntity.getUpdateDate() == null
                ? replyBoardEntity.getRegDate()
                : replyBoardEntity.getUpdateDate();

        return new InquiryAdminDetailDto(
                inquiryEntity.getInquiryNo(),
                inquiryEntity.getProduct().getProductName(),
                true,
                boardEntity.getUser().getUserName(),
                boardEntity.getUser().getUserId(),
                boardEntity.getRegDate(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                replyBoardEntity.getContent(),
                respondDt,
                replyBoardEntity.getUser().getUserName()
        );
    }
}
