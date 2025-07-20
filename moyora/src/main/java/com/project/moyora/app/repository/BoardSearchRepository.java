package com.project.moyora.app.repository;

import com.project.moyora.app.domain.BoardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BoardSearchRepository extends ElasticsearchRepository<BoardDocument, Long> {
}
