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
    long id;
    String name;
    String description;
    @Getter
    @Column(name = "is_available")
    Boolean isAvailable;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    Request request;

    public Item(String name, String description, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public Item(int id, String name, String description, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}
