package com.project.moyora.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.moyora.app.Dto.TagDto;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagCacheService {

    private static final String POPULAR_TAGS_KEY = "popular_tags";

    @Qualifier("objectRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final BoardRepository boardRepository;
    private final ObjectMapper objectMapper;

    // 인기 태그 조회 (캐시 우선)
    public List<TagDto> getCachedPopularTags() {
        Object cached = redisTemplate.opsForValue().get(POPULAR_TAGS_KEY);

        if (cached == null) {
            List<TagDto> popularTags = fetchPopularTagsFromDB();
            redisTemplate.opsForValue().set(POPULAR_TAGS_KEY, popularTags, Duration.ofHours(1));  // 1시간 TTL
            return popularTags;
        } else {
            // Redis에서 꺼낸 Object를 List<TagDto>로 변환
            List<TagDto> popularTags = objectMapper.convertValue(
                    cached,
                    new TypeReference<List<TagDto>>() {}
            );
            return popularTags;
        }
    }

    // DB에서 인기 태그 조회
    private List<TagDto> fetchPopularTagsFromDB() {
        List<String> popularTagStrings = boardRepository.findPopularTags(8); // String 리스트 반환
        return popularTagStrings.stream()
                .map(InterestTag::valueOf) // String → InterestTag 변환
                .map(TagDto::from)          // InterestTag → TagDto 변환
                .collect(Collectors.toList());
    }

    // 캐시 강제 갱신용 (예: 스케줄러에서 호출)
    public void refreshPopularTagsCache() {
        List<TagDto> popularTags = fetchPopularTagsFromDB();
        redisTemplate.opsForValue().set(POPULAR_TAGS_KEY, popularTags, Duration.ofHours(1));
    }
}
