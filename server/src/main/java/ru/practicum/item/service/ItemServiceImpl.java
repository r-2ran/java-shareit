package ru.practicum.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.exception.BookingException;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.exception.NoSuchItemFound;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.exception.NoSuchUserFound;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.booking.mapper.BookingMapper.fromBooking;
import static ru.practicum.item.mapper.CommentMapper.*;
import static ru.practicum.item.mapper.ItemMapper.*;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private final Sort sortNext = Sort.by(Sort.Direction.ASC, "start");

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) throws NoSuchUserFound {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no such user id = %d, " +
                        "so cannot add item", userId)));
        Item item = toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
            return toItemDto(itemRepository.save(item));
        }
        return toItemDto(itemRepository.save(item));

    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId)
            throws NoSuchUserFound, NoSuchItemFound {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemFound(String.format("no such item id = %d", itemId)));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no such user id = %d", userId)));
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
    public ItemDto getItemById(Long itemId, Long userId) throws NoSuchUserFound,
            NoSuchItemFound {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no user id = %d", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemFound(String.format("no such item id = %d", itemId)));

        ItemDto itemDto = toItemDto(item);
        itemDto.setComments(CommentMapper.toCommentDtosList(commentRepository.findAll()));
        if (itemDto.getOwner().getId().equals(userId)) {
            itemDto = addBooking(itemId, itemDto);
        }
        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItem(String text, Long userId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return toItemDtoList(itemRepository.search(text));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) throws NoSuchUserFound {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserFound(String.format("no user id = %d", userId)));
        List<ItemDto> items = toItemDtoList(itemRepository.findAllByOwnerIdOrderByIdAsc(userId));
        for (ItemDto itemDto : items) {
            if (itemDto.getOwner().getId().equals(userId)) {
                itemDto = addBooking(itemDto.getId(), itemDto);
            }
        }
        return items;
    }

    @Transactional
    @Override
    public CommentDtoOutput addComment(Long itemId, CommentDto commentDto, Long userId)
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

    public ItemDto addBooking(Long itemId, ItemDto item) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookingsLast = bookingRepository.findAllByItemIdAndStartIsBeforeAndStatusNot(itemId, time, sort,
                BookingStatus.REJECTED);
        Optional<Booking> lastBooking = bookingsLast
                .stream()
                .findFirst();
        if (lastBooking.isPresent())
            item.setLastBooking(fromBooking(lastBooking.get()));

        List<Booking> bookingsNext = bookingRepository
                .findAllByItemIdAndStartIsAfterAndStatusNot(itemId, time, sortNext,
                        BookingStatus.REJECTED);
        Optional<Booking> nextBooking = bookingsNext
                .stream()
                .findFirst();
        if (nextBooking.isPresent())
            item.setNextBooking(fromBooking(nextBooking.get()));
        return item;
    }
}
