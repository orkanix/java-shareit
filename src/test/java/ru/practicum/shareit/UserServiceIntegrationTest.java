package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto savedUser;

    @BeforeAll
    void initAll() {
        NewUserDto newUserDto = NewUserDto.builder()
                .name("User")
                .email("user@mail.ru")
                .build();
        savedUser = userService.createUser(newUserDto);
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
        assertEquals("UpdatedUser", userDto.getName());
        assertEquals("updateduser@mail.ru", userDto.getEmail());
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
        // Создаем пользователя для удаления
        NewUserDto newUserDto = NewUserDto.builder()
                .name("ToDelete")
                .email("todelete@mail.ru")
                .build();

        UserDto userToDelete = userService.createUser(newUserDto);
        Long idToDelete = userToDelete.getId();

        // Удаляем
        userService.deleteUser(idToDelete);

        // Проверяем, что пользователь удалён - должен выбросить исключение
        assertThrows(UserNotFound.class, () -> userService.getUserById(idToDelete));
    }
}
