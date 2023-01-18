package com.chatup.chatup_client.model.messaging;

public class OutgoingMessage {
    public OutgoingMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}
