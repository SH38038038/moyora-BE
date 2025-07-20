package com.project.moyora.app.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.BoardDocument;
import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.RequiredArgsConstructor;
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

    public SearchHits<BoardDocument> search(String keyword, MeetType meetType, List<InterestTag> tags, Pageable pageable) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // meetType 필수 조건
        if (meetType != null) {
            boolQuery.must(m -> m.term(t -> t.field("meetType").value(meetType.name())));
        }

        // confirmed = false 조건
        boolQuery.must(m -> m.term(t -> t.field("confirmed").value(false)));

        // 키워드가 있으면 title/content/meetDetail에 match
        if (keyword != null && !keyword.isBlank()) {
            boolQuery.should(s -> s.match(m -> m.field("title").query(keyword)));
            boolQuery.should(s -> s.match(m -> m.field("content").query(keyword)));
            boolQuery.should(s -> s.match(m -> m.field("meetDetail").query(keyword)));
            boolQuery.minimumShouldMatch("1"); // should는 1개 이상 일치해야 함
        }

        // 태그 조건
        if (tags != null && !tags.isEmpty()) {
            List<String> tagNames = tags.stream().map(Enum::name).toList();
            boolQuery.must(m -> m.terms(t -> t.field("tags").terms(terms -> terms.value(tagNames.stream()
                    .map(FieldValue::of)
                    .toList()))));
        }

        Query finalQuery = boolQuery.build()._toQuery();

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
