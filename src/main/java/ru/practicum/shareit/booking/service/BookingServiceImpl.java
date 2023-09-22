package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.NoSuchBookingFound;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.exception.NoSuchItemFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.mapper.BookingMapper.fromPage;


@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");


    @Transactional
    @Override
    public BookingDto addBooking(BookingDtoInput bookingDtoInput, Long userId) throws
            NoSuchItemFound, NoSuchUserFound, BookingException {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no such user id =%d", userId)));
        Item item = itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() ->
                new NoSuchItemFound(String.format("no such item id =%d",
                        bookingDtoInput.getItemId())));
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NoSuchUserFound(String.format("owner id = %d cannot be booker", userId));
        }
        if (!item.getIsAvailable()) {
            throw new BookingException(String.format("cannot book, item id = %d is already booked",
                    bookingDtoInput.getItemId()));
        }
        Booking booking = BookingMapper.toBookingFromInput(bookingDtoInput);
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BookingException("start and end cannot be equal");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BookingException("end cannot be before start");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long bookingId, Long userId, boolean approved) throws
            NoSuchItemFound, NoSuchUserFound, BookingException, NoSuchBookingFound {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchBookingFound(String.format("no booking id = %d", bookingId)));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no such user id =%d", userId)));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NoSuchBookingFound(String.format("user id = %d have not access to approving",
                    userId));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException(String.format("booking id = %d is already approved", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) throws NoSuchBookingFound,
            NoSuchUserFound {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NoSuchBookingFound(String.format("no booking id = %d", bookingId));
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        User user = userRepository.findById(userId).get();
        if (!Objects.equals(booking.getBooker().getId(), user.getId())
                && !Objects.equals(booking.getItem().getOwner().getId(), user.getId())) {
            throw new NoSuchBookingFound(String.format("no booking id = %d and user id = %d",
                    bookingId, userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingByBooker(Long userId, String stateStr,
                                               int from, int size) throws BookingException, NoSuchUserFound {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no such user id =%d", userId)));
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (from < 0) {
            throw new BookingException(String.format("bad page parameters, from cannot be = %d", from));
        }
        switch (state) {
            case ALL:
                bookings = fromPage(bookingRepository.findAllByBookerId(
                        userId, PageRequest.of(from / size, size, sort)));
                break;
            case CURRENT:
                bookings.addAll(fromPage(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, date, date, PageRequest.of(from, size, sort))));
                break;
            case PAST:
                bookings.addAll(fromPage(bookingRepository.findAllByBookerIdAndEndIsBefore(
                        userId, date, PageRequest.of(from, size, sort))));
                break;
            case FUTURE:
                bookings.addAll(fromPage(bookingRepository.findAllByBookerIdAndStartIsAfter(
                        userId, date, PageRequest.of(from, size, sort))));
                break;
            case WAITING:
                bookings.addAll(fromPage(bookingRepository.findAllByBookerIdAndStatusEquals(
                        userId, BookingStatus.WAITING, PageRequest.of(from, size, sort))));
                break;
            case REJECTED:
                bookings.addAll(fromPage(bookingRepository.findAllByBookerIdAndStatusEquals(
                        userId, BookingStatus.REJECTED, PageRequest.of(from, size, sort))));
                break;
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingByOwner(Long userId, String stateStr,
                                              int from, int size) throws BookingException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (state) {
            case ALL:
                bookings = fromPage(bookingRepository.findAllByItemOwnerId(
                        userId, PageRequest.of(from, size, sort)));
                break;
            case CURRENT:
                bookings = fromPage(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, date, date, PageRequest.of(from, size, sort)));
                break;
            case PAST:
                bookings = fromPage(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                        userId, date, PageRequest.of(from, size, sort)));
                break;
            case FUTURE:
                bookings = fromPage(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                        userId, date, PageRequest.of(from, size, sort)));
                break;
            case WAITING:
                bookings = fromPage(bookingRepository.findAllByItemOwnerIdAndStatusEquals(
                        userId, BookingStatus.WAITING, PageRequest.of(from, size, sort)));
                break;
            case REJECTED:
                bookings = fromPage(bookingRepository.findAllByItemOwnerIdAndStatusEquals(
                        userId, BookingStatus.REJECTED, PageRequest.of(from, size, sort)));
                break;
        }
        return BookingMapper.toBookingDtoList(bookings);
    }
}
