package com.project.moyora.app.service;


import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.RoleType;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    public Map<String, String> kakaoLogin(String kakaoAccessToken) {
        // 1. 카카오 사용자 정보 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> kakaoInfo = response.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String name = kakaoAccount.get("name") != null
                ? (String) kakaoAccount.get("name")
                : (String) profile.get("nickname");
        String genderStr = (String) kakaoAccount.get("gender");
        String birthday = (String) kakaoAccount.get("birthday");
        String birthyear = (String) kakaoAccount.get("birthyear");

        // 성별 파싱
        final GenderType parsedGender;
        if (genderStr != null) {
            switch (genderStr.toLowerCase()) {
                case "male" -> parsedGender = GenderType.MALE;
                case "female" -> parsedGender = GenderType.FEMALE;
                default -> parsedGender = GenderType.OTHER;
            }
        } else {
            parsedGender = GenderType.OTHER;
        }

        // 생일 파싱
        final LocalDate parsedBirth = (birthyear != null && birthday != null)
                ? LocalDate.parse(birthyear + birthday, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : null;


        // DB 저장 또는 업데이트
        User user = userRepository.findByEmail(email)
                .map(u -> {
                    u.setName(name);
                    u.setGender(parsedGender);
                    u.setBirth(parsedBirth);
                    u.setRoleType(RoleType.USER);
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .gender(parsedGender)
                        .birth(parsedBirth)
                        .roleType(RoleType.USER)
                        .verified(false)
                        .build()));

        // 토큰 발급
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 응답 데이터 구성
        Map<String, String> result = new LinkedHashMap<>();
        result.put("userId", String.valueOf(user.getId()));
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);

        return result;
    }
}
