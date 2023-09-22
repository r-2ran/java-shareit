package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.NoSuchRequestFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private LocalDateTime date = LocalDateTime.now();
    private User owner = new User(1L, "owner", "owner@mail.ru");
    private User requestor = new User(2L, "requestor", "requestor@mail.ru");
    private ItemRequest request = new ItemRequest(1L, "description", requestor,
            date.minusHours(5));
    private ItemRequestDto requestDto = new ItemRequestDto(1L, "description",
            date.minusHours(5));
    private Item item = new Item(1L, "item", "decsription", true,
            owner, request);
    private Item itemNoRequest = new Item(1L, "item", "decsription", true,
            owner, null);


    @Test
    void addRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);
        assertEquals(itemRequestService.addRequest(1L, requestDto).getId(), requestDto.getId());
    }

    @Test
    void addRequestWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemRequestService.addRequest(99L, requestDto));

        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        assertEquals(request.getId(), itemRequestService.getRequestById(1L, 2L).getId());
    }

    @Test
    void getRequestByIdItemNotReq() {
        Item item1 = new Item();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item1);

        assertEquals(request.getId(), itemRequestService.getRequestById(1L, 2L).getId());
    }

    @Test
    void getRequestByIdNoUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemRequestService.getRequestById(99L, 99L));

        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }

    @Test
    void getRequestByIdNot() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        final NoSuchRequestFound exception = Assertions.assertThrows(
                NoSuchRequestFound.class,
                () -> itemRequestService.getRequestById(99L, 1L));

        Assertions.assertEquals("no request id = 99", exception.getMessage());
    }

    @Test
    void getAllByRequestor() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findAllByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        assertEquals(request.getId(), itemRequestService
                .getAllByRequestor(requestor.getId()).get(0).getId());
    }

    @Test
    void getAllByRequestorItemNotRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findAllByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        assertEquals(request.getId(), itemRequestService
                .getAllByRequestor(requestor.getId()).get(0).getId());
    }

    @Test
    void getAllByRequestorNotUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(requestRepository.findAllByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemRequestService.getAllByRequestor(99L));

        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }

    @Test
    void getAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findAllByRequestorIdNot(1L, Pageable.unpaged()))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);
        assertTrue(itemRequestService.getAll(owner.getId(), 0, 5).isEmpty());
    }

    @Test
    void getAllItemNotRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findAllByRequestorIdNot(1L, Pageable.unpaged()))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);
        assertTrue(itemRequestService.getAll(owner.getId(), 0, 5).isEmpty());
    }

    @Test
    void getAllWithItems() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findAllByRequestorIdNot(1L, Pageable.unpaged()))
                .thenReturn(List.of(request));

        when(requestRepository.findAllByRequestorIdNot(anyLong(), any()))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);

        assertFalse(itemRequestService.getAll(owner.getId(), 0, 5).isEmpty());
    }

    @Test
    void getAllNoUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(requestRepository.findAllByRequestorIdNot(1L, Pageable.unpaged()))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findByIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(item);
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any()))
                .thenReturn(List.of(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemRequestService.getAll(99L, 0, 5));

        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }
}