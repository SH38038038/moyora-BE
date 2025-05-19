package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.ApplicationDto;
import com.project.moyora.app.domain.ApplicationStatus;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.service.ApplicationService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class ApplicationController {

    private final ApplicationService applicationService;

    // 신청 처리 (POST)
    @PostMapping("/{boardId}/apply")
    public ResponseEntity<ApiResponseTemplete<ApplicationDto>> applyForBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        if (user.isSuspended()) {
            throw new IllegalStateException("정지된 사용자는 모임에 참여할 수 없습니다.");
        }

        ApplicationDto applicationDto = applicationService.applyForBoard(boardId, userDetails);
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, applicationDto);
    }

    // 신청 상태 변경 (작성자 또는 신청자)
    @PutMapping("/{boardId}/applications/{applicationId}/status")
    public ResponseEntity<ApiResponseTemplete<String>> updateApplicationStatus(
            @PathVariable Long boardId,
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // currentUser가 null일 경우, 인증되지 않은 사용자 처리
        if (userDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        // 신청 상태 변경 서비스 호출
        applicationService.updateApplicationStatus(boardId, applicationId, status, userDetails);

        // 상태 업데이트 성공 응답 반환
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, "신청 상태가 업데이트되었습니다.");
    }

}
