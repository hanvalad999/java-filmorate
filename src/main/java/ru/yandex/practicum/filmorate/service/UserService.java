package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User create(User user) {
        validateUser(user);
        normalizeUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        normalizeUser(user);
        User updated = userStorage.update(user);
        return Optional.ofNullable(updated)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        findById(userId);
        findById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        findById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        findById(userId);
        findById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId)
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }

    private void normalizeUser(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}