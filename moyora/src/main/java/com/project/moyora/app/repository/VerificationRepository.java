package com.project.moyora.app.repository;

import com.project.moyora.app.domain.User;
import com.project.moyora.app.domain.Verification;
import com.project.moyora.app.domain.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    List<Verification> findAllByStatus(VerificationStatus status);
    Optional<Verification> findByUser(User user);
    Optional<Verification> findByStatusAndUserEmail(VerificationStatus status, String email);
}
