package ru.practicum.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(@NotNull Item item) throws NullPointerException {
        if (item.getRequest() != null) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getIsAvailable(),
                    item.getOwner(),
                    item.getRequest().getId()
            );
        } else {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getIsAvailable(),
                    item.getOwner()
            );
        }
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {

            if (item.getRequest() != null) {
                itemDtos.add(new ItemDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getIsAvailable(),
                        item.getOwner(),
                        item.getRequest().getId())
                );
            } else {
                itemDtos.add(new ItemDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getIsAvailable(),
                        item.getOwner())
                );
            }
        }
        return itemDtos;
    }
}
