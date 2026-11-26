package com.riu.backend.challenge.adapter.in.rest;

import com.riu.backend.challenge.adapter.in.rest.api.InvalidParam;
import com.riu.backend.challenge.domain.exception.QueueUnavailableException;
import com.riu.backend.challenge.domain.exception.SearchNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final String INVALID_REQUEST = "Invalid request.";

    @ExceptionHandler(SearchNotFoundException.class)
    ProblemDetail handleSearchNotFound(SearchNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(QueueUnavailableException.class)
    ProblemDetail handleQueueUnavailable(QueueUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleInvalidBody(MethodArgumentNotValidException e) {
        List<InvalidParam> invalidParams = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new InvalidParam(error.getField(), error.getDefaultMessage()))
                .toList();
        return badRequest(invalidParams);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ProblemDetail handleInvalidParameters(HandlerMethodValidationException e) {
        List<InvalidParam> invalidParams = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new InvalidParam(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage())))
                .toList();
        return badRequest(invalidParams);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail handleMissingParameter(MissingServletRequestParameterException e) {
        return badRequest(List.of(new InvalidParam(e.getParameterName(), "must not be null")));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return badRequest(List.of(new InvalidParam(e.getName(), "must be a valid UUID")));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableBody(HttpMessageNotReadableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed request body.");
    }

    private ProblemDetail badRequest(List<InvalidParam> invalidParams) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, INVALID_REQUEST);
        problem.setProperty("invalidParams", invalidParams);
        return problem;
    }
}
