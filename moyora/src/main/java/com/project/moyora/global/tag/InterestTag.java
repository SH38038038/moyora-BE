package com.project.moyora.global.tag;

import java.util.Arrays;
import java.util.Optional;

public enum InterestTag {
    // 스포츠
    SOCCER("스포츠", "축구"),
    BASKETBALL("스포츠", "농구"),
    BASEBALL("스포츠", "야구"),
    VOLLEYBALL("스포츠", "배구"),
    BADMINTON("스포츠", "배드민턴"),
    RUNNING("스포츠", "러닝"),
    HEALTH("스포츠", "헬스"),
    GOLF("스포츠", "골프"),
    SWIMMING("스포츠", "수영"),

    // 음악
    BAND("음악", "밴드"),
    HIPHOP("음악", "힙합"),
    K_POP("음악", "K-POP"),
    POP("음악", "POP"),
    J_POP("음악", "J-POP"),
    CLASSIC("음악", "클래식"),
    EDM("음악", "EDM"),

    // 게임
    CONSOLE_GAME("게임", "콘솔 게임"),
    PC_GAME("게임", "PC 게임"),
    MOBILE_GAME("게임", "모바일 게임"),
    BOARD_GAME("게임", "보드게임"),
    TRPG("게임", "TRPG"),
    STEAM_GAME("게임", "스팀 게임"),

    // 여행 & 아웃도어
    DOMESTIC_TRAVEL("여행 & 아웃도어", "국내 여행"),
    OVERSEAS_TRAVEL("여행 & 아웃도어", "해외 여행"),
    CAMPING("여행 & 아웃도어", "캠핑"),
    HIKING("여행 & 아웃도어", "등산"),
    FISHING("여행 & 아웃도어", "낚시"),
    BICYCLE("여행 & 아웃도어", "자전거"),
    SKI("여행 & 아웃도어", "스키"),
    SURFING("여행 & 아웃도어", "서핑"),
    PARAGLIDING("여행 & 아웃도어", "패러글라이딩"),

    // 취미
    DRAWING("취미", "그림"),
    PHOTOGRAPHY("취미", "사진"),
    WRITING("취미", "글쓰기"),
    CRAFT("취미", "공예"),
    READING("취미", "독서"),
    COLLECTING("취미", "수집"),
    MOVIE("취미", "영화"),
    DRAMA("취미", "드라마"),
    ANIMATION("취미", "애니메이션"),
    WEBTOON("취미", "웹툰"),
    OTT("취미", "OTT"),

    // 공부 & 자기계발
    CODING("공부 & 자기계발", "코딩"),
    FOREIGN_LANGUAGE("공부 & 자기계발", "외국어"),
    CERTIFICATION("공부 & 자기계발", "자격증"),

    // 일상 & 소셜
    FOOD_TOUR("일상 & 소셜", "맛집 탐방"),
    CAFE_TOUR("일상 & 소셜", "카페 투어"),
    PET("일상 & 소셜", "반려동물"),
    PARENTING("일상 & 소셜", "육아"),

    // 비즈니스 & 경제
    STARTUP("비즈니스 & 경제", "스타트업"),
    STOCK("비즈니스 & 경제", "주식"),
    REAL_ESTATE("비즈니스 & 경제", "부동산"),
    ENTREPRENEURSHIP("비즈니스 & 경제", "창업");

    private final String section;
    private final String displayName;

    InterestTag(String section, String displayName) {
        this.section = section;
        this.displayName = displayName;
    }

    public String getSection() {
        return section;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<InterestTag> from(String section, String name, String displayName) {
        return Arrays.stream(values())
                .filter(tag ->
                        tag.section.equals(section)
                                && tag.name().equals(name)    // ← name() 메서드 사용
                                && tag.displayName.equals(displayName))
                .findFirst();
    }

}
