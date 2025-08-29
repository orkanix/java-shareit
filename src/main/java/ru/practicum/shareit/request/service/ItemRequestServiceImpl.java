package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    private final ItemRepository itemRepository;

    Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public ItemRequestDto createRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        log.info("Сохраняю запрос на вещь с описанием: {}", newItemRequestDto.getDescription());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(newItemRequestDto, user));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestAnswersDto> getRequestsWithAnswers(Long userId) {
        log.info("Получение всех запросов пользователя с id: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUser_Id(userId, newestFirst).stream()
                .toList();

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<AnswerDto> answers = itemRepository.findAllByIdIn(requestIds).stream().map(ItemMapper::mapToAnswerDto).toList();
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.mapToItemRequestDtoWithAnswers(itemRequest, answers.stream().filter(answerDto -> answerDto.getId().equals(itemRequest.getId())).toList()))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        log.info("Получаю все запросы на аренду вещей...");
        return itemRequestRepository.findAll(newestFirst).stream().map(ItemRequestMapper::mapToItemRequestDto).toList();
    }

    @Override
    public ItemRequestAnswersDto getRequestById(Long requestId) {
        log.info("Получаю данные о запросе с id: {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFound("Запрос на вещь не найден"));
        List<AnswerDto> answers = itemRepository.findById(itemRequest.getId()).stream().map(ItemMapper::mapToAnswerDto).toList();
        return ItemRequestMapper.mapToItemRequestDtoWithAnswers(itemRequest, answers);
    }
}
