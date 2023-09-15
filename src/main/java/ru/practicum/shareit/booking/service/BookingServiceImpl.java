package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(BookingDtoInput bookingDtoInput, long userId) throws
            NoSuchItemFound, NoSuchUserFound, BookingException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        User booker = userRepository.findById(userId).get();
        if (itemRepository.findById(bookingDtoInput.getItemId()).isEmpty()) {
            throw new NoSuchItemFound(String.format("no such item id =%d",
                    bookingDtoInput.getItemId()));
        }
        Item item = itemRepository.findById(bookingDtoInput.getItemId()).get();
        if (booker.getId() != userId) {
            throw new NoSuchUserFound(String.format("no owner user id =%d for item id = %d", userId,
                    bookingDtoInput.getItemId()));
        }
        if (booker.getId() == item.getOwner().getId()) {
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
    public void deleteBooking(long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(long bookingId, long userId, boolean approved) throws
            NoSuchItemFound, NoSuchUserFound, BookingException, NoSuchBookingFound {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NoSuchBookingFound(String.format("no booking id = %d", bookingId));
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        if (userRepository.findById(userId).get().getId() != userId) {
            throw new NoSuchUserFound(String.format("no true user id =%d for item id = %d", userId,
                    booking.getItem().getId()));
        }
        if (itemRepository.findById(booking.getItem().getId()).get().getOwner().getId() != userId) {
            throw new NoSuchBookingFound(String.format("user id = %d have not access to approving",
                    userId));
        }
        if (bookingRepository.findById(bookingId).get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException(String.format("booking id = %d is already approved", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto getBookingById(long bookingId, long userId) throws NoSuchBookingFound,
            NoSuchUserFound {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NoSuchBookingFound(String.format("no booking id = %d", bookingId));
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        User user = userRepository.findById(userId).get();
        if (bookingRepository.findById(bookingId).get().getBooker().getId() != userId
                && userRepository.findById(userId).get().getId() != userId) {
            throw new NoSuchUserFound(String.format("user id = %d cannot have access" +
                    " to booking id = %d info", userId, booking.getItem().getId()));
        }
        if (booking.getBooker().getId() != user.getId()
                && booking.getItem().getOwner().getId() != user.getId()) {
            throw new NoSuchBookingFound(String.format("no booking id = %d and user id = %d",
                    bookingId, userId));
        }

        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
    }

    @Transactional
    @Override
    public List<BookingDto> getBookingByBooker(long userId, String stateStr)
            throws BookingException, NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByBookerId(userId, sort));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, date, date, sort));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndIsBefore(userId, date, sort));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartIsAfter(userId, date, sort));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.WAITING,
                        sort));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.REJECTED,
                        sort));
                break;
            default:
                throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public List<BookingDto> getBookingByOwner(long userId, String stateStr) throws BookingException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id =%d", userId));
        }
        List<Booking> bookings;
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, date, date, sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, date, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, date, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.WAITING,
                        sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.REJECTED,
                        sort);
                break;
            default:
                throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toBookingDtoList(bookings);
    }
}
