package com.project.moyora.app.controller;


import com.project.moyora.app.Dto.VerificationResponse;
import com.project.moyora.app.service.VerificationService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/verification")
public class VerifiedController {

    @Autowired
    private VerificationService verificationService;

    // 인증 요청 리스트 조회
    @GetMapping("/pending")
    public ResponseEntity<ApiResponseTemplete<List<VerificationResponse>>> getPendingVerifications() {
        List<VerificationResponse> verifications = verificationService.getPendingVerifications();
        return ApiResponseTemplete.success(SuccessCode.USER_VERIFICATION_STATUS_RETRIEVED, verifications);
    }

    // 세부 인증 요청 조회
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponseTemplete<VerificationResponse>> getVerificationDetails(@PathVariable Long id) {
        VerificationResponse verification = verificationService.getVerificationDetails(id);
        return ApiResponseTemplete.success(SuccessCode.USER_VERIFICATION_STATUS_RETRIEVED, verification);
    }

    // 인증 수락
    @PutMapping("/accept/{id}")
    public ResponseEntity<ApiResponseTemplete<String>> acceptVerification(@PathVariable Long id) {
        verificationService.acceptVerification(id);
        return ApiResponseTemplete.success(SuccessCode.USER_VERIFIED, "인증 수락 완료");
    }

    // 인증 거절
    @PutMapping("/reject/{id}")
    public ResponseEntity<ApiResponseTemplete<String>> rejectVerification(@PathVariable Long id) {
        verificationService.rejectVerification(id);
        return ApiResponseTemplete.success(SuccessCode.USER_VERIFICATION_REJECTED, "인증 거절 완료");
    }

    // 인증 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseTemplete<String>> deleteVerification(@PathVariable Long id) {
        verificationService.deleteVerification(id);
        return ApiResponseTemplete.success(SuccessCode.VERIFICATION_DELETED, "인증 요청 삭제 완료");
    }
}
