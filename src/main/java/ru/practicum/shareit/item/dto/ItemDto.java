package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class ItemDto {

    int id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @Getter
    @NotNull
    Boolean available;
    @JsonIgnore
    int ownerId;

    public ItemDto(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
