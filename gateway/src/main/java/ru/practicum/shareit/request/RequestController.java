package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid RequestDto requestDto) {
        log.info("Creating request");
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Retrieving requests");
        return requestClient.getRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Retrieving request {} with userId {}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
