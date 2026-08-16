package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.ReportWriteDto;
import com.example.ebearrestapi.entity.*;
import com.example.ebearrestapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportUserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BoardRepository boardRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public void write(ReportWriteDto reportWriteDto, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ProductEntity productEntity = productRepository.findById(reportWriteDto.getProductNo()).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        BoardEntity boardEntity = BoardEntity.builder().title(reportWriteDto.getTitle()).content(reportWriteDto.getContent()).user(userEntity).build();
        boardRepository.save(boardEntity);

        ReportEntity reportEntity = ReportEntity.builder().board(boardEntity).product(productEntity).build();
        reportRepository.save(reportEntity);
    }
}
