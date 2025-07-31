package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItem(String text);

    ItemDto createItem(NewItemDto newItemDto, Long ownerId);

    ItemDto updateItem(UpdateItemDto newItemDto, Long ownerId, Long userId);
}
