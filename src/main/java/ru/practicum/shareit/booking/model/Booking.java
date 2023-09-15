package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    User booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;

    public Booking(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
}
