package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private Item item;
    private Booking booking;

    private final Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booker = new User();
        booker.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setOwner(booker);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking_success() {
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now().plusHours(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.createBooking(newBookingDto, booker.getId()));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getUserBookings_success() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerAndStartAfter(eq(booker), any(), eq(newestFirst)))
                .thenReturn(List.of(booking));

        assertEquals(1, bookingService.getUserBookings("FUTURE", booker.getId()).size());
    }

    @Test
    void approveBooking_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.approveBooking(booking.getId(), booker.getId(), true));
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }
}
