package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.ReviewWriteReqDto;
import com.example.ebearrestapi.dto.response.ReviewWriteResDto;
import com.example.ebearrestapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody ReviewWriteReqDto reviewWriteReqDto, @AuthenticationPrincipal User user) {
        ReviewWriteResDto reviewWriteResDto = reviewService.write(reviewWriteReqDto, user);
        return ResponseEntity.status(HttpStatus.OK).body(reviewWriteResDto);
    }
}
