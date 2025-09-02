package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemBookingsDto itemBookingsDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Item1", "Description1", true, null, null);
        commentDto = new CommentDto(1L, "Nice item", itemDto, "Owner", LocalDateTime.now());
        itemBookingsDto = ItemBookingsDto.builder()
                .id(1L)
                .name("Item1")
                .description("Description1")
                .available(true)
                .owner(null)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of(commentDto))
                .build();
    }

    @Test
    void getItems_success() throws Exception {
        when(itemService.getItems(1L)).thenReturn(List.of(itemBookingsDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemBookingsDto.getId()))
                .andExpect(jsonPath("$[0].comments[0].text").value("Nice item"));
    }

    @Test
    void getItem_success() throws Exception {
        when(itemService.getItem(1L)).thenReturn(itemBookingsDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingsDto.getId()))
                .andExpect(jsonPath("$.comments[0].text").value("Nice item"));
    }

    @Test
    void searchItem_success() throws Exception {
        when(itemService.searchItem("Item")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void createItem_success() throws Exception {
        NewItemDto newItemDto = new NewItemDto("Item1", "Description1", true, null);

        when(itemService.createItem(any(NewItemDto.class), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem_success() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto("Updated", null, null);

        when(itemService.updateItem(any(UpdateItemDto.class), eq(1L), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void createComment_success() throws Exception {
        NewCommentDto newCommentDto = new NewCommentDto("Nice item");

        when(itemService.createComment(any(NewCommentDto.class), eq(1L), eq(1L)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}
