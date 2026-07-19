package com.example.ebearrestapi.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String password;
    private String name;
    private String email;
    private String address;
    private String phone;
}
