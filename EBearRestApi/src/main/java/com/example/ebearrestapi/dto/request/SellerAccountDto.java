package com.example.ebearrestapi.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerAccountDto {
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}
