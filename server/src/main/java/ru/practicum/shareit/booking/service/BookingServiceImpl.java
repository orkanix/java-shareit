package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.exception.UserNotForbidden;
import ru.practicum.shareit.booking.exception.UserNotItemOwner;
import ru.practicum.shareit.booking.model.BookingStates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.exceptions.ItemNotAvailable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto createBooking(NewBookingDto newBookingDto, Long userId) {
        log.info("Создаю новое бронирование...");
        Item item = itemRepository.findById(newBookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFound("Вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));
        if (!item.getAvailable()) {
            throw new ItemNotAvailable("Вещь недоступна для бронирования");
        }
        Booking booking = bookingRepository.save(BookingMapper.maptoBooking(newBookingDto, item, user));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("Подтверждение бронирования");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFound("Бронирование не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotForbidden("Пользователь не найден"));
        if (!booking.getItem().getOwner().equals(user)) {
            throw new UserNotItemOwner("Пользователь не владелец вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        log.info("Получение информации о бронировании");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFound("Бронирование не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));
        if (!booking.getItem().getOwner().getId().equals(user.getId()) &&
                !booking.getBooker().getId().equals(user.getId())) {
            throw new UserNotForbidden("Доступ к информации о бронировании запрещен");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(BookingStates state, Long userId) {
        log.info("Получаю все бронирования пользователя с id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now, newestFirst);
            case PAST -> bookingRepository.findAllByBookerAndEndBefore(user, now, newestFirst);
            case FUTURE -> bookingRepository.findAllByBookerAndStartAfter(user, now, newestFirst);
            case WAITING -> bookingRepository.findAllByStatusAndBooker(BookingStatus.WAITING, user, newestFirst);
            case REJECTED -> bookingRepository.findAllByStatusAndBooker(BookingStatus.REJECTED, user, newestFirst);
            case ALL -> bookingRepository.findAllByBooker(user, newestFirst);
        };

        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(BookingStates state, Long ownerId) {
        log.info("Получаю все бронирования пользователя с id: {}", ownerId);
       userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, newestFirst);
            case PAST -> bookingRepository.findAllByItem_Owner_IdAndEndBefore(ownerId, now, newestFirst);
            case FUTURE -> bookingRepository.findAllByItem_Owner_IdAndStartAfter(ownerId, now, newestFirst);
            case WAITING -> bookingRepository.findAllByStatusAndItem_Owner_Id(BookingStatus.WAITING, ownerId, newestFirst);
            case REJECTED -> bookingRepository.findAllByStatusAndItem_Owner_Id(BookingStatus.REJECTED, ownerId, newestFirst);
            case ALL -> bookingRepository.findAllByItem_Owner_Id(ownerId, newestFirst);
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }
}