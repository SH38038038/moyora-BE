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


}
