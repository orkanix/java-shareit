package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.exception.UserNotForbidden;
import ru.practicum.shareit.booking.exception.UserNotItemOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStates;
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

        assertEquals(1, bookingService.getUserBookings(BookingStates.FUTURE, booker.getId()).size());
    }

    @Test
    void approveBooking_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.approveBooking(booking.getId(), booker.getId(), true));
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void getOwnerBookings_allStatesCovered() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItem_Owner_IdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItem_Owner_IdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByStatusAndItem_Owner_Id(eq(BookingStatus.WAITING), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByStatusAndItem_Owner_Id(eq(BookingStatus.REJECTED), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItem_Owner_Id(anyLong(), any()))
                .thenReturn(List.of(booking));

        for (BookingStates state : BookingStates.values()) {
            List<BookingDto> result = bookingService.getOwnerBookings(state, 1L);
            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
        }

        verify(userRepository, times(6)).findById(1L);
    }

    @Test
    void getUserBookings_allStates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerAndStartAfter(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByStatusAndBooker(eq(BookingStatus.WAITING), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByStatusAndBooker(eq(BookingStatus.REJECTED), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> current = bookingService.getUserBookings(BookingStates.CURRENT, 1L);
        List<BookingDto> past = bookingService.getUserBookings(BookingStates.PAST, 1L);
        List<BookingDto> future = bookingService.getUserBookings(BookingStates.FUTURE, 1L);
        List<BookingDto> waiting = bookingService.getUserBookings(BookingStates.WAITING, 1L);
        List<BookingDto> rejected = bookingService.getUserBookings(BookingStates.REJECTED, 1L);
        List<BookingDto> all = bookingService.getUserBookings(BookingStates.ALL, 1L);

        assertEquals(1, current.size());
        assertEquals(1, past.size());
        assertEquals(1, future.size());
        assertEquals(1, waiting.size());
        assertEquals(1, rejected.size());
        assertEquals(1, all.size());
    }

    @Test
    void getOwnerBookings_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItem_Owner_Id(1L, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(BookingStates.ALL, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(userRepository).findById(1L);
        verify(bookingRepository).findAllByItem_Owner_Id(1L, Sort.by(Sort.Direction.DESC, "start"));
    }


    @Test
    void approveBooking_throwsUserNotItemOwner() {
        User stranger = new User();
        stranger.setId(2L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(stranger));

        assertThrows(UserNotItemOwner.class,
                () -> bookingService.approveBooking(1L, 2L, true));
    }

    @Test
    void getBooking_throwsUserNotForbidden() {
        User stranger = new User();
        stranger.setId(3L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(3L)).thenReturn(Optional.of(stranger));

        assertThrows(UserNotForbidden.class,
                () -> bookingService.getBooking(1L, 3L));
    }

    @Test
    void approveBooking_throwsBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        assertThrows(BookingNotFound.class,
                () -> bookingService.approveBooking(99L, 1L, true));
    }

}
