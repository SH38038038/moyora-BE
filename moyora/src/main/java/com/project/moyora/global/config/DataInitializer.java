package com.project.moyora.global.config;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.service.BoardSearchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final BoardRepository boardRepository;
    private final BoardSearchService boardSearchService;

    @PostConstruct
    public void indexAllBoardsForTest() {
        List<Board> boards = boardRepository.findAll();
        boards.forEach(boardSearchService::indexBoard);
        System.out.println("✅ 게시판 데이터 Elasticsearch 색인 완료");
    }
}
