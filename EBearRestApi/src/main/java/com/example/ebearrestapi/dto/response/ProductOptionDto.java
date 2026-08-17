package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {
    private Long productOptionId;
    private String productOptionName;
    private int quantity;
    private Long couponNo;
    private int price;
    private Long reviewId;
}
