package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage users;

    public UserService(UserStorage users) {
        this.users = users;
    }

    public List<User> findAllUsers() {
        return users.findAllUsers();
    }

    public User create(User user) {
        validateAndNormalize(user);
        return users.create(user);
    }

    public User update(User user) {
        validateAndNormalize(user);
        users.getUserById(user.getId()); // проверка существования
        return users.update(user);
    }

    public User getUserById(int id) {
        return users.getUserById(id);
    }

    // --- FRIENDSHIP LOGIC ---
    public User addFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        return user;
    }

    public void deleteFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    // /users/{id}/friends
    public List<User> getUserFriends(int id) {
        User user = getUserById(id);
        return user.getFriends().stream()
                .map(this::getUserById)   // friends хранит id (Integer)
                .toList();
    }
    // /users/{id}/friends/common/{otherId}
    public List<User> getMutualFriends(int id, int otherId) {
        Set<Integer> common = new HashSet<>(getUserById(id).getFriends());
        common.retainAll(getUserById(otherId).getFriends());
        return common.stream()
                .map(this::getUserById)
                .toList();
    }

    // --- VALIDATION ---
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
