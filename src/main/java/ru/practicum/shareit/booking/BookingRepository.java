package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                 LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusEquals(long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                    LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusEquals(long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemId(long itemId, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBeforeAndStatusEquals(long bookerId,
                                                                 LocalDateTime end,
                                                                 BookingStatus status);

    List<Booking> findAllByItemIdAndStartIsAfter(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndStartIsBefore(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndEndIsAfter(long itemId, LocalDateTime start, Sort sort);
}
