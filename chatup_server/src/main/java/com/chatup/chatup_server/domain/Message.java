package com.chatup.chatup_server.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
@Entity
@Table(
        name = "Messages"
)
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
    private String content;
    @Column(name="time_created")
    private Instant timeCreated;
    @OneToOne
    private AppUser author;
    @Column(name="channel_id")
    private Long channelID;
    @ColumnDefault(value = "false")
    private Boolean isDeleted;

    public Message() {
    }

    public Message(String content, Instant timeCreated, AppUser author, Long channelID, Boolean isDeleted) {
        this.content = content;
        this.timeCreated = timeCreated;
        this.author = author;
        this.channelID = channelID;
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

    public Long getChannelID() {
        return channelID;
    }

    public void setChannelID(Long channelID) {
        this.channelID = channelID;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}