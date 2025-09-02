package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            ItemMapper.mapToItemDto(booking.getItem()),
            booking.getStart(),
            booking.getEnd(),
            UserMapper.mapToUserDto(booking.getBooker()),
            booking.getBooked(),
            booking.getStatus()
        );
    }

    public static Booking maptoBooking(NewBookingDto newBookingDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(newBookingDto.getStart());
        booking.setEnd(newBookingDto.getEnd());
        booking.setBooker(user);
        return booking;
    }
}
