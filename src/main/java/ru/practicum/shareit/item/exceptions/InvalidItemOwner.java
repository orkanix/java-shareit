package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class InvalidItemOwner extends RuntimeException {
    public InvalidItemOwner(String message) {
        super(message);
    }
}
