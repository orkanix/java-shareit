package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 255, message = "Размер описания не более 255 символов")
    String description;
    @NotNull(message = "Доступность не может быть не заполнена")
    Boolean available;
    Long requestId;
}
