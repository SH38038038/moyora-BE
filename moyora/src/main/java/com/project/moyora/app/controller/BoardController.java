package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.BoardDto;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.service.BoardService;
import com.project.moyora.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // ✅ 모집글 생성
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(@RequestBody @Valid BoardDto dto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        User user = userDetails.getUser();

        // verified 검사
        if (!Boolean.TRUE.equals(user.getVerified())) {
            throw new AccessDeniedException("인증된 사용자만 글을 작성할 수 있습니다.");
        }

        BoardDto created = boardService.createBoard(dto, user);
        return ResponseEntity.ok(created);
    }



    // ✅ 모집글 목록 조회
    @GetMapping
    public ResponseEntity<List<BoardDto>> getBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    // ✅ 모집글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    // ✅ 모집글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable Long id, @RequestBody @Valid BoardDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(boardService.updateBoard(id, dto, userDetails.getUser()));
    }

    // ✅ 모집글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.deleteBoard(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}

