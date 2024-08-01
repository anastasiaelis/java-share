package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.ItemComment;

import java.util.List;

public interface CommentRepository extends JpaRepository<ItemComment, Long> {
    List<ItemComment> findAllByItemId(Long itemId);

    List<ItemComment> findAllByItemIdIn(List<Long> itemIds);
}