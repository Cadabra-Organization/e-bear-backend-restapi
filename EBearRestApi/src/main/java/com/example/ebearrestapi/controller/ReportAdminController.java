package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.ReportAdminAnswerDto;
import com.example.ebearrestapi.dto.response.ReportAdminDetailDto;
import com.example.ebearrestapi.dto.response.ReportAdminDto;
import com.example.ebearrestapi.service.ReportAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report/admin")
@RequiredArgsConstructor
public class ReportAdminController {
    private final ReportAdminService reportAdminService;

    @GetMapping("/list")
    public List<ReportAdminDto> list(@AuthenticationPrincipal User user) {
        return reportAdminService.list(user);
    }

    @GetMapping("/detail/{reportNo}")
    public ReportAdminDetailDto detail(@PathVariable Long reportNo, @AuthenticationPrincipal User user) {
        return reportAdminService.detail(reportNo, user);
    }

    @PutMapping("/detail/{reportNo}/answer")
    public ReportAdminDetailDto answer(@PathVariable Long reportNo, @Valid @RequestBody ReportAdminAnswerDto answerDto, @AuthenticationPrincipal User user) {
        return reportAdminService.answer(reportNo, answerDto, user);
    }
}
