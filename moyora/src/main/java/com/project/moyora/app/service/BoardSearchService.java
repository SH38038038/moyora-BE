package com.project.moyora.app.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.BoardDocument;
import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<BoardDocument> search(String keyword, MeetType meetType,
                                            List<InterestTag> tags, int userAge, GenderType userGender,
                                            int page, int size) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        if (meetType != null) {
            boolQuery.must(m -> m.term(t -> t.field("meetType").value(meetType.name())));
        }

        boolQuery.must(m -> m.term(t -> t.field("confirmed").value(false)));

        if (keyword != null && !keyword.isBlank()) {
            boolQuery.should(s -> s.multiMatch(mm -> mm
                    .query(keyword)
                    .fields("title", "content", "meetDetail")
                    .fuzziness("AUTO")
                    .prefixLength(1)
            ));
            boolQuery.should(s -> s.multiMatch(mm -> mm
                    .query(keyword)
                    .fields("title.ngram", "content.ngram", "meetDetail.ngram")
            ));
            boolQuery.minimumShouldMatch("1");
        }

        if (tags != null && !tags.isEmpty()) {
            List<String> tagNames = tags.stream().map(Enum::name).toList();
            boolQuery.must(m -> m.terms(t -> t.field("tags")
                    .terms(terms -> terms.value(tagNames.stream().map(FieldValue::of).toList()))));
        }

        // 나이 필터
        boolQuery.must(m -> m.bool(b -> b
                .should(s -> s.range(r -> r.field("minAge").lte(JsonData.of(userAge))))
                .should(s -> s.bool(b2 -> b2.mustNot(mn -> mn.exists(e -> e.field("minAge")))))
                .minimumShouldMatch("1")
        ));
        boolQuery.must(m -> m.bool(b -> b
                .should(s -> s.range(r -> r.field("maxAge").gte(JsonData.of(userAge))))
                .should(s -> s.bool(b2 -> b2.mustNot(mn -> mn.exists(e -> e.field("maxAge")))))
                .minimumShouldMatch("1")
        ));

        // 성별 필터
        if (userGender != null && userGender != GenderType.OTHER) {
            boolQuery.must(m -> m.bool(b -> b
                    .should(s -> s.term(t -> t.field("genderType").value(GenderType.OTHER.name())))
                    .should(s -> s.term(t -> t.field("genderType").value(userGender.name())))
                    .minimumShouldMatch("1")
            ));
        }

        Query finalQuery = boolQuery.build()._toQuery();

        // Pageable 직접 생성
        Pageable pageable = PageRequest.of(page, size);

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(pageable)
                .build();

        return elasticsearchOperations.search(searchQuery, BoardDocument.class);
    }

    public void indexBoard(Board board) {
        BoardDocument doc = BoardDocument.builder()
                .id(board.getId())
                .writerId(board.getWriter().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .genderType(board.getGenderType())
                .minAge(board.getMinAge())
                .maxAge(board.getMaxAge())
                .startDate(board.getStartDate())
                .endDate(board.getEndDate())
                .tags(board.getTags())
                .howMany(board.getHowMany())
                .participation(board.getParticipation())
                .confirmed(board.isConfirmed())
                .meetType(board.getMeetType())
                .meetDetail(board.getMeetDetail())
                .createdTime(board.getCreatedTime().toString())
                .build();

        elasticsearchOperations.save(doc);
    }
}
