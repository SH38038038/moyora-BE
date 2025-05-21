package com.project.moyora.app.domain;

import com.project.moyora.global.tag.InterestTag;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private GenderType gender;

    private LocalDate birth;

    private String idCardUrl;

    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private RoleType roleType = RoleType.USER;

    private LocalDateTime deletedAt;

    private String refreshToken;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = InterestTag.class)
    @CollectionTable(name = "user_interest_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_tag")
    private Set<InterestTag> interestTags = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // 찜한 게시물 목록

    @Enumerated(EnumType.STRING)
    private SuspensionPeriod suspensionPeriod;

    private LocalDateTime suspendedUntil;


    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getAge() {
        if (this.birth == null) {
            return 0; // 생일 정보 없을 경우 0 반환
        }
        return Period.between(this.birth, LocalDate.now()).getYears();
    }

    // 관심 태그 수정 메서드
    public void updateInterestTags(Set<InterestTag> tags) {
        this.interestTags.clear();
        this.interestTags.addAll(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id); // id를 기준으로 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // hashCode도 id 기준으로 계산
    }

    public boolean isSuspended() {
        return suspendedUntil != null && suspendedUntil.isAfter(LocalDateTime.now());
    }

}


