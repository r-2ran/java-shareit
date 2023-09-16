package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NoSuchItemFound;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) throws NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id = %d, " +
                    "so cannot add item,", userId));
        } else {
            User user = userRepository.findById(userId).get();
            Item item = toItem(itemDto);
            item.setOwner(user);
            return toItemDto(itemRepository.save(item));
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId)
            throws NoSuchUserFound, NoSuchItemFound {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NoSuchItemFound(String.format("no such item id = %d", itemId));
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no such user id = %d", userId));
        }
        Item item = itemRepository.findById(itemId).get();
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) throws NoSuchUserFound,
            NoSuchItemFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NoSuchItemFound(String.format("no such item id = %d", itemId));
        }
        ItemDto item = toItemDto(itemRepository.findById(itemId).get());
        item.setComments(CommentMapper.toCommentDtosList(commentRepository.findAll()));
        if (itemRepository.findById(itemId).get().getOwner().getId() == userId) {
            LocalDateTime time = LocalDateTime.now();
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            Sort sortNext = Sort.by(Sort.Direction.ASC, "start");
            Optional<Booking> lastBooking =
                    bookingRepository.findAllByItemIdAndStartIsBefore(
                                    itemId,
                                    time,
                                    sort)
                            .stream()
                            .findFirst();
            lastBooking.ifPresent(booking -> item.setLastBooking(fromBooking(booking)));

            if (lastBooking.isEmpty() &&
                    !bookingRepository.findAllByItemId(itemId, sort).isEmpty()) {
                return item;
            }
            Optional<Booking> nextBooking =
                    bookingRepository.findAllByItemIdAndStartIsAfter(
                                    itemId,
                                    time,
                                    sortNext)
                            .stream()
                            .findFirst();
            nextBooking.ifPresent(booking -> item.setNextBooking(fromBooking(booking)));
            if (nextBooking.isEmpty()) {
                lastBooking = bookingRepository.findAllByItemIdAndEndIsAfter(
                                itemId,
                                time,
                                sort)
                        .stream()
                        .findFirst();
                lastBooking.ifPresent(booking -> item.setLastBooking(fromBooking(booking)));
            }
        }
        return item;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItem(String text, long userId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return toItemDtoList(itemRepository.search(text));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemsByUser(long userId) throws NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        List<ItemDto> items = toItemDtoList(itemRepository.findAllByOwnerId(userId));
        for (ItemDto itemDto : items) {
            if (itemRepository.findById(itemDto.getId()).get().getOwner().getId() == userId
                    && !bookingRepository.findAllByItemId(itemDto.getId(), Sort.unsorted()).isEmpty()) {
                LocalDateTime time = LocalDateTime.now();
                Sort sort = Sort.by(Sort.Direction.DESC, "start");
                Sort sortNext = Sort.by(Sort.Direction.ASC, "start");
                Optional<Booking> lastBooking =
                        bookingRepository.findAllByItemIdAndStartIsBefore(
                                        itemDto.getId(),
                                        time,
                                        sort)
                                .stream()
                                .findFirst();
                lastBooking.ifPresent(booking -> itemDto.setLastBooking(fromBooking(booking)));

                Optional<Booking> nextBooking =
                        bookingRepository.findAllByItemIdAndStartIsAfter(
                                        itemDto.getId(),
                                        time,
                                        sortNext)
                                .stream()
                                .findFirst();
                nextBooking.ifPresent(booking -> itemDto.setNextBooking(fromBooking(booking)));
            }
        }
        return items;
    }

    @Transactional
    @Override
    public CommentDtoOutput addComment(long itemId, CommentDto commentDto, long userId)
            throws NoSuchUserFound, NoSuchItemFound, BookingException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        User user = userRepository.findById(userId).get();
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NoSuchItemFound(String.format("no item id = %d", itemId));
        }
        Item item = itemRepository.findById(itemId).get();
        if (bookingRepository.findAllByBookerIdAndEndIsBeforeAndStatusEquals(
                userId,
                LocalDateTime.now(),
                BookingStatus.APPROVED
        ).isEmpty()) {
            throw new BookingException(String.format("item id = %d by user id = %d" +
                    " has not been rented", itemId, userId));
        }
        Comment comment = toComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        return commentToOutput(commentRepository.save(comment));
    }
}
