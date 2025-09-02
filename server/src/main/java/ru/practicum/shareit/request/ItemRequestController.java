package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody NewItemRequestDto newItemRequestDto) {
        return itemRequestService.createRequest(userId, newItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestAnswersDto> getRequestsWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsWithAnswers(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestAnswersDto getRequestById(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
