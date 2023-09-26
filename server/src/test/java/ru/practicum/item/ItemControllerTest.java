package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.item.controller.ItemController;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.exception.NoSuchItemFound;
import ru.practicum.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto = new ItemDto(1L, "item", "description",
            true);
    private CommentDtoOutput commentDtoOutput = new CommentDtoOutput(1, "comment",
            "user", LocalDateTime.now());
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updated = itemDto;
        updated.setDescription("new desc");
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(updated);

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated))
                        .header(USER_ID, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updated.getName())))
                .andExpect(jsonPath("$.description", is(updated.getDescription())))
                .andExpect(jsonPath("$.available", is(updated.getAvailable())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemByIdNotFound() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NoSuchItemFound("not found"));

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByUser() throws Exception {
        when(itemService.getAllItemsByUser(anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchItem() throws Exception {

        when(itemService.searchItem(anyString(), anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {

        when(itemService.addComment(anyLong(), any(), anyLong()))
                .thenReturn(commentDtoOutput);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .content(mapper.writeValueAsString(commentDtoOutput))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOutput.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOutput.getAuthorName())));
    }
}