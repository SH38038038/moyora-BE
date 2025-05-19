package com.project.moyora.app.service;

import com.project.moyora.app.Dto.ReportResponse;
import com.project.moyora.app.Dto.ReportStatusUpdateRequest;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.ReportRepository;
import com.project.moyora.app.repository.UserRepository;
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

    public void reportPost(User reporter, Long boardId, String reason) {
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

    public void reportUser(User reporter, Long reportedUserId, String reason) {
        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        if (reporter.getId().equals(reported.getId())) {
            throw new IllegalArgumentException("본인을 신고할 수 없습니다.");
        }

        if (reportRepository.existsByReporterAndReportedUser(reporter, reported)) {
            throw new IllegalArgumentException("이미 신고한 사용자입니다.");
        }

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
            res.setReporterEmail(report.getReporter().getEmail());
            res.setReportedTarget(report.getReportType() == ReportType.POST ?
                    "Post ID: " + report.getReportedBoard().getId() :
                    "User Email: " + report.getReportedUser().getEmail());
            res.setCreatedAt(report.getCreatedAt());
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

