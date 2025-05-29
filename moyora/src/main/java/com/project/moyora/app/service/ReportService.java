package com.project.moyora.app.service;

import com.project.moyora.app.Dto.ReportResponse;
import com.project.moyora.app.Dto.ReportStatusUpdateRequest;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.ReportRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public void reportBoard(Long boardId, String reason, CustomUserDetails reporterDetails) {
        User reporter = reporterDetails.getUser();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 존재하지 않습니다."));

        if (board.getWriter().getId().equals(reporter.getId())) {
            throw new IllegalArgumentException("본인 게시글은 신고할 수 없습니다.");
        }

        if (reportRepository.existsByReporterAndReportedBoard(reporter, board)) {
            throw new IllegalArgumentException("이미 신고한 게시글입니다.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedBoard(board);
        report.setReportType(ReportType.POST);
        report.setStatus(ReportStatus.PENDING);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    public void reportUser(Long reportedUserId, String reason, CustomUserDetails reporterDetails) {
        User reporter = reporterDetails.getUser();
        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        if (reporter.getId().equals(reported.getId())) {
            throw new IllegalArgumentException("본인을 신고할 수 없습니다.");
        }

        if (reportRepository.existsByReporterAndReportedUserAndStatus(reporter, reported, ReportStatus.PENDING)) {
            throw new IllegalStateException("이미 이 사용자에 대해 처리 대기 중인 신고가 있습니다.");
        }

/*
        if (reportRepository.existsByReporterAndReportedUser(reporter, reported)) {
            throw new IllegalArgumentException("이미 신고한 사용자입니다.");
        }
*/
        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReportType(ReportType.USER);
        report.setStatus(ReportStatus.PENDING);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllWithAssociations().stream().map(report -> {
            ReportResponse res = new ReportResponse();
            res.setId(report.getId());
            res.setReason(report.getReason());
            res.setReportType(report.getReportType());
            res.setStatus(report.getStatus());
            res.setCreatedAt(report.getCreatedAt());

            // 신고자 정보
            res.setReporterId(report.getReporter().getId());
            res.setReporterName(report.getReporter().getName());

            // 피신고 대상 정보
            if (report.getReportType() == ReportType.POST && report.getReportedBoard() != null) {
                res.setReportedId(report.getReportedBoard().getId());
                res.setReportedName(report.getReportedBoard().getTitle()); // 또는 작성자 이름 등으로 수정 가능
                res.setReportedContent(report.getReportedBoard().getContent());
            } else if (report.getReportType() == ReportType.USER && report.getReportedUser() != null) {
                res.setReportedId(report.getReportedUser().getId());
                res.setReportedName(report.getReportedUser().getName());
            }

            return res;
        }).collect(Collectors.toList());
    }


    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("신고 내역이 존재하지 않습니다."));
        report.setStatus(status);
        reportRepository.save(report);
    }

    @Transactional
    public void handleBoardReport(Long reportId, ReportStatusUpdateRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고가 존재하지 않습니다."));

        if (report.getReportType() != ReportType.POST) {
            throw new IllegalStateException("게시글 신고가 아닙니다.");
        }

        report.setStatus(request.getStatus());

        if (request.getStatus() == ReportStatus.ACCEPTED) {
            Board board = report.getReportedBoard();
            boardRepository.delete(board); // 게시글 삭제
        }
    }

    @Transactional
    public void handleUserSuspension(Long reportId, SuspensionPeriod period) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고가 존재하지 않습니다."));

        if (report.getReportType() != ReportType.USER) {
            throw new IllegalStateException("사용자 신고가 아닙니다.");
        }

        User reportedUser = report.getReportedUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = switch (period) {
            case SUSPENDED_3DAYS -> now.plusDays(3);
            case SUSPENDED_7DAYS -> now.plusDays(7);
            case SUSPENDED_1MONTH -> now.plusMonths(1);
            case PERMANENT -> LocalDateTime.MAX;
        };

        reportedUser.setSuspendedUntil(until);
        reportedUser.setSuspensionPeriod(period);
        report.setStatus(ReportStatus.ACCEPTED);
    }

}

