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
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MoyoraApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    void generateTestUsersByAgeAndGender() {
        int currentYear = LocalDate.now().getYear();
        int[] ageGroups = {20, 30, 40, 50};

        for (int age : ageGroups) {
            int birthYear = currentYear - (age + 5); // 중간 나이 기준 (25세, 35세 등)

            for (GenderType gender : List.of(GenderType.MALE, GenderType.FEMALE)) {
                for (int i = 1; i <= 2; i++) {
                    String genderCode = gender == GenderType.MALE ? "m" : "f";

                    User user = User.builder()
                            .name(age + "대_" + genderCode + i)
                            .email("user" + age + genderCode + i + "@ex.com")
                            .birth(LocalDate.of(birthYear, 1, 1))
                            .gender(gender)
                            .roleType(RoleType.USER)
                            .build();

                    String refreshToken = tokenService.createRefreshToken();
                    user.setRefreshToken(refreshToken);

                    userRepository.save(user);
                }
            }
        }
    }
}
