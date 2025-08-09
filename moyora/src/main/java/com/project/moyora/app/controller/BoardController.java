package com.project.moyora.app.controller;

import com.project.moyora.app.domain.*;
import com.project.moyora.app.dto.*;
import com.project.moyora.app.service.ApplicationService;
import com.project.moyora.app.service.BoardSearchService;
import com.project.moyora.app.service.BoardService;
import com.project.moyora.app.service.ChatRoomService;
import com.project.moyora.global.config.AppConfig;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.CustomUserDetails;
import com.project.moyora.global.tag.InterestTag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ApplicationService applicationService;
    private final ChatRoomService chatRoomService;
    private final BoardSearchService boardSearchService;

    @Value("${external.fastapi.url}")
    private String fastApiUrl;

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

        BoardDto created = boardService.createBoard(dto, user, dto.getSub_tags());
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
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long id,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        return ResponseEntity.ok(boardService.getBoardById(id, currentUser));
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

    // ✅ 모집글 확정
    @PutMapping("/{boardId}/confirm")
    public ResponseEntity<ApiResponseTemplete<String>> confirmBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        boardService.confirmBoard(boardId, user);
        boardService.lockParticipantsAfterNoticeCreation(boardId);

        BoardDto boardDto = boardService.getBoardById(boardId, user);
        ChatRoom chatRoom = chatRoomService.createRoomForLockedBoard(boardService.getBoardEntityById(boardId));

        if (boardDto.getMeetType() == MeetType.OFFLINE) {
            RestTemplate restTemplate = new RestTemplate();

            // 👉 FastAPI 요청
            RecommendPlaceRequest request = new RecommendPlaceRequest(boardDto.getTitle(), boardDto.getMeetDetail(), boardDto.getContent());
            RecommendPlaceResponse response = restTemplate.postForObject(
                    fastApiUrl + "/recommend-place-tag", // yml에서 주입받을 변수 사용
                    request,
                    RecommendPlaceResponse.class
            );

            if (response != null && response.getRecommended_query() != null) {
                boardService.saveRecommendedSearchKeyword(boardId, response.getRecommended_query());
            }
        }

        String roomUrl = "/chatroom/" + chatRoom.getId();
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, roomUrl);
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

    // ✅ 모집 글 검색
    @GetMapping("/search")
    public ResponseEntity<List<BoardListDto>> searchBoards(
            BoardSearchRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User currentUser = userDetails.getUser();

        SearchHits<BoardDocument> searchHits = boardSearchService.search(
                request.getTitle(),
                request.getMeetType(),
                request.getInterestTag(),
                currentUser.getAge(),
                currentUser.getGender(),
                request.getPage(),
                request.getSize()
        );

        List<BoardListDto> results = searchHits.getSearchHits().stream()
                .map(hit -> {
                    BoardDocument doc = hit.getContent();

                    return BoardListDto.builder()
                            .detailId(doc.getId())
                            .title(doc.getTitle())
                            .endDate(doc.getEndDate())
                            .meetType(doc.getMeetType())
                            .meetDetail(doc.getMeetDetail())
                            .tags(convertTags(doc.getTags()))
                            .howMany(doc.getHowMany())
                            .participation(doc.getParticipation())
                            .confirmed(doc.isConfirmed())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(results);
    }


    private List<TagDto> convertTags(List<InterestTag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(TagDto::from)
                .toList();
    }

}

