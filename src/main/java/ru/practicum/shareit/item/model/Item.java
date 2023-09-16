package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
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
    @ManyToOne(targetEntity = User.class)
    User owner;
    @ManyToOne(targetEntity = ItemRequest.class)
    ItemRequest request;

    public Item(String name, String description, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}
