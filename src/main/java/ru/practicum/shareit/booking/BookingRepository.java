package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                 LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatusEquals(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                    LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemId(long itemId, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBeforeAndStatusEquals(long bookerId,
                                                                 LocalDateTime end,
                                                                 BookingStatus status);

    List<Booking> findAllByItemIdAndStartIsAfter(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndStartIsBefore(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndEndIsAfter(long itemId, LocalDateTime start, Sort sort);
}
