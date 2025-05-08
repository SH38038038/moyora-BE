package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.BoardListDto;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.Like;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.LikeRepository;
import com.project.moyora.app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MyPageController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public MyPageController(BoardRepository boardRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    // 찜 추가
    @PostMapping("/boards/{boardId}/liked/{userId}")
    public ResponseEntity<String> likeBoard(@PathVariable Long userId, @PathVariable Long boardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 이미 찜한 게시물이 있는지 확인
        Optional<Like> existingLike = likeRepository.findByUserAndBoard(user, board);
        if (existingLike.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이미 찜한 게시물입니다.");
        }

        // 찜 추가
        Like like = new Like();
        like.setUser(user);
        like.setBoard(board);
        likeRepository.save(like);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시물을 찜했습니다.");
    }

    // 찜 취소
    @DeleteMapping("/boards/{boardId}/liked/{userId}")
    public ResponseEntity<String> removeLike(@PathVariable Long userId, @PathVariable Long boardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 찜한 게시물이 있는지 확인
        Like like = likeRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new IllegalArgumentException("찜한 게시물이 아닙니다."));

        likeRepository.delete(like); // 찜 취소

        return ResponseEntity.ok("게시물 찜을 취소했습니다.");
    }

    // 찜한 게시물 조회
    @GetMapping("/mypage/{userId}/liked")
    public ResponseEntity<List<BoardListDto>> getLikedBoards(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Like> likes = likeRepository.findByUserWithBoard(user);
        List<BoardListDto> likedBoards = likes.stream()
                .map(like -> {
                    Board board = like.getBoard();
                    return new BoardListDto(
                            board.getTitle(),
                            board.getStartDate(),
                            board.getEndDate(),
                            board.getMeetType(),
                            board.getMeetDetail(),
                            board.getInterestTag(),
                            board.getHowMany(),
                            board.getParticipation(),
                            "/api/boards/" + board.getId(),  // detailUrl 구성 방식 예시,
                            true
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(likedBoards);
    }

}
