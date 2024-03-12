package com.group3.ezquiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidClassroomException extends RuntimeException {
    public InvalidClassroomException(String message) {
        super(message);
    }
}