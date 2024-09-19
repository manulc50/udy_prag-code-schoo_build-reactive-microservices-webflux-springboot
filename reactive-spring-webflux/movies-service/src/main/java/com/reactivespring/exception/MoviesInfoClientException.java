package com.reactivespring.exception;

import lombok.Getter;

@Getter
public class MoviesInfoClientException extends RuntimeException {
    private final Integer statusCode;

    public MoviesInfoClientException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
