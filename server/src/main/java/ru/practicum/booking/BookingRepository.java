package ru.practicum.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                 LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusEquals(long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start,
                                                                    LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemId(long itemId, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBeforeAndStatusEquals(long bookerId,
                                                                 LocalDateTime end,
                                                                 BookingStatus status);

    List<Booking> findAllByItemIdAndStartIsAfter(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndStartIsBefore(long itemId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndEndIsAfterAndStatusNot(long itemId, LocalDateTime start, Sort sort,
                                                           BookingStatus status);

    List<Booking> findAllByItemIdAndStartIsBeforeAndStatusNot(long itemId, LocalDateTime start, Sort sort,
                                                            BookingStatus status);

    List<Booking> findAllByItemIdAndStartIsAfterAndStatusNot(long itemId, LocalDateTime start, Sort sort,
                                                             BookingStatus status);


}
