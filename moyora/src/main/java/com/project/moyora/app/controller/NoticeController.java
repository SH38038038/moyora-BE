package com.project.moyora.app.controller;

import com.project.moyora.app.dto.BoardDto;
import com.project.moyora.app.dto.CommentRequest;
import com.project.moyora.app.dto.NoticeDto;
import com.project.moyora.app.dto.NoticeRequest;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.service.BoardService;
import com.project.moyora.app.service.NoticeService;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final BoardService boardService;

    // 공지사항 생성
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponseTemplete<NoticeDto>> createNotice(
            @PathVariable Long boardId,
            @RequestBody NoticeRequest noticeRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        BoardDto boardDto = new BoardDto(boardId);
        Board board = boardService.getBoardEntityFromDto(boardDto);

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 공지를 생성할 수 있습니다.");
        }

        if (!board.isConfirmed()) {
            throw new AccessDeniedException("참여 인원이 모두 찼을 때만 공지사항을 생성할 수 있습니다.");
        }

        NoticeDto notice = noticeService.createNotice(boardId, noticeRequest, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, notice);
    }

    // 공지사항 조회 (단건)
    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponseTemplete<NoticeDto>> getNoticeByBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        BoardDto boardDto = new BoardDto(boardId);
        Board board = boardService.getBoardEntityFromDto(boardDto);

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 공지사항을 조회할 수 있습니다.");
        }

        NoticeDto notice = noticeService.getNoticeByBoard(boardId);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, notice);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponseTemplete<NoticeDto>> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest noticeRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        NoticeDto updatedNotice = noticeService.updateNotice(noticeId, noticeRequest, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, updatedNotice);
    }

    // 공지사항 삭제 ❌ 금지
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponseTemplete<String>> deleteNotice(@PathVariable Long noticeId) {
        throw new UnsupportedOperationException("공지사항은 삭제할 수 없습니다.");
    }

    // 댓글 추가
    @PostMapping("/{boardId}/{noticeId}/comments")
    public ResponseEntity<ApiResponseTemplete<String>> addComment(
            @PathVariable Long boardId, @PathVariable Long noticeId,
            @RequestBody CommentRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {

        BoardDto boardDto = new BoardDto(boardId);
        Board board = boardService.getBoardEntityFromDto(boardDto);

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 댓글을 작성할 수 있습니다.");
        }

        noticeService.addComment(noticeId, request.getContent(), userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, "댓글이 등록되었습니다.");
    }
}
