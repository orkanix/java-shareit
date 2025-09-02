package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var owner = new UserDto(1L, "John Wick", "john.wick@comiccon.com");
        var booker = new UserDto(2L, "Neo", "neo@matrix.com");

        var item = new ItemDto(
                10L,
                "Katana",
                "Samurai sword",
                true,
                owner,
                null
        );

        var dto = new BookingDto(
                100L,
                item,
                LocalDateTime.of(2025, 9, 2, 12, 0),
                LocalDateTime.of(2025, 9, 3, 12, 0),
                booker,
                true,
                BookingStatus.APPROVED
        );


        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        assertThat(result).hasJsonPath("$.booked");
        assertThat(result).extractingJsonPathBooleanValue("$.booked").isEqualTo(dto.getBooked());

        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().name());

        assertThat(result).hasJsonPath("$.booker.id");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(dto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(dto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(dto.getBooker().getEmail());

        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(item.getAvailable());
    }
}
