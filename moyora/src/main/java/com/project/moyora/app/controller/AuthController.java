package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.LoginRequestDto;
import com.project.moyora.app.domain.RoleType;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        // 이메일로 사용자 조회
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        // 관리자 권한 확인
        if (!user.getRoleType().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 접근 가능합니다.");
        }

        // 토큰 생성
        String token = tokenService.createAccessToken(user);

        // 응답
        return ResponseEntity.ok(Collections.singletonMap("accessToken", token));
    }
}


