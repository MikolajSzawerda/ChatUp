package com.chatup.chatup_server.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.time.Instant;
@Entity
@Table(
        name = "Messages"
)
@Indexed
public class Message{
    @Id
    @SequenceGenerator(
            name = "message_sequence",
            sequenceName = "message_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "message_sequence"
    )
    @Column(name = "message_id")
    private Long ID;
    @Column(length = 5000)
    @FullTextField
    private String content;
    @Column(name="time_created")
    private Instant timeCreated;
    @OneToOne
    private AppUser author;
    @ManyToOne
    @IndexedEmbedded
    @JoinColumn(name="channel_id", nullable=false)
    private Channel channel;
    @ColumnDefault(value = "false")
    @Column(name="is_deleted")
    private Boolean isDeleted;

    public Message() {
    }

    public Message(String content, Instant timeCreated, AppUser author, Channel channel, Boolean isDeleted) {
        this.content = content;
        this.timeCreated = timeCreated;
        this.author = author;
        this.channel = channel;
        this.isDeleted = isDeleted;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}