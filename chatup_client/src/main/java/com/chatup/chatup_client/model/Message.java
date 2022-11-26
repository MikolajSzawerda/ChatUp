package com.chatup.chatup_client.model;

public record Message(
        String msg
) {
    @Override
    public String toString() {
        return msg;
    }
}
