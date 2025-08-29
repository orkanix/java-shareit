package ru.practicum.shareit.request.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemRequestNotFound extends RuntimeException {
    public ItemRequestNotFound(String message) {
        super(message);
    }
}
