package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CommentBadRequest extends RuntimeException {
    public CommentBadRequest(String message) {
        super(message);
    }
}
