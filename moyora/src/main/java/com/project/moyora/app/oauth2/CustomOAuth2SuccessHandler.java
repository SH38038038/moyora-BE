package com.project.moyora.app.oauth2;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.RoleType;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Kakao는 사용자 정보가 nested 되어 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String name = (String) kakaoAccount.get("name");
        String genderStr = (String) kakaoAccount.get("gender");
        String birthday = (String) kakaoAccount.get("birthday"); // MMDD
        String birthyear = (String) kakaoAccount.get("birthyear");

        // Gender 파싱
        final GenderType gender;
        if (genderStr != null) {
            switch (genderStr.toLowerCase()) {
                case "male":
                    gender = GenderType.MALE;
                    break;
                case "female":
                    gender = GenderType.FEMALE;
                    break;
                default:
                    gender = GenderType.OTHER;
            }
        } else {
            gender = GenderType.OTHER;  // 기본값 설정
        }

        // 생일 파싱
        final LocalDate birth = (birthyear != null && birthday != null)
                ? LocalDate.parse(birthyear + birthday, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : null;

        // DB에서 사용자 조회 또는 새로 저장
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> {
                            // 이미 존재하면 사용자 정보 업데이트
                            log.info("User already exists with email: {}", email);
                            user.setName(name != null ? name : nickname);
                            user.setGender(gender);
                            user.setBirth(birth);
                            user.setRoleType(RoleType.USER);
                            userRepository.save(user);  // 업데이트
                        },
                        () -> {
                            // 새로 저장할 경우
                            log.info("Saving new user with email: {}", email);
                            userRepository.save(User.builder()
                                    .email(email)
                                    .name(name != null ? name : nickname)
                                    .gender(gender)
                                    .birth(birth)
                                    .roleType(RoleType.USER)
                                    .verified(false)
                                    .build());
                        });

        // JWT 토큰 생성
        User user = userRepository.findByEmail(email).orElseThrow();
        String accessToken = tokenService.createAccessToken(user);

        // 리프레시 토큰을 DB에 저장
        String refreshToken = tokenService.createRefreshToken();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 1) LinkedHashMap으로 순서 보장
        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("accessToken", accessToken);

        // 2) ApiResponseTemplete<ResponseData> 객체 생성
        ResponseEntity<ApiResponseTemplete<Map<String, String>>> respEntity =
                ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, data);

        ApiResponseTemplete<Map<String, String>> apiResponse = respEntity.getBody();

        // 3) 응답 인코딩 설정 (한글 깨짐 방지)
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 4) JSON으로 출력
        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }
}
