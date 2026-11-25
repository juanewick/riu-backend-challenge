package com.riu.backend.challenge.domain.exception;

public class QueueUnavailableException extends RuntimeException {

    public QueueUnavailableException() {
        super("Queue message provider service unavailable.");
    }
}
