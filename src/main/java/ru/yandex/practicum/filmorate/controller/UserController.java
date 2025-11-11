package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    public UserController() {
        this.users = new UserService(new InMemoryUserStorage());
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET /users");
        return users.findAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable long id) {
        log.info("GET /users/{}", id);
        return users.get(id);
    }

    @PostMapping
    public User createUsers(@Valid @RequestBody User user) {
        log.info("POST /users body={}", user);
        return users.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users body={}", user);
        return users.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT /users/{}/friends/{}", id, friendId);
        users.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);
        users.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> listFriends(@PathVariable long id) {
        log.info("GET /users/{}/friends", id);
        return users.listFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("GET /users/{}/friends/common/{}", id, otherId);
        return users.commonFriends(id, otherId);
    }
}
