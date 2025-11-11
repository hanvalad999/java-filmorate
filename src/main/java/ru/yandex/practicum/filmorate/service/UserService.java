package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage users;

    public Collection<User> findAll() {
        return users.findAll();
    }

    public User get(long id) {
        return users.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id=" + id + " not found"));
    }

    public User create(User u) {
        validateAndNormalize(u, /*isUpdate=*/false);
        return users.create(u);
    }

    public User update(User u) {
        if (u.getId() == null) {
            throw new ValidationException("Id обязателен для обновления пользователя");
        }
        get(u.getId());
        validateAndNormalize(u, /*isUpdate=*/true);
        return users.update(u);
    }

    public void addFriend(long id, long friendId) {
        User u = get(id);
        User f = get(friendId);

        u.getFriends().add(friendId);
        f.getFriends().add(id);
    }

    public void removeFriend(long id, long friendId) {
        User user = get(id);
        User friend = get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> listFriends(long id) {
        return get(id).getFriends().stream()
                .map(this::get)
                .toList();
    }

    public List<User> commonFriends(long id, long otherId) {
        Set<Long> common = new HashSet<>(get(id).getFriends());
        common.retainAll(get(otherId).getFriends());
        return common.stream().map(this::get).toList();
    }

    private void validateAndNormalize(User u, boolean isUpdate) {
        // Логин не пустой и без пробелов
        if (u.getLogin() == null || u.getLogin().isBlank() || u.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        // Если name пуст — подставим login
        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }
    }

}
