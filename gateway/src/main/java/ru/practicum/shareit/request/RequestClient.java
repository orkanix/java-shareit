package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.IRequestDto;

import java.util.Map;

@Slf4j
@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long userId, IRequestDto requestDto) {
        log.info("Creating request {}", requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getRequest(long userId) {
        log.info("Get request {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        log.info("Retrieving request {} with userId {}", requestId, userId);
        return get("/{requestId}", userId, Map.of("requestId", requestId));
    }
}