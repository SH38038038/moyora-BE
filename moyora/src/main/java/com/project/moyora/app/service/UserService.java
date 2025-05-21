package com.project.moyora.app.service;

import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateInterestTags(Principal principal, Set<InterestTag> tags) {
        // 여기가 핵심: 저장될 태그 개수를 검증
        if (tags.size() < 3 || tags.size() > 10) {
            throw new IllegalArgumentException("관심 태그는 최소 3개, 최대 10개 저장해야 합니다.");
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.updateInterestTags(tags); // 여기서 기존 관심 태그 clear 후 새로 저장
    }

}
