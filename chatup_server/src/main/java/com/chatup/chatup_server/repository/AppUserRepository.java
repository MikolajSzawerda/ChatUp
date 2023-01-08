package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, AppUserSearchRepository {
    AppUser findAppUserByUsername(String username);
}
