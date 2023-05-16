package com.example.common.exceptions;

import org.apache.kafka.common.errors.RetriableException;

public class MyRetriableException extends RetriableException {
    public MyRetriableException(String message) {
        super(message);
    }
}
