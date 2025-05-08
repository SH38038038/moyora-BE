package com.project.moyora;

import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.app.repository.VerificationRepository;
import com.project.moyora.global.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Rollback(false)
class MoyoraApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private VerificationRepository verificationRepository;

	@Test
	void createTenUsersTest() {
		for (int i = 1; i <= 10; i++) {
			// User 생성
			User user = User.builder()
					.name("사용자" + i)
					.email("user" + i + "@example.com")
					.gender(i % 2 == 0 ? GenderType.MALE : GenderType.FEMALE)
					.birth(LocalDate.of(1990 + i % 5, i % 12 + 1, i % 28 + 1))
					.idCardUrl("https://example.com/idcard" + i + ".jpg")
					.verified(false)
					.verificationStatus(VerificationStatus.PENDING)
					.roleType(RoleType.USER)
					.deletedAt(null)
					.refreshToken(tokenService.createRefreshToken())
					.build();

			// User 저장
			userRepository.save(user);

			// Verification 생성 및 연결
			Verification verification = Verification.builder()
					.user(user) // 방금 생성한 User와 연결
					.status(VerificationStatus.PENDING) // 기본 상태로 PENDING
					.reason("Verification pending") // 이유 (거절 시 이유)
					.createdAt(LocalDateTime.now()) // 생성 시간
					.updatedAt(LocalDateTime.now()) // 업데이트 시간
					.build();

			// Verification 저장
			verificationRepository.save(verification);
		}
	}
}
