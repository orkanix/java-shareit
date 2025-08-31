package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemRequestDto {
	@NotNull(message = "Поле itemId не может быть пустым")
	Long itemId;
	@FutureOrPresent
	@NotNull(message = "Поле start не может быть пустым")
	LocalDateTime start;
	@Future
	@NotNull(message = "Поле end не может быть пустым")
	LocalDateTime end;
}
