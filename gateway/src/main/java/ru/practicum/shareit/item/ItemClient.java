package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemRequestDto itemRequestDto) {
        log.info("Sending create item request");
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        log.info("Sending create comment request");
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }

    public ResponseEntity<Object> getItem(long itemId, long userId) {
        log.info("Sending get item request");
        return get("/" + itemId, userId);
    }
}