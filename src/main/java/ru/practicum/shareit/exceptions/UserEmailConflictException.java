package ru.practicum.shareit.exceptions;

public class UserEmailConflictException extends RuntimeException {
    public UserEmailConflictException(String message) {
        super(message);
    }
}