package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryAdminDetailDto {
    private Long inquiryNo;
    private String productName;
    private boolean answered;
    private String customerName;
    private String customerId;
    private LocalDateTime inquiryRegDt;
    private String title;
    private String inquiryContent;
    private String answerContent;
    private LocalDateTime respondDt;
    private String responder;
}
