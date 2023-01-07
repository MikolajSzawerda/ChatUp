package com.chatup.chatup_client.model;

public class Channel
{
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean getIsDirectMessage() {
        return isDirectMessage;
    }

    public void setIsDirectMessage(boolean directMessage) {
        isDirectMessage = directMessage;
    }

    private String name;
    private boolean isPrivate;
    private boolean isDirectMessage;
}
