package com.project.moyora.app.service;

import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuspensionReleaseService {

    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 매 시간 0분에 실행
    public void releaseExpiredSuspensions() {
        LocalDateTime now = LocalDateTime.now();

        List<User> suspendedUsers = userRepository.findAllBySuspendedUntilBefore(now);

        for (User user : suspendedUsers) {
            // 정지 기간 만료 사용자에 대해 정지 정보 초기화
            user.setSuspendedUntil(null);
            user.setSuspensionPeriod(null);
            userRepository.save(user);
        }
    }
}

