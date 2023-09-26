package ru.practicum.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String ITEM_ID = "/{itemId}";

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping(ITEM_ID)
    public ItemDto updateItem(@RequestHeader(USER_ID) Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping(ITEM_ID)
    public ItemDto getItemById(@RequestHeader(USER_ID) Long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(USER_ID) Long userId) {
        return itemService.getAllItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text", required = false) String text,
                                    @RequestHeader(USER_ID) Long userId) {
        return itemService.searchItem(text, userId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoOutput addComment(@Valid @RequestBody CommentDto commentDto,
                                       @PathVariable long itemId,
                                       @RequestHeader(USER_ID) Long userId) {
        return itemService.addComment(itemId, commentDto, userId);
    }
}
