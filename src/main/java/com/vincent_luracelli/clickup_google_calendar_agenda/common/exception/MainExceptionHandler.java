package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class MainExceptionHandler {

    private static final String COMMA_DELIMITER = ", ";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response) {

        String invalidFields = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(COMMA_DELIMITER));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(invalidFields)
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response, HttpServletRequest request) {
        String invalidFields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(COMMA_DELIMITER));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(invalidFields)
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(e.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

}
