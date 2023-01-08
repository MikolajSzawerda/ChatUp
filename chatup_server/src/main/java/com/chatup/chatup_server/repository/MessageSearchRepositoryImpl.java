package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Message;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public class MessageSearchRepositoryImpl implements MessageSearchRepository {
    private final EntityManager entityManager;

    public MessageSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Message> fuzzySearchByContent(String phrase, Set<Long> channels, Pageable pageable) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Message> result = session.search(Message.class)
                .where(f->f.bool()
                        .must(f.match().field("content").matching(phrase).fuzzy())
                        .must(f.terms().field("channel.id").matchingAny(channels)))
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return result.hits();
    }



}
