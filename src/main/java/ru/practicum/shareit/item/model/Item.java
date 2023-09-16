package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.Request;
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
    @Column(name = "item_id")
    long id;
    @Column(name = "name")
    String name;
    @Column(name = "description")
    String description;
    @Getter
    @Column(name = "is_available")
    Boolean isAvailable;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    Request request;

    public Item(String name, String description, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}
