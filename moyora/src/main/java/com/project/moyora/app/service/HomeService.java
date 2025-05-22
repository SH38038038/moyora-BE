package com.project.moyora.app.service;

import com.project.moyora.app.Dto.BoardListDto;
import com.project.moyora.app.Dto.HomeResponseDto;
import com.project.moyora.app.Dto.TagDto;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TagCacheService tagCacheService;


    public HomeResponseDto getHomeData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        Set<InterestTag> interestTags = user.getInterestTags();

        Page<Board> recommendedBoardsPage;

        if (interestTags == null || interestTags.isEmpty()) {
            // 관심 태그가 없는 경우 랜덤 게시글 4개
            recommendedBoardsPage = boardRepository.findRandomBoards(PageRequest.of(0, 4));
        } else {
            // 관심 태그 기반으로 게시글 조회
            recommendedBoardsPage = boardRepository.findDistinctByTagsInOrderByCreatedTimeDesc(
                    interestTags,
                    PageRequest.of(0, 4)
            );

            // 관심 태그가 있지만 관련 게시글이 없으면 랜덤 게시글 4개
            if (recommendedBoardsPage.isEmpty()) {
                recommendedBoardsPage = boardRepository.findRandomBoards(PageRequest.of(0, 4));
            }
        }

        List<BoardListDto> recommendedBoardDtos = recommendedBoardsPage.stream()
                .map(BoardListDto::from)
                .collect(Collectors.toList());

        // 인기 태그 (Redis)
        List<TagDto> popularTags = tagCacheService.getCachedPopularTags();
        log.info("popularTags.size = {}", popularTags.size());
        popularTags.forEach(t -> log.info("popularTag: name={}, displayName={}", t.getName(), t.getDisplayName()));


        return HomeResponseDto.builder()
                .recommendedBoards(recommendedBoardDtos)
                .popularTags(popularTags)
                .build();
    }

}
