package com.riu.backend.challenge.adapter.in.rest;

import com.riu.backend.challenge.domain.exception.QueueUnavailableException;
import com.riu.backend.challenge.domain.exception.SearchNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SearchNotFoundException.class)
    ProblemDetail handleSearchNotFound(SearchNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(QueueUnavailableException.class)
    ProblemDetail handleQueueUnavailable(QueueUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
