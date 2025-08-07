package com.project.moyora.app.service;

import com.project.moyora.app.domain.SubTag;
import com.project.moyora.app.dto.BoardListDto;
import com.project.moyora.app.dto.HomeResponseDto;
import com.project.moyora.app.dto.SubTagDto;
import com.project.moyora.app.dto.TagDto;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.LikeRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.app.repository.UserSubTagRepository;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    private final LikeRepository likeRepository;
    private final UserSubTagRepository userSubTagRepository;

    @Transactional(readOnly = true)
    public HomeResponseDto getHomeData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        Set<SubTag> userSubTags = new HashSet<>(userSubTagRepository.findDistinctSubTagsByUser(user));

        Page<Board> recommendedBoardsPage;
        if (userSubTags.isEmpty()) {
            recommendedBoardsPage = boardRepository.findRandomBoardsExcludingUser(user.getId(), PageRequest.of(0, 4));
        } else {
            recommendedBoardsPage = boardRepository.findRecommendedBoardsByUserSubTagsExcludingUserApplications(
                    userSubTags, user.getId(), PageRequest.of(0, 4)
            );

            if (recommendedBoardsPage.isEmpty()) {
                recommendedBoardsPage = boardRepository.findRandomBoardsExcludingUser(user.getId(), PageRequest.of(0, 4));
            }
        }


        List<BoardListDto> recommendedBoardDtos = recommendedBoardsPage.getContent().stream()
                .map(board -> BoardListDto.from(board, user, likeRepository))
                .collect(Collectors.toList());

        List<SubTagDto> popularTags = tagCacheService.getCachedPopularSubTags();
        log.info("popularSubTags.size = {}", popularTags.size());
        popularTags.forEach(t -> log.info("popularSubTag: name={}", t.getName()));

        return HomeResponseDto.builder()
                .recommendedBoards(recommendedBoardDtos)
                .popularTags(popularTags)
                .build();
    }
}
