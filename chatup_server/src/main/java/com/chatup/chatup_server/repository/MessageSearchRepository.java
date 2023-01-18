package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Message;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@EnableJpaRepositories(
        basePackages = "com.chatup.chatup_server.repository", repositoryImplementationPostfix = "MessageSearchRepositoryImpl")
public interface MessageSearchRepository {

    List<Message> fuzzySearchByContent(String phrase, Set<Long> channels, Pageable pageable);
}
