package com.project.moyora.app.domain;


import com.project.moyora.global.tag.InterestTag;
import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.MeetType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "boards")
@Setting(settingPath = "elasticsearch/board-settings.json")
@Mapping(mappingPath = "elasticsearch/board-mappings.json")

public class BoardDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String content;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String meetDetail;

    @Field(type = FieldType.Keyword)
    private MeetType meetType;

    @Field(type = FieldType.Keyword)
    private List<InterestTag> tags;

    @Field(type = FieldType.Boolean)
    private boolean confirmed;

    @Field(type = FieldType.Long)
    private Long writerId;

    @Field(type = FieldType.Keyword)
    private GenderType genderType;

    @Field(type = FieldType.Integer)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    private Integer maxAge;

    @Field(type = FieldType.Date)
    private LocalDate startDate;

    @Field(type = FieldType.Date)
    private LocalDate endDate;

    @Field(type = FieldType.Integer)
    private Integer howMany;

    @Field(type = FieldType.Integer)
    private Integer participation;

    @Field(type = FieldType.Keyword)
    private String createdTime;
}