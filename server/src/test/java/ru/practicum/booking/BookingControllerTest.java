package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.controller.BookingController;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoInput;
import ru.practicum.booking.exception.BookingException;
import ru.practicum.booking.exception.NoSuchBookingFound;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.service.BookingService;
import ru.practicum.item.model.Item;
import ru.practicum.user.exception.NoSuchUserFound;
import ru.practicum.user.model.User;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private LocalDateTime date = LocalDateTime.now();
    private User owner = new User(1L, "owner", "owner@mail.ru");
    private User booker = new User(2L, "booker", "booker@mail.ru");
    private Item item = new Item(1L, "item", "descriptiion", true, owner, null);
    private BookingDto bookingDto = new BookingDto(1L, date.plusHours(5), date.plusDays(5),
            item, booker, BookingStatus.WAITING);
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(any(BookingDtoInput.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void addBookingWrong() throws Exception {
        when(bookingService.addBooking(any(BookingDtoInput.class), anyLong()))
                .thenThrow(new BookingException("error"));

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void getBookingByIdNotFound() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NoSuchBookingFound("not found"));

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingByIdNotFoundUser() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NoSuchUserFound("not found"));

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking() throws Exception {
        BookingDto output = bookingDto;
        output.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBooking(bookingDto.getId(), owner.getId(), true))
                .thenReturn(output);

        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void getBookingsByBooker() throws Exception {
        when(bookingService.getBookingByBooker(booker.getId(), "ALL", 0, 5))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingByOwner(owner.getId(), "ALL", 0, 5))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}