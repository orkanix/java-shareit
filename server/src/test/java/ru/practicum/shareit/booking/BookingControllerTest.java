package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingStates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private NewBookingDto newBookingDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(1L, "User", "user@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, userDto, null);

        bookingDto = new BookingDto(1L, itemDto,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                userDto,
                true,
                BookingStatus.WAITING);

        newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(1L);
        newBookingDto.setStart(LocalDateTime.now().plusHours(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void createBooking_success() throws Exception {
        when(bookingService.createBooking(any(NewBookingDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()));
    }

    @Test
    void approveBooking_success() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getBooking_success() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getUserBookings_success() throws Exception {
        when(bookingService.getUserBookings(any(BookingStates.class), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getOwnerBookings_success() throws Exception {
        when(bookingService.getOwnerBookings(any(BookingStates.class), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

}
