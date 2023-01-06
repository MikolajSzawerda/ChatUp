package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Set<Channel> findByUsersIdAndIsDirectMessage(Long userId, Boolean directMessage);
}
