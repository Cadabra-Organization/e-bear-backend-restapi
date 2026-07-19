package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.UserProfileUpdateRequest;
import com.example.ebearrestapi.dto.response.UserProfileResponse;
import com.example.ebearrestapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @PutMapping("/user/me")
    public UserProfileResponse updateMyProfile(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequest request
    ) {
        return userService.updateProfile(authentication.getName(), request);
    }
}
