package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.ReportWriteDto;
import com.example.ebearrestapi.dto.response.ReportUserListDto;
import com.example.ebearrestapi.dto.response.ReportUserListResponseDto;
import com.example.ebearrestapi.entity.*;
import com.example.ebearrestapi.etc.FileType;
import com.example.ebearrestapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportUserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BoardRepository boardRepository;
    private final ReportRepository reportRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void write(ReportWriteDto reportWriteDto, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ProductEntity productEntity = productRepository.findById(reportWriteDto.getProductNo()).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        BoardEntity boardEntity = BoardEntity.builder().title(reportWriteDto.getTitle()).content(reportWriteDto.getContent()).user(userEntity).build();
        boardRepository.save(boardEntity);

        ReportEntity reportEntity = ReportEntity.builder().board(boardEntity).product(productEntity).build();
        reportRepository.save(reportEntity);
    }

    public ReportUserListResponseDto getMyReportList(User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<ReportEntity> reportEntityList = reportRepository.findMyRootReport(userEntity.getUserNo());

        if (reportEntityList.isEmpty()) {
            return new ReportUserListResponseDto(List.of());
        }

        List<Long> reportNos = reportEntityList.stream().map(ReportEntity::getReportNo).toList();
        List<Long> productNos = reportEntityList.stream().map(report -> report.getProduct().getProductNo()).distinct().toList();
        List<ReportEntity> replies = reportRepository.findRepliesByParentReportNos(reportNos);
        List<FileEntity> productFiles = fileRepository.findByProduct_ProductNoInAndFileType(productNos, FileType.THUMBNAIL);
        Map<Long, ReportEntity> replyMap = replies.stream().collect(Collectors.toMap(
                reply -> reply.getParent().getReportNo(),
                reply -> reply
        ));
        Map<Long, String> productImageUrlMap = createProductImageUrlMap(productFiles);
        List<ReportUserListDto> result = reportEntityList.stream()
                .map(report -> toReportUserListDto(
                        report,
                        replyMap.get(report.getReportNo()),
                        productImageUrlMap.get(report.getProduct().getProductNo())
                ))
                .toList();

        return new ReportUserListResponseDto(result);
    }

    private Map<Long, String> createProductImageUrlMap(
            List<FileEntity> productFiles
    ) {
        Map<Long, String> productImageUrlMap = new HashMap<>();

        for (FileEntity file : productFiles) {
            Long productNo = file.getProduct().getProductNo();

            productImageUrlMap.put(
                    productNo,
                    file.getFileLocation()
            );
        }

        return productImageUrlMap;
    }

    private ReportUserListDto toReportUserListDto(
            ReportEntity report,
            ReportEntity reply,
            String productImageUrl
    ) {
        BoardEntity reportBoard = report.getBoard();
        ProductEntity product = report.getProduct();

        boolean answered = reply != null;

        String answerContent = null;
        LocalDateTime answerRegDate = null;

        if (answered) {
            BoardEntity answerBoard = reply.getBoard();

            answerContent = answerBoard.getContent();
            answerRegDate = answerBoard.getRegDate();
        }

        return ReportUserListDto.builder()
                .reportNo(report.getReportNo())
                .productNo(product.getProductNo())
                .brandName(product.getUser().getUserName())
                .productName(product.getProductName())
                .productImageUrl(productImageUrl)
                .title(reportBoard.getTitle())
                .content(reportBoard.getContent())
                .regDate(reportBoard.getRegDate())
                .answered(answered)
                .answerContent(answerContent)
                .answerRegDate(answerRegDate)
                .build();
    }
}
