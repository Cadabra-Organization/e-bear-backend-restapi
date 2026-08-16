package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.ReportWriteDto;
import com.example.ebearrestapi.service.ReportUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/user")
@RequiredArgsConstructor
public class ReportUserController {
    private final ReportUserService reportUserService;

    @PostMapping("/write")
    public void write(@RequestBody ReportWriteDto reportWriteDto, @AuthenticationPrincipal User user) {
        reportUserService.write(reportWriteDto, user);
    }
}
