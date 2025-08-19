package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_Id(Long userId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByStatusAndBooker(BookingStatus status, User user, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort newestFirst);

    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User user,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findAllByItem_Owner_IdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByStatusAndItem_Owner_Id(BookingStatus status, Long userId, Sort sort);

    List<Booking> findAllByItem_Id(Long id);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}

