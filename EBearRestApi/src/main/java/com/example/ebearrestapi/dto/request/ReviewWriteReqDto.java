package com.example.ebearrestapi.dto.request;

import lombok.Data;

@Data
public class ReviewWriteReqDto {
    private Long boardId;
    private Integer rating;
    private Long productId;
}
