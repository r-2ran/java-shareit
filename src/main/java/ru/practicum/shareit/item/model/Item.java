package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Item {
    int id;
    String name;
    String description;
    @Getter
    Boolean isAvailable;
    User owner;
    ItemRequest request;

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
