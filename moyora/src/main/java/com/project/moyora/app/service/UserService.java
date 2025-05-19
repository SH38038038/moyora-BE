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
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.updateInterestTags(tags);
        // save 생략 가능 (변경 감지 = Dirty Checking)
    }
}
