package com.chatup.chatup_server.domain.exceptions;

public class InvalidRequestException extends ApiException{
    public InvalidRequestException(String message) {
        super(message);
    }
}
