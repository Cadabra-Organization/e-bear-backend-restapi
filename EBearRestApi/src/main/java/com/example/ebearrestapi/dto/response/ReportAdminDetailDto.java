package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportAdminDetailDto {
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
