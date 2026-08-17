package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.ReportAdminAnswerDto;
import com.example.ebearrestapi.dto.response.ReportAdminDetailDto;
import com.example.ebearrestapi.dto.response.ReportAdminDto;
import com.example.ebearrestapi.entity.BoardEntity;
import com.example.ebearrestapi.entity.ReportEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.repository.BoardRepository;
import com.example.ebearrestapi.repository.ReportRepository;
import com.example.ebearrestapi.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAdminService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public ReportAdminDetailDto detail(Long reportNo, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ReportEntity reportEntity = reportRepository.findRootReportDetail(reportNo).orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        if (!userEntity.isAdmin() && !reportEntity.getProduct().getUser().getUserNo().equals(userEntity.getUserNo())) {
            throw new RuntimeException("해당 문의를 조회할 권한이 없습니다.");
        }

        BoardEntity boardEntity = reportEntity.getBoard();
        ReportEntity replyEntity = reportRepository.findReplyByParentReportNo(reportNo).orElse(null);
        BoardEntity replyBoardEntity = replyEntity == null ? null : replyEntity.getBoard();

        LocalDateTime respondDt = null;
        if (replyBoardEntity != null) {
            respondDt = replyBoardEntity.getUpdateDate() == null ? replyBoardEntity.getRegDate() : replyBoardEntity.getUpdateDate();
        }

        return new ReportAdminDetailDto(
                reportEntity.getReportNo(),
                reportEntity.getProduct().getProductName(),
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

    public ReportAdminDetailDto answer(Long reportNo, @Valid ReportAdminAnswerDto answerDto, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ReportEntity reportEntity = reportRepository.findRootReportDetail(reportNo).orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        if (!userEntity.isAdmin() && !reportEntity.getProduct().getUser().getUserNo().equals(userEntity.getUserNo())) {
            throw new RuntimeException("해당 문의에 답변할 권한이 없습니다.");
        }

        ReportEntity replyEntity = reportRepository.findReplyByParentReportNo(reportNo).orElse(null);
        if (replyEntity == null) {
            // 기존 답변이 없으면 새 답변 생성
            BoardEntity replyBoardEntity = BoardEntity.builder()
                    .title(reportEntity.getBoard().getTitle())
                    .content(answerDto.getContent().trim())
                    .user(userEntity)
                    .build();

            boardRepository.save(replyBoardEntity);

            replyEntity = ReportEntity.builder()
                    .stateCode(reportEntity.getStateCode())
                    .board(replyBoardEntity)
                    .product(reportEntity.getProduct())
                    .build();

            reportEntity.addReply(replyEntity);

            reportRepository.save(replyEntity);
        } else {
            // 기존 답변이 있으면 내용과 답변자를 수정
            BoardEntity replyBoardEntity = replyEntity.getBoard();

            replyBoardEntity.setContent(answerDto.getContent().trim());
            replyBoardEntity.setUser(userEntity);
        }
        boardRepository.flush();

        BoardEntity boardEntity = reportEntity.getBoard();
        BoardEntity replyBoardEntity = replyEntity.getBoard();

        LocalDateTime respondDt = replyBoardEntity.getUpdateDate() == null
                ? replyBoardEntity.getRegDate()
                : replyBoardEntity.getUpdateDate();

        return new ReportAdminDetailDto(
                reportEntity.getReportNo(),
                reportEntity.getProduct().getProductName(),
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

    public List<ReportAdminDto> list(User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<ReportEntity> reportEntityList;

        if (userEntity.isAdmin()) {
            reportEntityList = reportRepository.findByParentIsNullAndBoard_DelYNOrderByReportNoDesc("N");
        } else {
            reportEntityList = reportRepository.findByParentIsNullAndBoard_DelYNAndProduct_UserOrderByReportNoDesc("N", userEntity);
        }

        List<ReportAdminDto> reportAdminDtoList = new ArrayList<>();
        for (ReportEntity reportEntity : reportEntityList) {
            BoardEntity boardEntity = reportEntity.getBoard();

            ReportEntity replyEntity = reportEntity.getChildList().isEmpty() ? null : reportEntity.getChildList().get(0);
            BoardEntity replyBoardEntity = replyEntity == null ? null : replyEntity.getBoard();

            LocalDateTime respondDt = null;
            if (replyBoardEntity != null) {
                respondDt = replyBoardEntity.getUpdateDate() == null ? replyBoardEntity.getRegDate() : replyBoardEntity.getUpdateDate();
            }

            ReportAdminDto reportAdminDto = new ReportAdminDto(
                    reportEntity.getReportNo(),
                    reportEntity.getProduct().getProductName(),
                    boardEntity.getTitle(),
                    boardEntity.getUser().getUserName(),
                    boardEntity.getRegDate(),
                    respondDt,
                    replyBoardEntity == null ? null : replyBoardEntity.getUser().getUserName()
            );

            reportAdminDtoList.add(reportAdminDto);
        }

        return reportAdminDtoList;
    }
}
