package ru.practicum.request;

import org.junit.jupiter.api.Test;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.request.RequestMapper.*;

class RequestMapperTest {
    private LocalDateTime date = LocalDateTime.now();
    private User requestor = new User(1L, "name", "user@mail.ru");
    private ItemRequest itemRequest = new ItemRequest(1L, "text", requestor, date);
    private List<ItemRequest> list = List.of(itemRequest);

    @Test
    void fromPageTest() {
        List<ItemRequestDto> res = fromPage(list);
        assertEquals(res.get(0).getId(), list.get(0).getId());
        assertEquals(res.get(0).getDescription(), list.get(0).getDescription());
    }
}