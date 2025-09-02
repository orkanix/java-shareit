package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setName("TestUser");
        savedUser.setEmail("testuser" + System.currentTimeMillis() + "@mail.ru");
        savedUser = userRepository.save(savedUser);
    }

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            userRepository.delete(savedUser);
            savedUser = null;
        }
    }

    @Test
    void createAndGetRequest_success() throws Exception {
        NewItemRequestDto newRequest = new NewItemRequestDto("Need a drill");

        String jsonRequest = objectMapper.writeValueAsString(newRequest);

        String responseJson = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ItemRequestDto createdRequest = objectMapper.readValue(responseJson, ItemRequestDto.class);

        assertNotNull(createdRequest.getId());
        assertEquals(newRequest.getDescription(), createdRequest.getDescription());

        mockMvc.perform(get("/requests/{requestId}", createdRequest.getId())
                        .header("X-Sharer-User-Id", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(newRequest.getDescription()));
    }
}
