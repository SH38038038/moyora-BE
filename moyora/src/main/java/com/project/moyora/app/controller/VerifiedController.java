package com.project.moyora.app.controller;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.domain.VerificationStatus;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/verify")
@RequiredArgsConstructor
public class VerifiedController {

    private final UserRepository userRepository;

    /**
     * 1. 사용자 인증 정보 조회 (사진, 생일, 성별)
     */
    @Operation(summary = "사용자 신분증 인증을 위한 정보 조회")
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponseTemplete<Map<String, Object>>> getVerificationInfo(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();
        result.put("email", user.getEmail());
        result.put("idcardUrl", user.getIdCardUrl());
        result.put("birth", user.getBirth());
        result.put("gender", user.getGender());
        result.put("verificationStatus", user.getVerificationStatus());

        return ApiResponseTemplete.success(SuccessCode.USER_ID_CARD_RETRIEVED, result);
    }

    /**
     * 2. 인증 수락
     */
    @Operation(summary = "사용자 인증 수락")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponseTemplete<String>> acceptVerification(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setVerificationStatus(VerificationStatus.ACCEPTED);
        user.setVerified(true);
        userRepository.save(user);

        return ApiResponseTemplete.success(SuccessCode.USER_VERIFIED, "인증 수락 완료");
    }

    /**
     * 3. 인증 거절
     */
    @Operation(summary = "사용자 인증 거절")
    @PostMapping("/reject")
    public ResponseEntity<ApiResponseTemplete<String>> rejectVerification(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setVerificationStatus(VerificationStatus.REJECTED);
        user.setVerified(false);
        userRepository.save(user);

        return ApiResponseTemplete.success(SuccessCode.USER_VERIFICATION_REJECTED, "인증 거절 완료");
    }
}
