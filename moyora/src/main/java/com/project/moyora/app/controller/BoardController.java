package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.*;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.service.ApplicationService;
import com.project.moyora.app.service.BoardService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final ApplicationService applicationService;

    // ✅ 모집글 생성
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(@RequestBody @Valid BoardDto dto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        User user = userDetails.getUser();

        if (user.isSuspended()) {
            throw new IllegalStateException("정지된 사용자는 게시글을 작성할 수 없습니다.");
        }

        // verified 검사
        if (!Boolean.TRUE.equals(user.getVerified())) {
            throw new AccessDeniedException("인증된 사용자만 글을 작성할 수 있습니다.");
        }

        BoardDto created = boardService.createBoard(dto, user);
        return ResponseEntity.ok(created);
    }


    // ✅ 모집글 목록 조회
    @GetMapping
    public ResponseEntity<List<BoardListDto>> getBoards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        List<BoardListDto> boards = boardService.getAllBoards(currentUser);
        return ResponseEntity.ok(boards);
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
    public ResponseEntity<ApiResponseTemplete<String>> deleteBoard(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.deleteBoard(id, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.DELETE_POST_SUCCESS, "모임이 삭제되었습니다.");
    }

    @PutMapping("/{boardId}/confirm")
    public ResponseEntity<ApiResponseTemplete<String>> confirmBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boardService.confirmBoard(boardId, userDetails.getUser());  // 확정 처리
        boardService.lockParticipantsAfterNoticeCreation(boardId);
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, "모임이 확정되었습니다.");
    }

    // ✅ 모집 신청 현황
    @GetMapping("/{boardId}/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getBoardApplications(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // ✅ userDetails를 그대로 넘김
        List<ApplicationResponseDto> responseDtos = applicationService
                .getApplicationsForBoardByOwner(boardId, userDetails);

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BoardResponseDto>> searchBoards(BoardSearchRequest request) {
        List<Board> results = boardService.searchBoards(request);
        List<BoardResponseDto> response = results.stream()
                .map(BoardResponseDto::from)
                .toList();
        return ResponseEntity.ok(response);
    }


}

