package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return new ItemRequestDto(
                itemRequest.getId(),
                UserMapper.mapToUserDto(itemRequest.getUser()),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
