package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void toBookingDtoTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024,12,12,12,12,12);
        LocalDateTime end = start.plusDays(5);
        BookingDto bookingDto = new BookingDto(
                start,
                end,
                new Item("item", "some item", true));

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.toString());

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.toString());
    }
}
