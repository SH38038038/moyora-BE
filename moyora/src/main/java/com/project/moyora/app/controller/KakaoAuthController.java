package com.project.moyora.app.controller;

import com.project.moyora.app.dto.KakaoLoginRequest;
import com.project.moyora.app.service.KakaoOAuthService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @PostMapping("/kakao")
    public ResponseEntity<ApiResponseTemplete<Map<String, String>>> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        Map<String, String> tokens = kakaoOAuthService.kakaoLogin(request.getAccessToken());
        return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, tokens);
    }
}
