package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto requestDto);

    ItemRequestDto getRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getAllByRequestor(Long userId);

    List<ItemRequestDto> getAll(Long userId, int from, int size);
}

