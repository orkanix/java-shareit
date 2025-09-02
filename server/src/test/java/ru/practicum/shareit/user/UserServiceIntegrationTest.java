package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateAndGetUser() {
        NewUserDto newUserDto = NewUserDto.builder()
                .name("IntegrationTestUser")
                .email("integration@mail.com")
                .build();

        UserDto createdUser = userService.createUser(newUserDto);

        assertNotNull(createdUser.getId());
        assertEquals(newUserDto.getName(), createdUser.getName());
        assertEquals(newUserDto.getEmail(), createdUser.getEmail());

        UserDto fetchedUser = userService.getUserById(createdUser.getId());

        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals(createdUser.getName(), fetchedUser.getName());
        assertEquals(createdUser.getEmail(), fetchedUser.getEmail());

        Long nonExistentId = createdUser.getId() + 1000;
        assertThrows(UserNotFound.class, () -> userService.getUserById(nonExistentId));
    }
}
