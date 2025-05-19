package com.project.moyora.app.service;

import com.project.moyora.app.Dto.VerificationResponse;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.domain.Verification;
import com.project.moyora.app.domain.VerificationStatus;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.app.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private UserRepository userRepository;

    // 인증 요청 리스트 조회 (PENDING 상태만)
    @Transactional
    public List<VerificationResponse> getPendingVerifications() {
        List<Verification> verifications = verificationRepository.findAllByStatus(VerificationStatus.PENDING);
        return verifications.stream()
                .map(v -> new VerificationResponse(v.getId(), v.getUser().getEmail(), v.getUser().getIdCardUrl(),
                        v.getUser().getBirth(), v.getUser().getGender(), v.getStatus(),
                        "/api/verification/details/" + v.getId())) // 세부 조회 URL 추가
                .collect(Collectors.toList());
    }

    // 인증 요청 세부 조회
    @Transactional
    public VerificationResponse getVerificationDetails(Long id) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인증 요청을 찾을 수 없습니다."));

        return new VerificationResponse(verification.getId(), verification.getUser().getEmail(), verification.getUser().getIdCardUrl(),
                verification.getUser().getBirth(), verification.getUser().getGender(), verification.getStatus(),"/api/verification/details/" + verification.getId());
    }

    // 인증 수락
    @Transactional
    public void acceptVerification(Long id) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인증 요청을 찾을 수 없습니다."));

        // 2. Verification 상태 변경
        verification.setStatus(VerificationStatus.ACCEPTED);

        // 3. User 정보 업데이트
        User user = verification.getUser();
        user.setVerified(true); // User의 verified 필드를 true로 설정
        user.setVerificationStatus(VerificationStatus.ACCEPTED);
        // 4. Verification, User 업데이트
        verificationRepository.save(verification);  // Verification 상태 저장
        userRepository.save(user);  // User의 verified 필드 저장
    }

    // 인증 거절
    @Transactional
    public void rejectVerification(Long id, String reason) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인증 요청을 찾을 수 없습니다."));

        verification.setStatus(VerificationStatus.REJECTED);
        verification.setReason(reason);

        User user = verification.getUser();
        user.setVerified(false); // User의 verified 필드를 true로 설정
        user.setVerificationStatus(VerificationStatus.REJECTED);
        verificationRepository.save(verification);
        userRepository.save(user);
    }

    // 인증 요청 삭제
    @Transactional
    public void deleteVerification(Long id) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인증 요청을 찾을 수 없습니다."));

        verificationRepository.delete(verification);
    }
}
