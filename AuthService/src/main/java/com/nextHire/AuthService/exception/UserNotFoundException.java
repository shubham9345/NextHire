package com.nextHire.AuthService.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final String localMessage;
    public UserNotFoundException(String message,String localMessage){
        super(message);
        this.localMessage = localMessage;
    }
    public UserNotFoundException(String message, Throwable cause,String localMessage) {
        super(message, cause);
        this.localMessage = localMessage;
    }
}