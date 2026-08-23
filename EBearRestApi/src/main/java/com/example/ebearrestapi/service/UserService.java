package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.SignupDto;
import com.example.ebearrestapi.dto.request.UserProfileUpdateRequest;
import com.example.ebearrestapi.dto.response.UserProfileResponse;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthStore emailAuthStore;

    public UserEntity signup(SignupDto dto) {

        if(!emailAuthStore.isVerified(dto.getEmail())){
            throw new RuntimeException("이메일 인증을 완료해주세요.");
        }
//        emailAuthStore.remove(dto.getEmail());

        userRepository.findByUserId(dto.getId())
                .ifPresent(user -> {
                    throw new RuntimeException("이미 존재하는 아이디입니다.");
                });

        UserEntity user = dto.toEntity(passwordEncoder.encode(dto.getPw()));
        return userRepository.save(user);
    }

    public UserProfileResponse getProfile(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String userId, UserProfileUpdateRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        user.updateProfile(
                request.getName(),
                request.getEmail(),
                request.getAddress(),
                request.getPhone()
        );

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserProfileResponse.from(user);
    }
}
