package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static ItemRequest toRequest(ItemRequestDto requestDto) {
        return new ItemRequest(
                requestDto.getDescription(),
                requestDto.getRequestor(),
                requestDto.getCreated()
        );
    }

    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated()
        );
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(new ItemRequestDto(
                            itemRequest.getId(),
                            itemRequest.getDescription(),
                            itemRequest.getRequestor(),
                            itemRequest.getCreated()
                    )
            );
        }
        return result;
    }

    public static List<ItemRequestDto> fromPage(Page<ItemRequest> itemRequestDtos) {
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestDtos) {
            result.add(new ItemRequestDto(
                            itemRequest.getId(),
                            itemRequest.getDescription(),
                            itemRequest.getRequestor(),
                            itemRequest.getCreated()
                    )
            );
        }
        return result;
    }
}
