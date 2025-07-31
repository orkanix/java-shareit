package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.exceptions.InvalidItemOwner;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Override
    public ItemDto getItem(Long itemId) {
        log.info("Получение вещи с id: {}", itemId);
        Optional<Item> item = itemRepository.findItemById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFound("Вещь не найдена");
        }
        return ItemMapper.mapToItemDto(item.get());
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("Получение всех вещей с id владельца: {}", userId);
        return itemRepository.findUserItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Поиск вещей по подстроке: {}", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(NewItemDto newItemDto, Long ownerId) {
        log.info("Сохраняю вещь владельца с id: {}", ownerId);
        if (!userRepository.exists(ownerId)) {
            throw new UserNotFound("Пользователь не найден");
        }
        Item item = ItemMapper.mapToItem(newItemDto, ownerId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto updateItemDto, Long userId, Long itemId) {
        log.info("Обновляю вещь пользователя с id: {}", userId);
        Optional<Item> item = itemRepository.findItemById(itemId);

        if (!userRepository.exists(userId)) {
            throw new UserNotFound("Пользователь не найден");
        }
        if (item.isEmpty()) {
            throw new ItemNotFound("Вещь не найдена");
        }
        if (!Objects.equals(item.get().getOwner(), userId)) {
            throw new InvalidItemOwner("Владелец вещи и пользователь не совпадают");
        }

        Item itemUpdated = ItemMapper.updateItemFields(item.get(), updateItemDto);

        return ItemMapper.mapToItemDto(itemRepository.update(itemUpdated));
    }
}
