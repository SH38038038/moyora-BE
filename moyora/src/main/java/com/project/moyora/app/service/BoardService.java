package com.project.moyora.app.service;

import com.project.moyora.app.dto.*;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.*;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.ResourceNotFoundException;
import com.project.moyora.global.exception.model.CustomException;
import com.project.moyora.global.tag.InterestTag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final BoardApplicationRepository boardApplicationRepository;
    private final ReportRepository reportRepository;
    private final BoardSearchService boardSearchService;
    private final UserSubTagRepository userSubTagRepository;
/*
    public BoardDto createBoard(BoardDto dto, User currentUser) {
        if (!Boolean.TRUE.equals(currentUser.getVerified())) {
            throw new AccessDeniedException("인증된 사용자만 글을 작성할 수 있습니다.");
        }

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .howMany(dto.getHowMany())
                .participation(0)
                .meetType(dto.getMeetType())
                .meetDetail(dto.getMeetDetail())
                .genderType(dto.getGenderType())
                .minAge(dto.getMinAge())
                .maxAge(dto.getMaxAge())
                .tags(dto.getTags().stream()
                        .map(tagDto -> InterestTag.fromName(tagDto.getName())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagDto.getName())))
                        .collect(Collectors.toList()))

                .writer(currentUser)
                .createdTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        Board savedBoard = boardRepository.save(board);

        BoardApplication application = BoardApplication.builder()
                .board(savedBoard)
                .applicant(currentUser)
                .status(ApplicationStatus.LOCKED)
                .build();
        boardApplicationRepository.save(application);

        boardSearchService.indexBoard(board); // elastic search 연동

        return new BoardDto(savedBoard, application);  // ✅ userStatus 포함
    }
*/

    public BoardDto createBoard(BoardDto dto, User currentUser, List<String> subTagNames) {
        if (!Boolean.TRUE.equals(currentUser.getVerified())) {
            throw new AccessDeniedException("인증된 사용자만 글을 작성할 수 있습니다.");
        }

        // ✅ 1. Python 서버로 게시글 내용 전달
        List<String> subTags = fetchSubTagsFromPython(dto.getTitle(), dto.getContent());

        // ✅ 2. 대분류 태그 enum 매핑
        List<InterestTag> majorTags = dto.getTags().stream()
                .map(tagDto -> InterestTag.fromName(tagDto.getName())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagDto.getName())))
                .collect(Collectors.toList());

        // ✅ 3. Board 생성 및 저장
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .howMany(dto.getHowMany())
                .participation(0)
                .meetType(dto.getMeetType())
                .meetDetail(dto.getMeetDetail())
                .genderType(dto.getGenderType())
                .minAge(dto.getMinAge())
                .maxAge(dto.getMaxAge())
                .tags(majorTags)
                .writer(currentUser)
                .createdTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        List<SubTag> subTagEntities = subTags.stream()
                .map(name -> SubTag.builder()
                        .name(name)
                        .board(board)  // 양방향 연관관계라면 반드시 설정
                        .build())
                .collect(Collectors.toList());

        board.setSubTags(subTagEntities);

        Board savedBoard = boardRepository.save(board);
        // 신청 엔티티 생성
        BoardApplication application = BoardApplication.builder()
                .board(savedBoard)
                .applicant(currentUser)
                .status(ApplicationStatus.LOCKED)
                .build();
        boardApplicationRepository.save(application);

        boardSearchService.indexBoard(board); // ElasticSearch 연동

        for (SubTag subTag : savedBoard.getSubTags()) {
            UserSubTag userSubTag = UserSubTag.builder()
                    .user(currentUser)
                    .subTag(subTag)
                    .category(Category.WRITTEN)
                    .build();
            userSubTagRepository.save(userSubTag);
        }


        return new BoardDto(savedBoard, application); // userStatus 포함
    }


    private List<String> fetchSubTagsFromPython(String title, String content) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("title", title);
            requestBody.put("content", content);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            String pythonUrl = "https://a533f9c5e423.ngrok-free.app/tagging";
            ResponseEntity<SubTagResponse> response = restTemplate.postForEntity(
                    pythonUrl, request, SubTagResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getTags();
            }
        } catch (Exception e) {
            log.error("파이썬 서버 태그 추출 실패", e);
        }
        return Collections.emptyList(); // 실패 시 빈 리스트
    }


    public List<BoardListDto> getAllBoards(User currentUser) {
        int userAge = currentUser.getAge(); // 동적으로 계산
        GenderType userGender = currentUser.getGender();

        return boardRepository.findAllByOrderByCreatedTimeDesc().stream()
                .filter(board -> !board.isConfirmed()) // ✅ confirmed == true 인 것 제외
                .filter(board -> userAge >= board.getMinAge() && userAge <= board.getMaxAge())
                .filter(board -> board.getGenderType() == GenderType.OTHER || board.getGenderType() == userGender)
                .map(board -> toListDto(board, currentUser))
                .toList();
    }



    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id, User currentUser) {
        Board board = boardRepository.findBoardById(id);

        List<TagDto> tagDtos = board.getTags().stream()
                .map(tag -> new TagDto(tag.name(), tag.getDisplayName()))
                .collect(Collectors.toList());

        BoardApplication application = boardApplicationRepository
                .findByBoardIdAndApplicantId(board.getId(), currentUser.getId())
                .orElse(null);
        return new BoardDto(board, application);
    }

    // BoardDto를 Board 엔티티로 변환하는 메서드
    public Board getBoardEntityFromDto(BoardDto boardDto) {
        return boardRepository.findById(boardDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
    }

    // 참가자 확인
    public boolean isUserParticipantOrWriter(Board board, User user) {
        boolean isAcceptedParticipant = boardApplicationRepository.existsByBoardAndApplicantAndStatus(board, user, ApplicationStatus.LOCKED);
        boolean isWriter = board.getWriter().equals(user);
        return isAcceptedParticipant || isWriter;
    }

    public void confirmBoard(Long boardId, User writer) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("게시판을 찾을 수 없습니다."));

        if (!board.getWriter().equals(writer)) {
            throw new AccessDeniedException("게시판 작성자만 확정을 할 수 있습니다.");
        }

        if (board.getParticipation() < board.getHowMany()) {
            throw new AccessDeniedException("참여 인원이 부족하여 확정을 할 수 없습니다.");
        }

        board.setConfirmed(true);  // 확정 상태로 설정
        boardRepository.save(board);
    }

    @Transactional
    public void lockParticipantsAfterNoticeCreation(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("게시판을 찾을 수 없습니다."));

        List<BoardApplication> applications = boardApplicationRepository.findByBoard(board);
        for (BoardApplication application : applications) {
            if (application.getStatus() == ApplicationStatus.ACCEPTED) {
                application.setStatus(ApplicationStatus.LOCKED);
            }
        }
        // saveAll 호출은 필요에 따라 추가
    }


    // BoardService 수정 예시
    public BoardDto updateBoard(Long id, BoardDto dto, User currentUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        checkBoardWriter(board, currentUser);

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setStartDate(dto.getStartDate());
        board.setEndDate(dto.getEndDate());
        board.setHowMany(dto.getHowMany());
        board.setGenderType(dto.getGenderType());

        board.setTags(
                dto.getTags().stream()
                        .map(tagDto -> InterestTag.fromName(tagDto.getName())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagDto.getName())))
                        .collect(Collectors.toList())
        );

        Board updatedBoard = boardRepository.save(board);

        BoardApplication application = boardApplicationRepository
                .findByBoardIdAndApplicantId(updatedBoard.getId(), currentUser.getId())
                .orElse(null);

        return new BoardDto(updatedBoard, application);
    }


    private void checkBoardWriter(Board board, User currentUser) {
        if (!board.getWriter().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the author of this post");
        }
    }


    public void deleteBoard(Long id, User currentUser) throws AccessDeniedException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        if (!board.getWriter().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the author of this post");
        }

        boardRepository.delete(board);

        userSubTagRepository.deleteByUserAndCategory(currentUser, Category.WRITTEN);
    }

    private BoardDto toDto(Board board, User currentUser) {
        List<TagDto> tagDtos = board.getTags().stream()
                .map(tag -> new TagDto(tag.name(), tag.getDisplayName()))
                .collect(Collectors.toList());
        BoardApplication application = boardApplicationRepository
                .findByBoardIdAndApplicantId(board.getId(), currentUser.getId())
                .orElse(null);

        return new BoardDto(board, application);
    }

    public List<BoardListDto> toListDto(List<Board> boards, User currentUser) {
        List<Like> userLikes = likeRepository.findByUserWithBoard(currentUser);
        Set<Long> likedBoardIds = userLikes.stream()
                .map(like -> like.getBoard().getId())
                .collect(Collectors.toSet());

        return boards.stream().map(board -> {
            boolean liked = likedBoardIds.contains(board.getId());

            List<TagDto> tagDtos = board.getTags().stream()
                    .map(tag -> new TagDto(tag.name(), tag.getDisplayName()))
                    .collect(Collectors.toList());

            // BoardListDto 생성 부분에서 interestTag → tagDtos로만 사용
            List<String> subTagNames = board.getSubTags().stream()
                    .map(SubTag::getName)
                    .collect(Collectors.toList());

            return new BoardListDto(
                    board.getTitle(),
                    board.getStartDate(),
                    board.getEndDate(),
                    board.getMeetType(),
                    board.getMeetDetail(),
                    tagDtos,
                    subTagNames,  // 이름 리스트 전달
                    board.getHowMany(),
                    board.getParticipation(),
                    board.getId(),
                    liked,
                    board.isConfirmed()
            );


        }).collect(Collectors.toList());
    }


    public BoardListDto toListDto(Board board, User currentUser) {
        boolean liked = likeRepository.existsByUserAndBoard(currentUser, board);

        // board.getTags()가 List<Tag> 타입이고, Tag 엔티티가 section, name, displayName 필드가 있다고 가정
        List<TagDto> tagDtos = board.getTags().stream()
                .map(tag -> new TagDto(tag.name(), tag.getDisplayName()))
                .collect(Collectors.toList());

        List<String> subTagNames = board.getSubTags().stream()
                .map(SubTag::getName)
                .collect(Collectors.toList());

        return new BoardListDto(
                board.getTitle(),
                board.getStartDate(),
                board.getEndDate(),
                board.getMeetType(),
                board.getMeetDetail(),
                tagDtos,
                subTagNames,  // 이름 리스트 전달
                board.getHowMany(),
                board.getParticipation(),
                board.getId(),
                liked,
                board.isConfirmed()
        );

    }





    private Board toEntity(BoardDto dto) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .howMany(dto.getHowMany())
                .genderType(dto.getGenderType())
                .tags(dto.getTags().stream()
                        .map(tagDto -> InterestTag.fromName(
                                        tagDto.getName())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagDto)))
                        .collect(Collectors.toList()))
                .build();

    }

    private List<UserDto> mapUsersByStatus(Board board, ApplicationStatus status) {
        // getApplications()가 null인 경우 빈 리스트 반환
        if (board.getApplications() == null) {
            return new ArrayList<>();
        }

        return board.getApplications().stream()
                .filter(app -> app.getStatus() == status)
                .map(app -> {
                    User user = app.getApplicant();
                    return UserDto.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .birth(user.getBirth())
                            .gender(user.getGender())
                            .verified(user.getVerified())
                            .build();
                })
                .toList();
    }

    @Transactional
    public void deleteBoardIfExpiredOrReported(Long boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            log.warn("게시글 ID {} 는 존재하지 않음", boardId);
            return;
        }

        LocalDate endDate = board.getEndDate();
        LocalDateTime now = LocalDateTime.now();

        boolean isExpired = endDate != null && now.isAfter(endDate.plusDays(1).atStartOfDay());
        boolean isReported = reportRepository.existsByReportTypeAndReportedBoardAndStatus(
                ReportType.POST, board, ReportStatus.ACCEPTED);

        if (isExpired || isReported) {
            // 연관된 BoardApplication, Notice 모두 cascade로 삭제됨
            boardRepository.delete(board);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행 예시
    public void scheduledBoardCleanup() {
        List<Board> boards = boardRepository.findAll();

        for (Board board : boards) {
            deleteBoardIfExpiredOrReported(board.getId());
        }
    }

    public List<BoardListDto> searchBoards(BoardSearchRequest request, User currentUser) {
        String keyword = request.getTitle();
        if (keyword == null || keyword.isBlank()) {
            keyword = null;
        }

        List<Board> boards = boardRepository.searchBoardsWithUserTags(
                keyword,
                request.getInterestTag(),
                request.getMeetType(),
                request.getMeetDetail()
        );

        int userAge = currentUser.getAge();
        GenderType userGender = currentUser.getGender();

        List<Board> filtered = boards.stream()
                .filter(board -> !board.isConfirmed()) // ✅ confirmed == true 인 것 제외
                .filter(board -> userAge >= board.getMinAge() && userAge <= board.getMaxAge())
                .filter(board -> board.getGenderType() == GenderType.OTHER || board.getGenderType() == userGender)
                .toList();

        return toListDto(filtered, currentUser); // 👍 좋아요 정보 포함된 BoardListDto로 반환
    }

    @Transactional(readOnly = true)
    public Board getBoardEntityById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND, null));
    }


}