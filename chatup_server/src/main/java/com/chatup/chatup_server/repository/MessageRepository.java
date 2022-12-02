package com.chatup.chatup_server.repository;

import com.chatup.chatup_server.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;

import java.util.List;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value="select m from Message m where m.channelID=?1 order by m.ID desc")
    Page<Message> getLastFeed(Long channelID, Pageable pageable);

    @Query(value="select m from Message m where m.channelID=?1 and m.ID < ?2 order by m.ID desc")
    Page<Message> getFeedFrom(Long channelID, Long messageID, Pageable pageable);
}
