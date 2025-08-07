package com.project.moyora.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.moyora.app.domain.SubTag;
import com.project.moyora.app.dto.SubTagDto;
import com.project.moyora.app.repository.SubTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagCacheService {

    private static final String POPULAR_TAGS_KEY = "popular_sub_tags";

    @Qualifier("objectRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final SubTagRepository subTagRepository;
    private final ObjectMapper objectMapper;

    // 인기 소분류 태그 조회 (캐시 우선)
    public List<SubTagDto> getCachedPopularSubTags() {
        Object cached = redisTemplate.opsForValue().get(POPULAR_TAGS_KEY);

        if (cached == null) {
            List<SubTagDto> popularSubTags = fetchPopularSubTagsFromDB();
            redisTemplate.opsForValue().set(POPULAR_TAGS_KEY, popularSubTags, Duration.ofHours(1));  // 1시간 TTL
            return popularSubTags;
        } else {
            return objectMapper.convertValue(
                    cached,
                    new TypeReference<List<SubTagDto>>() {}
            );
        }
    }

    // DB에서 인기 소분류 태그 조회
    private List<SubTagDto> fetchPopularSubTagsFromDB() {
        List<Object[]> results = subTagRepository.findTopSubTags(PageRequest.of(0, 8)); // 상위 8개

        return results.stream()
                .map(obj -> {
                    String name = (String) obj[0];
                    Long count = (Long) obj[1]; // 사용 빈도 (필요하면 DTO에 포함 가능)
                    return new SubTagDto(name); // id가 없으므로 null 또는 다른 값 넣기
                })
                .collect(Collectors.toList());
    }


    // 캐시 강제 갱신
    public void refreshPopularSubTagsCache() {
        List<SubTagDto> popularSubTags = fetchPopularSubTagsFromDB();
        redisTemplate.opsForValue().set(POPULAR_TAGS_KEY, popularSubTags, Duration.ofHours(1));
    }
}
