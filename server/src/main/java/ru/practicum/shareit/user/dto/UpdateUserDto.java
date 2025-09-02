package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDto {
    String name;
    String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
