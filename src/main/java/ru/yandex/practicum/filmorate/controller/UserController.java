package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserController {

    // только сервис, без прямой зависимости от хранилища
    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя.");
        return userService.create(user);
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя.");
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Поступил запрос на добавление в друзья: userId={}, friendId={}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение списка пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Поступил запрос на получение пользователя по id={}.", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Поступил запрос на получение списка друзей пользователя id={}.", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Поступил запрос на получение списка общих друзей: userId={}, otherId={}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Поступил запрос на удаление из друзей: userId={}, friendId={}", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
