package ru.practicum.shareit.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class UserNotItemOwner extends RuntimeException {
  public UserNotItemOwner(String message) {
    super(message);
  }
}
