package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    static final String USER_ID = "X-Sharer-User-Id";
    LocalDateTime start = LocalDateTime.now();


    BookingDtoInput bookingDtoInput = new BookingDtoInput(
            1L,
            start,
            start.plusDays(3)
    );

    BookingDto bookingDto = new BookingDto(
            1L,
            start,
            start.plusDays(3),
            new Item(),
            new User(),
            BookingStatus.WAITING
    );

//    @Test
//    public void addBookingTest() throws Exception {
//        when(bookingService.addBooking(any(BookingDtoInput.class), any(Long.class)))
//                .thenReturn(bookingDto);
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingDtoInput))
//                        .header(USER_ID, 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
//
//        verify(bookingService, times(1))
//                .addBooking(any(BookingDtoInput.class), any(Long.class));
//    }

    @Test
    public void updateBookingTest() throws Exception {

        when(bookingService.updateBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1))
                .updateBooking(any(Long.class), any(Long.class), any(Boolean.class));
    }

    @Test
    public void getByIdTest() throws Exception {

        when(bookingService.getBookingById(any(Long.class), any(Long.class)))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1))
                .getBookingById(any(Long.class), any(Long.class));
    }

    @Test
    public void getAllByBookerTest() throws Exception {
        when(bookingService
                .getBookingByBooker(any(Long.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "5")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getBookingByBooker(any(Long.class), any(String.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void getAllByOwnerTest() throws Exception {
        when(bookingService
                .getBookingByOwner(any(Long.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getBookingByOwner(any(Long.class), any(String.class), any(Integer.class), any(Integer.class));
    }
}

