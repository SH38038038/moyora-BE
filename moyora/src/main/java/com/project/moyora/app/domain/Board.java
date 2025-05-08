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
import java.util.List;

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
    private User writer;

    private String title;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private Integer minAge;
    private Integer maxAge;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private InterestTag interestTag;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer howMany;    // 모집인원

    private Integer participation;  // 현재 신청자수

    private MeetType meetType;

    private String meetDetail;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardApplication> applications = new ArrayList<>();

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
}
