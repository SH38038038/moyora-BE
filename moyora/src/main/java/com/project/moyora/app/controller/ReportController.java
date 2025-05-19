package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.ReportPostRequest;
import com.project.moyora.app.Dto.ReportResponse;
import com.project.moyora.app.Dto.ReportStatusUpdateRequest;
import com.project.moyora.app.Dto.ReportUserRequest;
import com.project.moyora.app.service.ReportService;
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

    @PostMapping("/report/post")
    public ResponseEntity<?> reportPost(@RequestBody ReportPostRequest request,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        reportService.reportPost(userDetails.getUser(), request.getBoardId(), request.getReason());
        return ResponseEntity.ok("게시글이 신고되었습니다.");
    }

    @PostMapping("/report/user")
    public ResponseEntity<?> reportUser(@RequestBody ReportUserRequest request,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        reportService.reportUser(userDetails.getUser(), request.getReportedUserId(), request.getReason());
        return ResponseEntity.ok("이용자가 신고되었습니다.");
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
