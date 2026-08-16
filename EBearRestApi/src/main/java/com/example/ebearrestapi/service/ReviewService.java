package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.ReviewWriteReqDto;
import com.example.ebearrestapi.dto.response.ReviewWriteResDto;
import com.example.ebearrestapi.entity.BoardEntity;
import com.example.ebearrestapi.entity.ProductEntity;
import com.example.ebearrestapi.entity.ReviewEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.repository.ReviewRepository;
import com.example.ebearrestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    private final ProductService productService;

    @Transactional
    public ReviewWriteResDto write(ReviewWriteReqDto reviewWriteReqDto, User user) {
        UserEntity userInfo = userRepository.findByUserId(user.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        BoardEntity board = boardService.selectBoardEntity(reviewWriteReqDto.getBoardId());
        ProductEntity product = productService.findByProductId(reviewWriteReqDto.getProductId());

        ReviewEntity review = ReviewEntity.builder().board(board).user(userInfo).rating(reviewWriteReqDto.getRating()).product(product).build();
        reviewRepository.save(review);

        return ReviewWriteResDto.builder().localDateTime(LocalDateTime.now()).build();
    }
}
