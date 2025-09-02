package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingStates;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(NewBookingDto newBookingDto, Long userId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(BookingStates state, Long userId);

    List<BookingDto> getOwnerBookings(BookingStates state, Long userId);
}