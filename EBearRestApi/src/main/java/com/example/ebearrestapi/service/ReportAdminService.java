package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.ReportAdminAnswerDto;
import com.example.ebearrestapi.dto.response.ReportAdminDetailDto;
import com.example.ebearrestapi.dto.response.ReportAdminDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAdminService {
    public ReportAdminDetailDto detail(Long reportNo, User user) {
        return ReportAdminDetailDto.builder().answered(false).build();
    }

    public ReportAdminDetailDto answer(Long reportNo, @Valid ReportAdminAnswerDto answerDto, User user) {
        return ReportAdminDetailDto.builder().answered(false).build();
    }

    public List<ReportAdminDto> list(User user) {
        return null;
    }
}
