package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.NoSuchRequestFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.request.RequestMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto requestDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        ItemRequest request = toRequest(requestDto);
        User user = userRepository.findById(userId).get();
        request.setRequestor(user);
        LocalDateTime created = LocalDateTime.now();
        request.setCreated(created);
        return toRequestDto(requestRepository.save(request));
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        if (requestRepository.findById(requestId).isEmpty()) {
            throw new NoSuchRequestFound(String.format("no request id = %d", requestId));
        }
        ItemRequestDto requestDto = toRequestDto(requestRepository.findById(requestId).get());
        requestDto.setItems(toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId())));
        for (ItemDto itemDto : requestDto.getItems()) {
            if (itemDto.getRequestId() != null) {
                itemDto.setRequestId(itemRepository.findByIdAndRequestId(
                        itemDto.getId(), itemDto.getRequestId()).getRequest().getId());
            }
        }
        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByRequestor(Long userId) throws NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<ItemRequestDto> requestDtos = toItemRequestDtoList(requestRepository.findAllByRequestorId(userId, sort));


        for (ItemRequestDto requestDto : requestDtos) {
            requestDto.setItems(toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId())));
            for (ItemDto itemDto : requestDto.getItems()) {
                if (itemDto.getRequestId() != null) {
                    itemDto.setRequestId(itemRepository.findByIdAndRequestId(
                            itemDto.getId(), itemDto.getRequestId()).getRequest().getId());
                }
            }
        }


        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<ItemRequestDto> requestDtos = fromPage(requestRepository.findAllByRequestorIdNot(userId,
                PageRequest.of(from, size, sort)));
        for (ItemRequestDto requestDto : requestDtos) {
            requestDto.setItems(toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId())));
            for (ItemDto itemDto : requestDto.getItems()) {
                if (itemDto.getRequestId() != null) {
                    itemDto.setRequestId(itemRepository.findByIdAndRequestId(
                            itemDto.getId(), itemDto.getRequestId()).getRequest().getId());
                }
            }
        }
        return requestDtos;
    }
}