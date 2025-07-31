package ru.practicum.shareit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto savedUser;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "user" + System.currentTimeMillis() + "@mail.ru";
        NewUserDto newUserDto = NewUserDto.builder()
                .name("User")
                .email(uniqueEmail)
                .build();
        savedUser = userService.createUser(newUserDto);
    }


    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            userService.deleteUser(savedUser.getId());
            savedUser = null;
        }
    }

    @Test
    void testCreateUser() {
        NewUserDto newUserDto = NewUserDto.builder()
                .name("TestUser")
                .email("testuser@mail.ru")
                .build();

        UserDto createdUser = userService.createUser(newUserDto);

        assertNotNull(createdUser);
        assertEquals(newUserDto.getName(), createdUser.getName());
        assertEquals(newUserDto.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getId());
    }

    @Test
    void testGetUserById() {
        UserDto userDto = userService.getUserById(savedUser.getId());

        assertNotNull(userDto);
        assertEquals(savedUser.getName(), userDto.getName());
        assertEquals(savedUser.getEmail(), userDto.getEmail());
    }

    @Test
    void testUpdateUser() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("UpdatedUser")
                .email("updateduser@mail.ru")
                .build();

        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateUserDto);

        assertEquals("UpdatedUser", updatedUser.getName());
        assertEquals("updateduser@mail.ru", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        NewUserDto newUserDto = NewUserDto.builder()
                .name("ToDelete")
                .email("todelete@mail.ru")
                .build();

        UserDto userToDelete = userService.createUser(newUserDto);
        Long idToDelete = userToDelete.getId();

        userService.deleteUser(idToDelete);

        assertThrows(UserNotFound.class, () -> userService.getUserById(idToDelete));
    }
}
