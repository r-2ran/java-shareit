package ru.practicum.shareit.booking.mapper;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking toBookingFromDto(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus()
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }


    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        List<BookingDto> res = new ArrayList<>();
        for (Booking booking : bookings) {
            res.add(new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItem(),
                    booking.getBooker(),
                    booking.getStatus()
            ));
        }
        return res;
    }

    public static Booking toBookingFromInput(BookingDtoInput bookingDtoInput) {
        return new Booking(
                bookingDtoInput.getStart(),
                bookingDtoInput.getEnd()
        );
    }

    public static BookingForItemDto fromBooking(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static List<Booking> fromPage(Page<Booking> bookings) {
        List<Booking> res = new ArrayList<>();
        for (Booking booking : bookings) {
            res.add(new Booking(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItem(),
                    booking.getBooker(),
                    booking.getStatus()
            ));
        }
        return res;
    }
}