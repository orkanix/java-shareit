package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public static User mapToUser(NewUserDto newUserDto) {
        User user = new User();
        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        return user;
    }

    public static User updateUserFields(User user, UpdateUserDto updateUserDto) {
        if (updateUserDto.hasName()) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.hasEmail()) {
            user.setEmail(updateUserDto.getEmail());
        }

        return user;
    }
}
