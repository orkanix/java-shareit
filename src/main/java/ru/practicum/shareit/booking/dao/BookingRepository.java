package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByStatusAndBookerOrderByStartDesc(BookingStatus status, User user);

    List<Booking> findAllByItem_Id(Long id);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}

