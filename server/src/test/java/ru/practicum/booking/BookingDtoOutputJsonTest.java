package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoOutputJsonTest {
    @Autowired
    JacksonTester<BookingDtoInput> json;
    private LocalDateTime date =
            LocalDateTime.of(2020, 12, 12, 12, 12, 12);

    @Test
    void test() throws Exception {
        BookingDtoInput bookingDtoInput = new BookingDtoInput(
                1L,
                date.plusHours(5),
                date.plusDays(5)
        );
        JsonContent<BookingDtoInput> result = json.write(bookingDtoInput);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

    }
}
