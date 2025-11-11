package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAllUsers();

    User create(User user);

    User addFriend(Integer userId, Integer friendId);

    User deleteFriend(Integer userId, Integer friendId);

    List<User> getMutualFriends(Integer id, Integer otherId);

    User update(User user);

    User getUserById(Integer id);

    List<User> getFriendsByUserId(Integer id);
}
