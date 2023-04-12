package com.company.web.smart_garage.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityDuplicationException.class)
    public ResponseEntity<Error> handleEntityDuplicationException(EntityDuplicationException e, WebRequest webRequest) {
        Error error = new Error(LocalDateTime.now(), e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException e, WebRequest webRequest) {
        Error error = new Error(LocalDateTime.now(), e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Error> handleUnauthorizedOperationException(UnauthorizedOperationException e, WebRequest webRequest) {
        Error error = new Error(LocalDateTime.now(), e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<Error> handleInvalidParamException(InvalidParamException e, WebRequest webRequest) {
        Error error = new Error(LocalDateTime.now(), e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException e, WebRequest webRequest) {
        Error error = new Error(LocalDateTime.now(), e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


}
