package com.project.moyora.app.repository;

import com.project.moyora.app.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReportedUser(User reporter, User reportedUser);
    boolean existsByReporterAndReportedBoard(User reporter, Board reportedBoard);
    boolean existsByReportTypeAndReportedBoardAndStatus(ReportType reportType, Board board, ReportStatus status);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.reporter LEFT JOIN FETCH r.reportedUser LEFT JOIN FETCH r.reportedBoard")
    List<Report> findAllWithAssociations();

    boolean existsByReporterAndReportedUserAndStatus(User reporter, User reportedUser, ReportStatus status);


}
