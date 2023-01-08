package com.chatup.chatup_client.model;

import java.time.Instant;

public class Message {
    private Long messageID;
    private String content;
    private Long authorID;
    private String authorUsername;
    private String authorFirstName;
    private String authorLastName;
    private Long channelID;
    private Instant timeCreated;
    private Boolean isDeleted;
    private Boolean duplicateFlag = false;

    public Message(Message other) {
        this.messageID = other.getMessageID();
        this.content = other.getContent();
        this.authorID = other.getAuthorID();
        this.authorUsername = other.getAuthorUsername();
        this.authorFirstName = other.getAuthorFirstName();
        this.authorLastName = other.getAuthorLastName();
        this.channelID = other.getChannelID();
        this.timeCreated = other.getTimeCreated();
        this.isDeleted = other.getIsDeleted();
        this.duplicateFlag = other.getDuplicateFlag();
    }

    public Message() {}

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Long authorID) {
        this.authorID = authorID;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Long getChannelID() {
        return channelID;
    }

    public void setChannelID(Long channelID) {
        this.channelID = channelID;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String toString() {
        return content;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof Message other)) return false;
        return getMessageID().equals(other.getMessageID())
                && getAuthorID().equals(other.getAuthorID())
                && getChannelID().equals(other.getChannelID())
                && getTimeCreated().equals(other.getTimeCreated())
                && getIsDeleted().equals(other.getIsDeleted())
                && getContent().equals(other.getContent())
                && getAuthorUsername().equals(other.getAuthorUsername())
                && getAuthorFirstName().equals(other.getAuthorFirstName())
                && getAuthorLastName().equals(other.getAuthorLastName());
    }

    public Boolean getDuplicateFlag() {
        return duplicateFlag;
    }

    public void setDuplicateFlag(Boolean duplicateFlag) {
        this.duplicateFlag = duplicateFlag;
    }
}