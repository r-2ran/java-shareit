package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private static final String USER_ID = "X-Sharer-User-Id";

    private UserDto userDto = new UserDto(
            1L,
            "User",
            "user@mail.ri"
    );

    private User owner = new User(
            2L,
            "Owner",
            "owner@mail.ru"
    );

    private ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "some item",
            true,
            owner
    );

    private ItemDto inputItemDto = new ItemDto(
            "name",
            "description",
            true
    );


    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(2L, inputItemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputItemDto))
                        .header(USER_ID, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        verify(itemService, times(1))
                .addItem(any(Long.class), any(ItemDto.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        verify(itemService, times(1))
                .getItemById(any(Long.class), any(Long.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto newItemDto = itemDto;
        newItemDto.setDescription("new description");
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(newItemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(newItemDto))
                        .header(USER_ID, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        verify(itemService, times(1))
                .updateItem(any(Long.class), any(ItemDto.class), any(Long.class));
    }

    @Test
    void getAllItemsByUserTest() throws Exception {
        when(itemService.getAllItemsByUser(anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .getAllItemsByUser(any(Long.class));

    }

    @Test
    public void addCommentTest() throws Exception {
        CommentDto inputCommentDto = new CommentDto(
                "comment"
        );

        CommentDtoOutput outputCommentDto = new CommentDtoOutput(
                1L,
                inputCommentDto.getText(),
                userDto.getName(),
                LocalDateTime.now()
        );

        when(itemService.addComment(any(Long.class), any(CommentDto.class), any(Long.class)))
                .thenReturn(outputCommentDto);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(outputCommentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(outputCommentDto.getText()), String.class));

        verify(itemService, times(1))
                .addComment(any(Long.class), any(CommentDto.class), any(Long.class));
    }
}
