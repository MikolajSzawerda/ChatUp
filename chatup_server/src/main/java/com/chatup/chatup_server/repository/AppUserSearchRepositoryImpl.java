package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.AppUser;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class AppUserSearchRepositoryImpl implements AppUserSearchRepository{
    private final EntityManager entityManager;

    public AppUserSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<AppUser> fuzzyUserSearch(String name, Pageable pageable) {
        SearchSession session = Search.session(entityManager);
        SearchResult<AppUser> result = session.search(AppUser.class)
                .where(f->f.match().field("firstName").matching(name).fuzzy())
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return result.hits();
    }
}
