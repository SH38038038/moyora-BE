package com.project.moyora.app.controller;

import com.project.moyora.app.domain.SuspensionPeriod;
import com.project.moyora.app.dto.LoginRequestDto;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.exception.model.CustomException;
import com.project.moyora.global.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    public LoginController(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseTemplete<Map<String, String>>> login(@RequestBody LoginRequestDto loginRequest) {
        // 이메일로 사용자 찾기
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        // 사용자가 존재하지 않으면 404 반환
        if (userOpt.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.USER_NOT_FOUND, null);
        }

        User user = userOpt.get();

        LocalDateTime now = LocalDateTime.now();
        if (user.getSuspendedUntil() != null && user.getSuspendedUntil().isAfter(now)) {
            if (user.getSuspensionPeriod() == SuspensionPeriod.PERMANENT) {
                throw new CustomException(ErrorCode.USER_SUSPENDED, "영구 정지된 사용자입니다. 로그인할 수 없습니다.");
            }
        }

        // accessToken 생성
        String accessToken = tokenService.createAccessToken(user);

        // 필요한 데이터만 포함해서 반환
        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("userId", String.valueOf(user.getId()));
        responseData.put("accessToken", accessToken);

        // 성공적인 응답 반환
        return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, responseData);
    }
}
