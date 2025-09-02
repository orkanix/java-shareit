package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestAnswersDto> getRequestsWithAnswers(Long userId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestAnswersDto getRequestById(Long requestId);
}
