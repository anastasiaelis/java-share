package ru.practicum.shareit.booking;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,//бронирование отклонено владельцем
    CANCELED // бронирование отменено создателем

}