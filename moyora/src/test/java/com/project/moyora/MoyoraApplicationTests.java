package com.project.moyora;

import com.nimbusds.jose.util.Pair;
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
import java.util.List;

@SpringBootTest
class MoyoraApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    void generateTestUsersBySpecificAgeAndGender() {


        // ✅ 관리자 계정 생성
        if (userRepository.findByEmail("admin@ex.com").isEmpty()) {
            User admin = User.builder()
                    .name("admin")
                    .email("admin@ex.com")
                    .birth(LocalDate.of(2000, 1, 1))
                    .gender(GenderType.FEMALE)
                    .roleType(RoleType.ADMIN)
                    .verified(true)
                    .verificationStatus(VerificationStatus.ACCEPTED)
                    .idCardUrl("1")
                    .build();

            admin.setRefreshToken(tokenService.createRefreshToken());

            userRepository.save(admin);
        }
    }
}
