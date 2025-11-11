package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User u);
    User update(User u);
    Optional<User> findById(long id);
    Collection<User> findAll();
    void deleteById(long id);
}
