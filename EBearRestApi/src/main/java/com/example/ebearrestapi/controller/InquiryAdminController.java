package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.InquiryAdminAnswerDto;
import com.example.ebearrestapi.dto.response.InquiryAdminDetailDto;
import com.example.ebearrestapi.dto.response.InquiryAdminDto;
import com.example.ebearrestapi.service.InquiryAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiry/admin")
@RequiredArgsConstructor
public class InquiryAdminController {
    private final InquiryAdminService inquiryAdminService;

    @GetMapping("/list")
    public List<InquiryAdminDto> list(@AuthenticationPrincipal User user) {
        return inquiryAdminService.list(user);
    }

    @GetMapping("/detail/{inquiryNo}")
    public InquiryAdminDetailDto detail(@PathVariable Long inquiryNo, @AuthenticationPrincipal User user) {
        return inquiryAdminService.detail(inquiryNo, user);
    }

    @PutMapping("/detail/{inquiryNo}/answer")
    public InquiryAdminDetailDto answer(@PathVariable Long inquiryNo, @Valid @RequestBody InquiryAdminAnswerDto answerDto, @AuthenticationPrincipal User user) {
        return inquiryAdminService.answer(inquiryNo, answerDto, user);
    }
}
