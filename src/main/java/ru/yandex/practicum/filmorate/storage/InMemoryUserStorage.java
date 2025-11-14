package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private int idForUser = 0;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        userValidation(user);
        user.setFriends(new HashSet<>());
        user.setId(getIdForUser());
        users.put(user.getId(), user);
        log.info("Поступил запрос на добавление пользователя. Пользователь добавлен.");
        return user;
    }

    @Override
    public User update(User user) {
        if (users.get(user.getId()) != null) {
            userValidation(user);
            user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            log.info("Поступил запрос на изменения пользователя. Пользователь изменён.");
        } else {
            log.error("Поступил запрос на изменения пользователя. Пользователь не найден.");
            throw new NotFoundException("User not found.");
        }
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new NotFoundException("User not found.");
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        User user = getUserById(id); // если нет пользователя → NotFoundException → 404
        return user.getFriends().stream()
                .map(this::getUserById)
                .toList();
    }


    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Integer> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());

        return commonIds.stream()
                .map(this::getUserById)
                .toList();
    }



    private int getIdForUser() {
        return ++idForUser;
    }

    private void userValidation(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }
}
