package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByBoardId(Long boardId);

}
