package ru.practicum.shareit.exception;

public class UniqueViolationException extends RuntimeException {
    public UniqueViolationException() {
        super();
    }

    public UniqueViolationException(String message) {
        super(message);
    }
}
