package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.CommentBadRequest;
import ru.practicum.shareit.item.exceptions.InvalidItemOwner;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Override
    public ItemBookingsDto getItem(Long itemId) {
        log.info("Получение вещи с id: {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFound("Вещь не найдена"));

        List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookings.stream()
                .filter(b -> b.getEnd().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now) && b.getStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);

        log.info("Item {}: найдено {} комментариев", item.getId(), comments.size());

        return ItemMapper.mapToItemBookingsDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemBookingsDto> getItems(Long userId) {
        log.info("Получение всех вещей с id владельца: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFound("Пользователь не найден");
        }

        List<Item> items = itemRepository.findItemsByOwnerId(userId);

        return items.stream().map(item -> {
            List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
            LocalDateTime now = LocalDateTime.now();

            Booking lastBooking = bookings.stream()
                    .filter(b -> b.getEnd().isAfter(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(now) && b.getStatus() == BookingStatus.APPROVED)
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());

            return ItemMapper.mapToItemBookingsDto(item, lastBooking, nextBooking, comments);
        }).toList();
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
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));
        Item item = itemRepository.save(ItemMapper.mapToItem(newItemDto, user));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(UpdateItemDto updateItemDto, Long userId, Long itemId) {
        log.info("Обновляю вещь пользователя с id: {}", userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFound("Вещь не найдена"));

        if (!userRepository.existsById(userId)) {
            throw new UserNotFound("Пользователь не найден");
        }
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new InvalidItemOwner("Владелец вещи и пользователь не совпадают");
        }

        Item itemUpdated = ItemMapper.updateItemFields(item, updateItemDto);
        return ItemMapper.mapToItemDto(itemRepository.save(itemUpdated));
    }

    @Override
    public CommentDto createComment(NewCommentDto newCommentDto, Long itemId, Long userId) {
        log.info("Сохраняю комментарий...");
        LocalDateTime now = LocalDateTime.now();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFound("Вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        if (!bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(itemId, userId, now)) {
            throw new CommentBadRequest("Не удалось сохранить комментарий");
        }

        Comment comment = commentRepository.save(CommentMapper.mapToComment(newCommentDto, item, user));
        return CommentMapper.mapToCommentDto(comment);
    }
}