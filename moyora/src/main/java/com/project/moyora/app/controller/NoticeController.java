package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.BoardDto;
import com.project.moyora.app.Dto.CommentRequest;
import com.project.moyora.app.Dto.NoticeDto;
import com.project.moyora.app.Dto.NoticeRequest;
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
import java.util.List;

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

        BoardDto boardDto = new BoardDto(boardId);  // BoardDto 생성
        Board board = boardService.getBoardEntityFromDto(boardDto);  // Board 엔티티로 변환

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 공지를 생성할 수 있습니다.");
        }

        if (!board.isConfirmed()) {
            throw new AccessDeniedException("참여 인원이 모두 찼을 때만 공지사항을 생성할 수 있습니다.");
        }

        NoticeDto notice = noticeService.createNotice(noticeRequest, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, notice);
    }

    // 공지사항 조회 (단건)
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponseTemplete<NoticeDto>> getNotice(
            @PathVariable Long noticeId) {

        NoticeDto notice = noticeService.getNotice(noticeId);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, notice);
    }

    // 공지사항 조회 (전체)
    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponseTemplete<List<NoticeDto>>> getNoticesByBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        BoardDto boardDto = new BoardDto(boardId);  // BoardDto 생성
        Board board = boardService.getBoardEntityFromDto(boardDto);  // Board 엔티티로 변환

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 공지사항을 조회할 수 있습니다.");
        }

        List<NoticeDto> notices = noticeService.getNoticesByBoard(boardId);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, notices);
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

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponseTemplete<String>> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        noticeService.deleteNotice(noticeId, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.DELETE_REPORT_SUCCESS, "공지사항이 삭제되었습니다.");
    }

    // 댓글 추가
    @PostMapping("/{boardId}/{noticeId}/comments")
    public ResponseEntity<ApiResponseTemplete<String>> addComment(
            @PathVariable Long boardId, @PathVariable Long noticeId,
            @RequestBody CommentRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {

        // BoardDto를 id로 생성하여 Board 엔티티로 변환
        BoardDto boardDto = new BoardDto(boardId);  // BoardDto 생성
        Board board = boardService.getBoardEntityFromDto(boardDto);  // Board 엔티티로 변환

        if (!boardService.isUserParticipantOrWriter(board, userDetails.getUser())) {
            throw new AccessDeniedException("이 게시판에 참여한 사용자만 댓글을 작성할 수 있습니다.");
        }

        // 댓글 추가
        noticeService.addComment(noticeId, request.getContent(), userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, "댓글이 등록되었습니다.");
    }
}
