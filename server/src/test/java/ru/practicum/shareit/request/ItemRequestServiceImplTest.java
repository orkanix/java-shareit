package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestServiceImpl service;

    private User user;
    private ItemRequest itemRequest;

    Sort newestFirst;

    @BeforeEach
    void setUp() {
        newestFirst = Sort.by(Sort.Direction.DESC, "created");
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@mail.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setUser(user);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_success() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Need a drill");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = service.createRequest(1L, dto);

        assertNotNull(result);
        assertEquals(dto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequestById_success() {
        // Создаем item и owner
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setOwner(owner);  // <--- очень важно

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(1L)).thenReturn(List.of(item));

        ItemRequestAnswersDto result = service.getRequestById(1L);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequestsWithAnswers_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByUser_Id(1L, newestFirst)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of());

        List<ItemRequestAnswersDto> results = service.getRequestsWithAnswers(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(itemRequest.getId(), results.get(0).getId());
    }

    @Test
    void getAllRequests_success() {
        when(itemRequestRepository.findAll(newestFirst)).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> results = service.getAllRequests();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(itemRequest.getDescription(), results.get(0).getDescription());
    }
}
