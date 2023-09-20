package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private ItemRequestDto requestDtoInput = new ItemRequestDto(
            1L,
            "description"
    );
    static final String USER_ID = "X-Sharer-User-Id";

    @Test
    public void addRequestTest() throws Exception {
        ItemRequestDto requestDtoInput = new ItemRequestDto(
                1L,
                "description"
        );
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto requestDto = requestDtoInput;
        requestDto.setCreated(created);

        when(itemRequestService.addRequest(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .addRequest(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    public void getAllByRequestorTest() throws Exception {
        when(itemRequestService.getAllByRequestor(any(Long.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1))
                .getAllByRequestor(any(Long.class));
    }

    @Test
    public void getAllTest() throws Exception {
        when(itemRequestService.getAll(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "5")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1))
                .getAll(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        ItemRequestDto itemRequestDto = requestDtoInput;
        itemRequestDto.setItems(new ArrayList<>());

        when(itemRequestService.getRequestById(any(Long.class), any(Long.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems()), List.class));

        verify(itemRequestService, times(1))
                .getRequestById(any(Long.class), any(Long.class));
    }
}
