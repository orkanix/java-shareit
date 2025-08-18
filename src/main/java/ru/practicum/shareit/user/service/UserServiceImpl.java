package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        log.info("Сохранение пользователя с email: {}",  newUserDto.getEmail());
        if (userRepository.existsByEmail(newUserDto.getEmail())) {
            throw new UserAlreadyExists(newUserDto.getEmail());
        }

        User newUser = userRepository.save(UserMapper.mapToUser(newUserDto));
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        log.info("Обновление пользователя с id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        if (updateUserDto.hasEmail()
                && !updateUserDto.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(updateUserDto.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new UserAlreadyExists("Этот email принадлежит другому пользователю");
                    });
        }

        User updatedUser = UserMapper.updateUserFields(user, updateUserDto);

        updatedUser = userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }


    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        userRepository.delete(userRepository.findById(id).get());
        log.info("Успешное удаление пользователя с id: {}", id);
    }
}
