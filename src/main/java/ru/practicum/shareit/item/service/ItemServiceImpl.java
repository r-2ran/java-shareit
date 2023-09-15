package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NoSuchItemFound;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(user);
            return ItemMapper.toItemDto(itemRepository.save(item));
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
        return ItemMapper.toItemDto(itemRepository.save(item));
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
        ItemDto item = ItemMapper.toItemDto(itemRepository.findById(itemId).get());
        item.setComments(CommentMapper.toCommentDtosList(commentRepository.findAll()));
        if (itemRepository.findById(itemId).get().getOwner().getId() == userId
                && !bookingRepository.findAllByItemId(itemId, Sort.unsorted()).isEmpty()) {
            Sort sortLast = Sort.by(Sort.Direction.ASC, "start");
            Sort sortNext = Sort.by(Sort.Direction.DESC, "start");
            if (!bookingRepository.findAllByItemId(itemId, sortLast).isEmpty()) {
                item.setLastBooking(BookingMapper.fromBooking(
                        bookingRepository.findAllByItemId(itemId, sortLast).get(0)));
            } else {
                item.setLastBooking(null);
            }
            if (item.getLastBooking() == null) {
                item.setNextBooking(null);
            } else {
                item.setNextBooking(BookingMapper.fromBooking(
                        bookingRepository.findAllByItemId(itemId, sortNext).get(0)));
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
        return ItemMapper.toItemDtoList(itemRepository.search(text));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemsByUser(long userId) throws NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        Sort sortLast = Sort.by(Sort.Direction.ASC, "start");
        Sort sortNext = Sort.by(Sort.Direction.DESC, "start");
        List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByOwnerId(userId));
        for (ItemDto itemDto : items) {
            if (itemRepository.findById(itemDto.getId()).get().getOwner().getId() == userId
                    && !bookingRepository.findAllByItemId(itemDto.getId(), Sort.unsorted()).isEmpty()) {
                itemDto.setLastBooking(BookingMapper.fromBooking(
                        bookingRepository.findAllByItemId(itemDto.getId(), sortLast).get(0)));
                itemDto.setNextBooking(BookingMapper.fromBooking(
                        bookingRepository.findAllByItemId(itemDto.getId(), sortNext).get(0)));
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
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.commentToOutput(commentRepository.save(comment));
    }
}
