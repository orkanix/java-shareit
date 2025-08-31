package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestAnswersDto mapToItemRequestDtoWithAnswers(ItemRequest itemRequest, List<AnswerDto> answers) {
        return new ItemRequestAnswersDto(
                itemRequest.getId(),
                UserMapper.mapToUserDto(itemRequest.getUser()),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                answers
        );
    }

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

    public static ItemRequest mapToItemRequest(NewItemRequestDto newItemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newItemRequestDto.getDescription());
        itemRequest.setUser(user);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }
}
