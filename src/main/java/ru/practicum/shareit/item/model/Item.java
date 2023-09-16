package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "name")
    String name;
    @Column(name = "description")
    String description;
    @Getter
    @Column(name = "is_available")
    Boolean isAvailable;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;
//    @ManyToOne
//    @JoinColumn(name = "request_id")
//    ItemRequest request;
    //  неиспользуемый сейчас функционал, из-за него не запускаются тесты

    public Item(String name, String description, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}
