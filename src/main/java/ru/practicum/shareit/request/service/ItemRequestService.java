package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto addRequest(long userId, ItemRequestDto requestDto);

    ItemRequestDto getRequestById(long requestId, long userId);

    List<ItemRequestDto> getAllByRequestor(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);
}

