package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage users;

    public UserService(UserStorage users) {
        this.users = users;
    }

    // --- CRUD ---

    public List<User> findAllUsers() {
        log.info("Запрошен список всех пользователей");
        return users.findAllUsers();
    }

    public User create(User user) {
        validateAndNormalize(user);
        log.info("Создание пользователя: {}", user);
        return users.create(user);
    }

    public User update(User user) {
        validateAndNormalize(user);
        log.info("Обновление пользователя: id={}", user.getId());
        users.getUserById(user.getId()); // проверка существования
        return users.update(user);
    }

    public User getUserById(int id) {
        log.info("Запрошен пользователь по id={}", id);
        return users.getUserById(id);
    }

    // --- дружба ---

    public User addFriend(int id, int friendId) {
        log.info("Добавление в друзья: userId={}, friendId={}", id, friendId);
        return users.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        log.info("Удаление из друзей: userId={}, friendId={}", id, friendId);
        users.deleteFriend(id, friendId);
    }

    public List<User> getUserFriends(int id) {
        log.info("Запрошен список друзей пользователя id={}", id);
        // важно: если пользователя нет, storage должен кинуть NotFoundException → будет 404
        return users.getFriendsByUserId(id);
    }

    public List<User> getMutualFriends(int id, int otherId) {
        log.info("Запрошен список общих друзей: userId={}, otherId={}", id, otherId);
        return users.getMutualFriends(id, otherId);
    }

    // --- VALIDATION --- (как у тебя было)
    private void validateAndNormalize(User u) {
        if (u == null) {
            throw new ValidationException("User body must not be null");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (u.getLogin() == null || u.getLogin().isBlank() || u.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }
        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }
        if (u.getBirthday() == null || u.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
    }
}
