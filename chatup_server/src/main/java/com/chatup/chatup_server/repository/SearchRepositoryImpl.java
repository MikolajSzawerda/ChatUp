package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.SearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SearchRepositoryImpl implements SearchRepository {
    private final EntityManager entityManager;

    public SearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long getHitCount(String word) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Message> results = session.search(Message.class)
                .where(f->f.match().field("content").matching(word)).fetch(20);
        return results.total().hitCount();
    }

    @Override
    public List<Message> fuzzySearchByContent(String phrase, Pageable pageable) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Message> result = session.search(Message.class)
                .where(f->f.match().field("content").matching(phrase).fuzzy())
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return result.hits();
    }

}
