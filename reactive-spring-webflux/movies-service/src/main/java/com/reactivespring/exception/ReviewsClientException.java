package com.reactivespring.exception;

import lombok.Getter;

@Getter
public class ReviewsClientException extends RuntimeException {
    private final Integer statusCode;

    public ReviewsClientException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
