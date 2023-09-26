package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    JacksonTester<BookingDto> json;
    private LocalDateTime date =
            LocalDateTime.of(2020, 12, 12, 12, 12, 12);

    @Test
    void test() throws Exception {
        User user = new User(1L, "user", "user@mail.ru");
        User owner = new User(1L, "owner", "owner@mail.ru");
        Item item = new Item(1L, "name", "some item", true, owner, null);


        BookingDto bookingDto = new BookingDto(
                1L,
                date.plusHours(5),
                date.plusDays(5),
                item,
                user,
                BookingStatus.WAITING
        );
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(BookingStatus.WAITING.toString());

    }
}
