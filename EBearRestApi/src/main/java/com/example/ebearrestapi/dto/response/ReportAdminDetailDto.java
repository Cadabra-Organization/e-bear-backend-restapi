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
    private Long reportNo;
    private String productName;
    private boolean answered;
    private String customerName;
    private String customerId;
    private LocalDateTime reportRegDt;
    private String title;
    private String reportContent;
    private String answerContent;
    private LocalDateTime respondDt;
    private String responder;
}
