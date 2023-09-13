package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            ConstraintViolationException.class,
            ValidationException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Object> handleValidationError(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        return new ResponseEntity<>(
                new ErrorResponse(fieldError.getField() + " " + fieldError.getDefaultMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleRemaining(Throwable e) {
        String message = e.getMessage();
        return new ResponseEntity<>(
                new ErrorResponse(message != null ? message : "Unrecognised error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
