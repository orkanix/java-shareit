package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUserById(Long id);

    UserDto createUser(NewUserDto newUserDto);

    UserDto updateUser(Long id, UpdateUserDto updateUserDto);

    void deleteUser(Long id);
}
