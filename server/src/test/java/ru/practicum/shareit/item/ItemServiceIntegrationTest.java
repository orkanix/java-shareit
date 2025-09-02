package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ru.practicum.shareit.item.dao.ItemRepository itemRepository;

    @Test
    void testItemWithCommentIntegration() {
        NewUserDto newUser = NewUserDto.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build();
        UserDto userDto = userService.createUser(newUser);
        User userEntity = userRepository.findById(userDto.getId()).orElseThrow();

        NewItemDto newItem = NewItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .requestId(null)
                .build();
        ItemDto itemDto = itemService.createItem(newItem, userDto.getId());
        Item itemEntity = itemRepository.findById(itemDto.getId()).orElseThrow();

        Booking booking = new Booking();
        booking.setItem(itemEntity);
        booking.setBooker(userEntity);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        NewCommentDto commentDto = new NewCommentDto("Nice item");
        CommentDto comment = itemService.createComment(commentDto, itemDto.getId(), userDto.getId());

        assertNotNull(itemDto.getId());
        assertEquals("Item1", itemDto.getName());
        assertNotNull(comment.getId());
        assertEquals("Nice item", comment.getText());
        assertEquals("Owner", comment.getAuthorName());

        ItemBookingsDto fetchedItem = itemService.getItem(itemDto.getId());
        assertEquals(1, fetchedItem.getComments().size());
        assertEquals(comment.getText(), fetchedItem.getComments().get(0).getText());
    }
}
