package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findUser() {
        Collection<User> users = userService.findAll();
        log.info("Получен запрос GET /users. Количество пользователей сейчас: {}", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userService.findById(id);
    }

    @PostMapping
    public User createUsers(@Valid @RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);

        User created = userService.create(user);
        log.info("Пользователь успешно создан: id={}, name='{}'", created.getId(), created.getName());
        return created;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Попытка обновить пользователя: {}", user);

        User updated = userService.update(user);
        log.info("Пользователь успешно обновлён: id={}, name='{}'", updated.getId(), updated.getName());
        return updated;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} добавляет в друзья {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} удаляет из друзей {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на список друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
