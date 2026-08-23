package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListResultDto {
    private Long wishListNo;    //찜 번호
    private Long productNo;     //상품 번호
    private String productName; //상품 이름
    private String productFile; //상품 대표사진
    private Integer price;      //상품 가격
    private String sellerName;  //판매자명
    private String sellerFile;  //판매자 사진
}
