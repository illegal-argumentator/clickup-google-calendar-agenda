package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class MainExceptionHandler {

    private static final String COMMA_DELIMITER = ", ";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request, HttpServletResponse response) {

        String invalidFields = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(COMMA_DELIMITER));

        return ResponseEntity.badRequest().body(invalidFields);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response, HttpServletRequest request) {

        String invalidFields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(COMMA_DELIMITER));

        return ResponseEntity.badRequest().body(invalidFields);
    }

}
