package com.project.moyora.app.service;

import com.project.moyora.app.Dto.BoardDto;
import com.project.moyora.app.Dto.BoardListDto;
import com.project.moyora.app.Dto.UserDto;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;

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
                .interestTag(dto.getInterestTag())
                .writer(currentUser)
                .createdTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        return toDto(boardRepository.save(board));
    }


    public List<BoardListDto> getAllBoards(User currentUser) {
        int userAge = currentUser.getAge(); // 동적으로 계산
        GenderType userGender = currentUser.getGender();

        return boardRepository.findAllByOrderByCreatedTimeDesc().stream()
                .filter(board -> userAge >= board.getMinAge() && userAge <= board.getMaxAge())
                .filter(board -> board.getGenderType() == GenderType.OTHER || board.getGenderType() == userGender)
                .map(board -> toListDto(board, currentUser)) // 수정된 부분
                .toList();
    }



    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id) {
        Board board = boardRepository.findBoardById(id);

        // BoardDto 생성
        return new BoardDto(
                board.getId(),
                board.getWriter().getName(),
                board.getTitle(),
                board.getGenderType(),
                board.getMinAge(),
                board.getMaxAge(),
                board.getStartDate(),
                board.getEndDate(),
                board.getInterestTag(),
                board.getContent(),
                board.getHowMany(),
                board.getParticipation(),
                board.getMeetType(),
                board.getMeetDetail(),
                board.getCreatedTime(),
                board.getUpdateTime()
        );
    }

    // BoardService 수정 예시
    public BoardDto updateBoard(Long id, BoardDto dto, User currentUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        checkBoardWriter(board, currentUser);  // 중복 제거

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setStartDate(dto.getStartDate());
        board.setEndDate(dto.getEndDate());
        board.setHowMany(dto.getHowMany());
        board.setGenderType(dto.getGenderType());
        board.setInterestTag(dto.getInterestTag());

        return toDto(boardRepository.save(board));
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
    }

    private BoardDto toDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getWriter().getName(),
                board.getTitle(),
                board.getGenderType(),
                board.getMinAge(),
                board.getMaxAge(),
                board.getStartDate(),
                board.getEndDate(),
                board.getInterestTag(),
                board.getContent(),
                board.getHowMany(),
                board.getParticipation(),
                board.getMeetType(),
                board.getMeetDetail(),
                board.getCreatedTime(),
                board.getUpdateTime()
        );
    }

    public List<BoardListDto> toListDto(List<Board> boards, User currentUser) {
        List<Like> userLikes = likeRepository.findByUserWithBoard(currentUser);
        Set<Long> likedBoardIds = userLikes.stream()
                .map(like -> like.getBoard().getId())
                .collect(Collectors.toSet());

        return boards.stream().map(board -> {
            boolean liked = likedBoardIds.contains(board.getId());

            return new BoardListDto(
                    board.getTitle(),
                    board.getStartDate(),
                    board.getEndDate(),
                    board.getMeetType(),
                    board.getMeetDetail(),
                    board.getInterestTag(),
                    board.getHowMany(),
                    board.getParticipation(),
                    "/boards/" + board.getId(),
                    liked
            );
        }).collect(Collectors.toList());
    }

    public BoardListDto toListDto(Board board, User currentUser) {
        boolean liked = likeRepository.existsByUserAndBoard(currentUser, board);

        return new BoardListDto(
                board.getTitle(),
                board.getStartDate(),
                board.getEndDate(),
                board.getMeetType(),
                board.getMeetDetail(),
                board.getInterestTag(),
                board.getHowMany(),
                board.getParticipation(),
                "/boards/" + board.getId(),
                liked
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
                .interestTag(dto.getInterestTag())
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
}