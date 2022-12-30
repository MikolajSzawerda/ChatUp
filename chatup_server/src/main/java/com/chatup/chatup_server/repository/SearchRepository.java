package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.domain.Pageable;

import java.util.List;

@EnableJpaRepositories(
        basePackages = "com.chatup.chatup_server.repository", repositoryImplementationPostfix = "SearchRepositoryImlp")
public interface SearchRepository {
    @Transactional
    Long getHitCount(String word);

    List<Message> fuzzySearchByContent(String phrase, Pageable pageable);
}
