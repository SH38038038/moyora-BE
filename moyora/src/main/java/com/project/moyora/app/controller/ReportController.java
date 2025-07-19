package com.project.moyora.app.controller;

import com.project.moyora.app.dto.ReportRequest;
import com.project.moyora.app.dto.ReportResponse;
import com.project.moyora.app.dto.ReportStatusUpdateRequest;
import com.project.moyora.app.service.ReportService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // 게시글 신고
    @PostMapping("/report/board/{boardId}")
    public ResponseEntity<?> reportBoard(
            @PathVariable Long boardId,
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        reportService.reportBoard(boardId, request.getReason(), currentUser);
        return ResponseEntity.ok(ApiResponseTemplete.success(SuccessCode.REPORT_SUCCESS,"게시글 신고 완료"));
    }

    // 사용자 신고
    @PostMapping("/report/user/{reportedUserId}")
    public ResponseEntity<?> reportUser(
            @PathVariable Long reportedUserId,
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        reportService.reportUser(reportedUserId, request.getReason(), currentUser);
        return ResponseEntity.ok(ApiResponseTemplete.success(SuccessCode.REPORT_SUCCESS,"이용자 신고 완료"));
    }

    @GetMapping("/admin/reports")
    public List<ReportResponse> getAllReports() {
        return reportService.getAllReports();
    }

    @PatchMapping("/admin/reports/{id}/status")
    public ResponseEntity<?> updateReportStatus(@PathVariable Long id, @RequestBody ReportStatusUpdateRequest request) {
        reportService.updateReportStatus(id, request.getStatus());
        return ResponseEntity.ok("신고 상태가 업데이트되었습니다.");
    }
}
