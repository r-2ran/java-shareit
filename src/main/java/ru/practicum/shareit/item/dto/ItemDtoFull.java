package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoFull {

    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @Getter
    @NotNull
    Boolean available;
    @JsonIgnore
    User owner;
    BookingForItemDto lastBooking;
    BookingForItemDto nextBooking;
    List<CommentDtoOutput> comments;

    public ItemDtoFull(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
