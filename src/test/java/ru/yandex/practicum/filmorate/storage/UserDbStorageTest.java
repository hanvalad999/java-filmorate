package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserStorage userStorage;

    @Test
    void shouldCreateAndFindUser() {
        User user = buildUser("user@mail.com", "login", "User Name");
        User saved = userStorage.create(user);

        User found = userStorage.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("User not found"));

        assertThat(found.getEmail()).isEqualTo("user@mail.com");
    }

    @Test
    void shouldUpdateUser() {
        User user = buildUser("user@mail.com", "login", "User Name");
        User saved = userStorage.create(user);

        saved.setName("Updated");
        saved.setEmail("new@mail.com");
        User updated = userStorage.update(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void shouldHandleFriendsUnidirectional() {
        User first = userStorage.create(buildUser("first@mail.com", "first", "First"));
        User second = userStorage.create(buildUser("second@mail.com", "second", "Second"));

        userStorage.addFriend(first.getId(), second.getId());

        assertThat(userStorage.getFriends(first.getId()))
                .extracting(User::getId)
                .containsExactly(second.getId());
        assertThat(userStorage.getFriends(second.getId())).isEmpty();
    }

    @Test
    void shouldFindCommonFriends() {
        User first = userStorage.create(buildUser("first@mail.com", "first", "First"));
        User second = userStorage.create(buildUser("second@mail.com", "second", "Second"));
        User common = userStorage.create(buildUser("third@mail.com", "third", "Third"));

        userStorage.addFriend(first.getId(), common.getId());
        userStorage.addFriend(second.getId(), common.getId());

        assertThat(userStorage.getCommonFriends(first.getId(), second.getId()))
                .extracting(User::getId)
                .containsExactly(common.getId());
    }

    private User buildUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}