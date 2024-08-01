package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        ItemRequest request = ItemRequestMapper.toRequest(user, itemRequestDto);
        request.setRequester(user);
        return ItemRequestMapper.toRequestDtoOut(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOut> getUserRequests(Long userId) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterId(userId);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest requestById = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не был найден."));

        return ItemRequestMapper.toRequestDtoOut(requestById);
    }
}

