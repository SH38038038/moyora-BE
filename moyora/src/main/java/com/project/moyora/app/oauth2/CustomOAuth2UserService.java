package com.project.moyora.app.oauth2;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo userInfo = extractUserInfo(oAuth2User.getAttributes());

        // 카카오 로그인 처리
        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());
        if (existingUser.isPresent()) {
            return new CustomUserDetails(existingUser.get(), oAuth2User.getAttributes());
        } else {
            User newUser = createNewUser(userInfo);
            userRepository.save(newUser);
            return new CustomUserDetails(newUser, oAuth2User.getAttributes());
        }
    }

    private OAuth2UserInfo extractUserInfo(Map<String, Object> attributes) {
        return new KakaoUserInfo(attributes);
    }

    private User createNewUser(OAuth2UserInfo userInfo) {
        return User.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .gender(GenderType.OTHER) // 카카오는 성별 정보를 제공하지 않음
                .verified(true)
                .build();
    }
}

