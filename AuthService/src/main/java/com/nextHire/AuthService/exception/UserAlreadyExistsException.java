package com.nextHire.AuthService.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {

    private final String localMessage;

    public UserAlreadyExistsException(String message, String localMessage) {
        super(message);
        this.localMessage = localMessage;
    }
}