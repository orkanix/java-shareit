package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    private ItemRequestDto requestDto;
    private ItemRequestAnswersDto answersDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto(1L, null, "Need a drill", LocalDateTime.now());
        answersDto = new ItemRequestAnswersDto(1L, null, "Need a drill", LocalDateTime.now(), List.of());
    }

    @Test
    void createRequest_success() throws Exception {
        NewItemRequestDto newDto = new NewItemRequestDto("Need a drill");
        when(service.createRequest(anyLong(), any(NewItemRequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getRequestsWithAnswers_success() throws Exception {
        when(service.getRequestsWithAnswers(anyLong())).thenReturn(List.of(answersDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(answersDto.getId()));
    }

    @Test
    void getAllRequests_success() throws Exception {
        when(service.getAllRequests()).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));
    }

    @Test
    void getRequestById_success() throws Exception {
        when(service.getRequestById(anyLong())).thenReturn(answersDto);

        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(answersDto.getId()));
    }
}
