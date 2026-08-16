package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReportUserListResponseDto {
    private List<ReportUserListDto> reports;
}
