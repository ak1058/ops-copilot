package com.opscopilot.exception;

import com.opscopilot.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage(), generateRequestId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        return new ErrorResponse("VALIDATION_ERROR", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), generateRequestId());
    }

    @ExceptionHandler(GeminiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleGeminiException(GeminiException ex) {
        log.error("AI Error: {}", ex.getMessage());
        return new ErrorResponse("AI_SERVICE_UNAVAILABLE", "The operations assistant encountered an error while communicating with the AI service.", generateRequestId());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Internal Error", ex);
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred.", generateRequestId());
    }

    private String generateRequestId() {
        return "req_err_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
