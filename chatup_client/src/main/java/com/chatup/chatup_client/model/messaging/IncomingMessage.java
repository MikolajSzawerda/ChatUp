package com.chatup.chatup_client.model.messaging;

public class IncomingMessage {
    public IncomingMessage(String message) {
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
