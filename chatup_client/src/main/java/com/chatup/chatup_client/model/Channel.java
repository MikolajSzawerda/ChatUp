package com.chatup.chatup_client.model;

public class Channel
{
    private Long id;
    private String name;
    private Boolean isPrivate;
    private Boolean isDirectMessage;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Channel(Channel other) {
        this.id = other.getId();
        this.name = other.getName();
        this.isPrivate = other.getIsPrivate();
        this.isDirectMessage = other.getIsDirectMessage();
    }

    public Channel() {}
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getIsDirectMessage() {
        return isDirectMessage;
    }

    public void setIsDirectMessage(Boolean directMessage) {
        isDirectMessage = directMessage;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof Channel channelObj)) return false;
        return id.equals(channelObj.getId());
    }

    public String toString() {
        return name;
    }
}
