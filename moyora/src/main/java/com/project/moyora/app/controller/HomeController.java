package com.project.moyora.app.controller;

import com.project.moyora.app.dto.HomeResponseDto;
import com.project.moyora.app.service.HomeService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponseTemplete<HomeResponseDto>> getHome(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername(); // 인증된 사용자 이메일
        log.info("Authenticated user's email: {}", email); // 이메일 로그 찍기
        HomeResponseDto response = homeService.getHomeData(email);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, response);
    }
}
