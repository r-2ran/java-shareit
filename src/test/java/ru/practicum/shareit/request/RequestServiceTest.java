package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
//public class RequestServiceTest {
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//
//    private ItemRepository itemRepository;
//    @Mock
//
//    private ItemRequestService requestService;
//    @Mock
//
//    private ItemRequestRepository requestRepository;
//
//    private User user;
//    private ItemRequest request;
//
//    @BeforeEach
//    public void start() {
//        LocalDateTime date = LocalDateTime.now();
//        user = new User(1L, "name", "user@emali.com");
//        request = new ItemRequest(1L, "description", user, date);
//    }
//
//    @Test
//    public void addRequestTest() {
//        ItemRequestDto inputDto = new ItemRequestDto(1L, request.getDescription());
//
//        when(userRepository.findById(any(Long.class)))
//                .thenReturn(Optional.ofNullable(user));
//
//        when(requestRepository.save(any(ItemRequest.class)))
//                .thenReturn(request);
//
//        ItemRequestDto requestDto = requestService.addRequest(1L, inputDto);
//
//        assertNotNull(requestDto);
//        assertEquals(1L, requestDto.getId());
//        assertEquals(inputDto.getDescription(), requestDto.getDescription());
//    }
//
//}
