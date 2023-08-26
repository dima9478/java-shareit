package ru.practicum.shareit.exception;

public class ObjectUnavailableException extends RuntimeException {
    public ObjectUnavailableException() {
        super();
    }

    public ObjectUnavailableException(String message) {
        super(message);
    }
}
