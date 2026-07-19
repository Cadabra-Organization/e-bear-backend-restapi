package com.example.ebearrestapi.dto.response;

import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.etc.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private Long userNo;
    private String userId;
    private String name;
    private String email;
    private String post;
    private String address;
    private String addressDetails;
    private String fullAddress;
    private String phone;
    private Role role;

    public static UserProfileResponse from(UserEntity user) {
        return UserProfileResponse.builder()
                .userNo(user.getUserNo())
                .userId(user.getUserId())
                .name(user.getUserName())
                .email(user.getEmail())
                .post(user.getPost())
                .address(user.getAddress())
                .addressDetails(user.getAddressDetails())
                .fullAddress(user.getFullAddress())
                .phone(user.getMobile())
                .role(user.getRole())
                .build();
    }
}
