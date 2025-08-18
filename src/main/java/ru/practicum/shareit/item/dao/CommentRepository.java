package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findAllByItem_IdAndAuthor_Id(Long itemId, Long authorId);

    List<Comment> findAllByItem_Id(Long itemId);
}
