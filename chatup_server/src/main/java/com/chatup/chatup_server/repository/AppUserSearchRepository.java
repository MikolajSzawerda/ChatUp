package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories(
        basePackages = "com.chatup.chatup_server.repository", repositoryImplementationPostfix = "AppUserSearchRepositoryImpl")
public interface AppUserSearchRepository {
    List<AppUser> fuzzyUserSearch(String name, Pageable pageable);

}
