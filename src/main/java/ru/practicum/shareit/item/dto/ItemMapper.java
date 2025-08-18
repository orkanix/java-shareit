package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.mapToUserDto(item.getOwner()),
                ItemRequestMapper.mapToItemRequestDto(item.getRequest() == null ? null : item.getRequest())
        );
    }

    public static Item mapToItem(NewItemDto newItemDto, User user) {
        Item item = new Item();
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemDto updateItemDto) {
        if (updateItemDto.hasName()) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.hasDescription()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.hasAvailable()) {
            item.setAvailable(updateItemDto.getAvailable());
        }

        return item;
    }

    public static ItemBookingsDto mapToItemBookingsDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return ItemBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserMapper.mapToUserDto(item.getOwner()))
                .request(ItemRequestMapper.mapToItemRequestDto(item.getRequest() == null ? null : item.getRequest()))
                .lastBooking(lastBooking != null ? BookingMapper.mapToBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? BookingMapper.mapToBookingDto(nextBooking) : null)
                .comments(comments.stream().map(comment -> CommentMapper.mapToCommentDto(comment)).collect(Collectors.toList()))
                .build();
    }
}