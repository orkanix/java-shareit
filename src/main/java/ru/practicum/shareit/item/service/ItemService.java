package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import java.util.List;

public interface ItemService {

    ItemBookingsDto getItem(Long itemId);

    List<ItemBookingsDto> getItems(Long userId);

    List<ItemDto> searchItem(String text);

    ItemDto createItem(NewItemDto newItemDto, Long ownerId);

    ItemDto updateItem(UpdateItemDto newItemDto, Long ownerId, Long userId);

    CommentDto createComment(NewCommentDto newCommentDto, Long itemId, Long userId);
}
