package com.chatup.chatup_server.domain.exceptions;


public class ApiException extends Exception{
    private String message;

    public ApiException(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
