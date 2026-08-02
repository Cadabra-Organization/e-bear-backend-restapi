package com.example.ebearrestapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryAdminAnswerDto {
    @NotBlank(message = "답변 내용을 입력해 주세요.")
    private String content;
}
