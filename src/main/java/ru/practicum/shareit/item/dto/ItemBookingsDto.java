package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class ItemBookingsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    ItemRequestDto request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
