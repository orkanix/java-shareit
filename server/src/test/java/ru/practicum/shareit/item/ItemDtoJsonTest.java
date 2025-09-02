package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        var owner = new UserDto(1L, "John Wick", "john.wick@comiccon.com");
        var requester = new UserDto(2L, "Neo", "neo@matrix.com");

        var created = LocalDateTime.of(2025, 9, 1, 10, 0);

        var request = new ItemRequestDto(
                5L,
                requester,
                "Need katana",
                created
        );

        var dto = new ItemDto(
                10L,
                "Katana",
                "Samurai sword",
                true,
                owner,
                request
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());

        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(owner.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo(owner.getName());
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo(owner.getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.request.id").isEqualTo(request.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.request.description").isEqualTo(request.getDescription());

        assertThat(result).extractingJsonPathNumberValue("$.request.user.id").isEqualTo(requester.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.request.user.name").isEqualTo(requester.getName());
        assertThat(result).extractingJsonPathStringValue("$.request.user.email").isEqualTo(requester.getEmail());

        assertThat(result).extractingJsonPathStringValue("$.request.created")
                .isEqualTo(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}
