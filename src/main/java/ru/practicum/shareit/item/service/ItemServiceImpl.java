package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserServiceImpl userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        Optional<UserDto> user = Optional.ofNullable(userService.getUser(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner((UserMapper.toUser(user.get())));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        }
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }


    @Override
    @Transactional
    public ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto) {
        Optional<UserDto> user = Optional.ofNullable(userService.getUser(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с " + itemId + " не существует")
                );
        if (!UserMapper.toUser(user.get()).equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с id = " + userId +
                    " не является собственником вещи id = " + itemId);
        }
        Boolean isAvailable = itemDto.getAvailable();
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        String description = itemDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        String name = itemDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        return ItemMapper.toItemDtoOut(item);
    }


    @Override
    @Transactional
    public ItemDtoOut findItemById(Long userId, Long itemId) {
        //  userService.getUser(userId);
        Item itemGet = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("У пользователя с id = " + userId + " не " +
                        "существует вещи с id = " + itemId));

        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(itemGet);
        itemDtoOut.setComments(getAllItemComments(itemId));
        if (!itemGet.getOwner().getId().equals(userId)) {
            return itemDtoOut;
        }
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(itemGet, BookingStatus.APPROVED);
        List<BookingDtoOut> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(toList());

        itemDtoOut.setLastBooking(getLastBooking(bookingDTOList, LocalDateTime.now()));
        itemDtoOut.setNextBooking(getNextBooking(bookingDTOList, LocalDateTime.now()));
        return itemDtoOut;
    }


    @Override
    @Transactional
    public List<ItemDtoOut> findAll(Long userId, Integer from, Integer size) {
        Optional<UserDto> owner = Optional.ofNullable(userService.getUser(userId));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, List<CommentDtoOut>> comments = commentRepository.findAllByItemIdIn(idList)
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Long, List<BookingDtoOut>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingDtoOut::getItemId, toList()));

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }


    @Override
    @Transactional
    public List<ItemDtoOut> search(Long userId, String text, Integer from, Integer size) {
        userService.getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.search(text, pageable);
        return itemList.stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.toUser(userService.getUser(userId));

        Item itemById = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("У пользователя с id = " + userId + " не " +
                        "существует вещи с id = " + itemId));


        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("У пользователя с id   " + userId + " должно быть хотя бы одно бронирование предмета с id " + itemId);
        }

        return CommentMapper.toCommentDtoOut(commentRepository.save(CommentMapper.toComment(commentDto, itemById, user)));
    }

    public List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    private BookingDtoOut getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingDtoOut getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}