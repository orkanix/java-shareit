package ru.practicum.shareit.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotForbidden extends RuntimeException {
    public UserNotForbidden(String message) {
        super(message);
    }
}
