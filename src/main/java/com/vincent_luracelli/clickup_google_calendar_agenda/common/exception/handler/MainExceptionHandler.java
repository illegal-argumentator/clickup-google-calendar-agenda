package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.handler;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.*;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class MainExceptionHandler {

    private final ExceptionHandlerService exceptionHandlerService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, fieldError -> StringUtils.defaultIfEmpty(fieldError.getDefaultMessage(), "")));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(errors.toString())
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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException(ApiException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(e.getSourceType())
                .body(e.getMessage())
                .code(e.getCode())
                .build();
        return ResponseEntity.status(exceptionResponse.code()).body(exceptionResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class, CriticalApiException.class})
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ExceptionResponse exceptionResponse = exceptionHandlerService.handleInternalServerError(e);
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
