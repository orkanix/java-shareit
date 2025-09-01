package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private NewUserDto newUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        newUserDto = NewUserDto.builder()
                .name("John")
                .email("john@mail.com")
                .build();

        updateUserDto = UpdateUserDto.builder()
                .name("Johnny")
                .email("johnny@mail.com")
                .build();
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(newUserDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(newUserDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_AlreadyExists() {
        when(userRepository.existsByEmail(newUserDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> userService.createUser(newUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.getUserById(1L));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updated = userService.updateUser(1L, updateUserDto);

        assertEquals(user.getName(), updated.getName());
        assertEquals(user.getEmail(), updated.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void updateUser_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updated = userService.updateUser(user.getId(), updateUserDto);

        assertNotNull(updated);
        assertEquals("Johnny", updated.getName());
        assertEquals("johnny@mail.com", updated.getEmail());
    }

    @Test
    void updateUser_emailAlreadyExists() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("johnny@mail.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDto.getEmail())).thenReturn(Optional.of(anotherUser));

        assertThrows(UserAlreadyExists.class,
                () -> userService.updateUser(user.getId(), updateUserDto));
    }

    @Test
    void updateUser_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,
                () -> userService.updateUser(1L, updateUserDto));
    }

    @Test
    void updateUser_emailChanged_conflict_throwsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail(updateUserDto.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDto.getEmail())).thenReturn(Optional.of(anotherUser));

        assertThrows(UserAlreadyExists.class, () -> userService.updateUser(user.getId(), updateUserDto));
    }

}