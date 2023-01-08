package com.chatup.chatup_client.model;

public class Channel
{
    private Long id;
    private String name;
    private Boolean isPrivate;
    private Boolean isDirectMessage;


    private Boolean duplicateFlag = false;

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
        this.duplicateFlag = other.getDuplicateFlag();
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
        return name.equals(channelObj.getName()) &&
                id.equals(channelObj.getId()) &&
                isPrivate.equals(channelObj.getIsPrivate()) &&
                isDirectMessage.equals(channelObj.getIsDirectMessage());
    }

    public Boolean getDuplicateFlag() {
        return duplicateFlag;
    }

    public void setDuplicateFlag(Boolean duplicateFlag) {
        this.duplicateFlag = duplicateFlag;
    }

    public String toString() {
        return name;
    }
}
