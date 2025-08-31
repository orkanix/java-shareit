package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private User owner;
    private Item item;
    private NewItemDto newItemDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setOwner(owner);

        newItemDto = NewItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .requestId(null)
                .build();
    }

    @Test
    void createItem_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto created = itemService.createItem(newItemDto, 1L);

        assertNotNull(created);
        assertEquals("Item1", created.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItem_success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItem_Id(1L)).thenReturn(List.of());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(List.of());

        ItemBookingsDto result = itemService.getItem(1L);

        assertEquals(item.getName(), result.getName());
        assertNotNull(result.getComments());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItem_notFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> itemService.getItem(1L));
    }

    @Test
    void createComment_success() {
        NewCommentDto newCommentDto = new NewCommentDto("Nice item");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(newCommentDto.getText());
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(newCommentDto, 1L, 2L);

        assertNotNull(result);
        assertEquals("Nice item", result.getText());
        assertEquals(1L, result.getId());
        assertEquals("Owner", result.getAuthorName());
    }

}
