package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Получен запрос на список пользователей. Текущее количество: {}", users.size());
        return users;
    }

    public User findById(Long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь найден: id={}, email='{}'", user.getId(), user.getEmail());
        return user;
    }

    public User create(User user) {
        log.info("Попытка создать пользователя: {}", user);
        validateUser(user);
        normalizeUser(user);
        User created = userStorage.create(user);
        log.info("Пользователь успешно создан: id={}, name='{}'", created.getId(), created.getName());
        return created;

    }

    public User update(User user) {
        log.info("Попытка обновить пользователя: {}", user);
        validateUser(user);
        normalizeUser(user);
        User updated = userStorage.update(user);
        User resolved = Optional.ofNullable(updated)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь успешно обновлён: id={}, name='{}'", resolved.getId(), resolved.getName());
        return resolved;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        findById(userId);
        findById(friendId);
        log.info("Пользователь {} добавляет в друзья {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);
        log.info("Пользователь {} удаляет из друзей {}", userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        findById(userId);
        List<User> friends = userStorage.getFriends(userId);
        log.info("Получен список друзей пользователя {}. Количество: {}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        findById(userId);
        findById(otherUserId);
        List<User> common = userStorage.getCommonFriends(userId, otherUserId)
                .stream()
                .distinct()
                .collect(Collectors.toList());
        log.info("Найдено {} общих друзей у пользователей {} и {}", common.size(), userId, otherUserId);
        return common;
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