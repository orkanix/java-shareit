package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NewBookingDto {
    @NotNull(message = "Id вещи не может быть пустым")
    Long itemId;

    @NotNull(message = "Время начала аренды не может быть пустым")
    LocalDateTime start;

    @NotNull(message = "Время конца аренды не может быть пустым")
    @Future(message = "Время конца аренды должно быть в будущем")
    LocalDateTime end;
}
