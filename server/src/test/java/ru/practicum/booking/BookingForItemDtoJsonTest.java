package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.dto.BookingForItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingForItemDtoJsonTest {
    @Autowired
    JacksonTester<BookingForItemDto> json;

    @Test
    void test() throws Exception {


        BookingForItemDto bookingDto = new BookingForItemDto(
                1L,
                1L
        );
        JsonContent<BookingForItemDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);

    }
}
