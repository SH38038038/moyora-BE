package com.project.moyora.app.domain;

import com.project.moyora.global.tag.InterestTag;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    private String title;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private Integer minAge;
    private Integer maxAge;

    private LocalDate startDate;
    private LocalDate endDate;

    @ElementCollection(targetClass = InterestTag.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "board_interest_tags", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "tags")  // 컬럼 이름 명시
    @Enumerated(EnumType.STRING)
    private List<InterestTag> tags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer howMany;    // 모집인원

    private Integer participation;  // 현재 신청자수

    private boolean confirmed;  // 확정 여부 (참여 인원 다 찼을 때만 변경 가능)

    private MeetType meetType;

    @Column(name = "meet_detail")
    private String meetDetail;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BoardApplication> applications = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // 찜한 사용자 목록

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public boolean isFull() {
        return participation != null && howMany != null && participation >= howMany;
    }

    public void increaseParticipation() {
        if (this.participation == null) this.participation = 0;
        if (isFull()) {
            throw new IllegalStateException("모집 인원이 모두 찼습니다.");
        }
        this.participation++;
    }

}
