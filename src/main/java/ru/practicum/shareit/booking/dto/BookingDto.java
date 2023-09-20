package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @NotNull
    Long id;
    @Future
    LocalDateTime start;
    @Future
    LocalDateTime end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    @NotNull
    BookingStatus status;

    public BookingDto(LocalDateTime start, LocalDateTime end, Item item) {
        this.start = start;
        this.end = end;
        this.item = item;
    }
}
