package ru.practicum.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

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
}
