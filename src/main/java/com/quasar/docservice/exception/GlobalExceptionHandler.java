package com.quasar.docservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTemplateNotFound(TemplateNotFoundException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Template Error", ex.getMessage());
    }

    @ExceptionHandler(InvalidJsonException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson(InvalidJsonException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON", ex.getMessage());
    }

    @ExceptionHandler(DocumentGenerationException.class)
    public ResponseEntity<Map<String, String>> handleDocumentGeneration(DocumentGenerationException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Generation Error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error", ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
