package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
   // User save(User user);

    Boolean existsByEmail(String email);

    //List<User> findAll();
}