package com.project.moyora.app.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.domain.Verification;
import com.project.moyora.app.domain.VerificationStatus;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.app.repository.VerificationRepository;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.SuccessCode;
import com.project.moyora.global.exception.model.ApiResponseTemplete;
import com.project.moyora.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Tag(name = "신분증 이미지 업로드", description = "로그인한 사용자의 신분증 이미지를 업로드하고 URL을 저장합니다.")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    @Value("${imgbb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final TokenService tokenService; // JWT로부터 사용자 추출

    @Operation(
            summary = "신분증 이미지 업로드",
            description = "이미지를 업로드하고, 로그인한 사용자의 idcardUrl에 저장합니다."
    )
    @PostMapping(value = "/upload/idcard", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponseTemplete<String>> uploadIdCardImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("image") MultipartFile file) {

        try {
            // JWT에서 사용자 정보 추출
            String accessToken = authHeader.replace("Bearer ", "");
            String userEmail = tokenService.extractEmail(accessToken)
                    .orElseThrow(() -> new RuntimeException("JWT에서 이메일을 추출할 수 없습니다."));

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));


            // 이미지 업로드 요청
            String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            JsonNode responseBody = new ObjectMapper().readTree(response.getBody());
            String imageUrl = responseBody.path("data").path("url").asText();

            // 사용자 정보 업데이트
            user.setIdCardUrl(imageUrl);
            user.setVerificationStatus(VerificationStatus.PENDING);
            userRepository.save(user);

            Verification verification = new Verification();
            verification.setUser(user);
            verification.setStatus(VerificationStatus.PENDING);
            verification.setCreatedAt(LocalDateTime.now());
            verificationRepository.save(verification);

            return ApiResponseTemplete.success(SuccessCode.IMAGE_SERVER_SUCCESS, imageUrl);

        } catch (IOException e) {
            return ApiResponseTemplete.error(ErrorCode.IMAGE_SERVER_ERROR, null);
        } catch (RestClientException e) {
            return ApiResponseTemplete.error(ErrorCode.IMAGE_UPLOAD_ERROR, null);
        } catch (Exception e) {
            return ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, null);
        }
    }
}
