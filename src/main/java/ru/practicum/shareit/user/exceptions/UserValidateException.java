package ru.practicum.shareit.user.exceptions;

public class UserValidateException extends RuntimeException {
    public UserValidateException(String message) {
        super(message);
    }
}
