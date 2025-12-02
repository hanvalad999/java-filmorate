package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        UserService userService = mock(UserService.class);
        controller = new UserController(userService);

        when(userService.create(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user == null) {
                throw new ValidationException("Пользователь не может быть null");
            }
            if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new ValidationException("Некорректный email");
            }
            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new ValidationException("Некорректный логин");
            }
            if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(1L);
            return user;
        });
    }

    @Test
    void createUsers_nullBody_shouldThrow() {
        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(null),
                "Ожидалось, что null-тело не пройдёт валидацию"
        );
    }

    @Test
    void createUsers_emptyEmail_shouldThrow() {
        User user = new User();
        user.setEmail("   ");     // пусто
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(user),
                "Пустой email должен привести к ошибке"
        );
    }

    @Test
    void createUsers_emailWithoutAt_shouldThrow() {
        User user = new User();
        user.setEmail("mail.example.com");  // без @
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(user),
                "Email без @ не должен проходить"
        );
    }

    @Test
    void createUsers_emptyLogin_shouldThrow() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("   "); // пустой логин
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(user),
                "Пустой логин не должен проходить"
        );
    }

    @Test
    void createUsers_loginWithSpaces_shouldThrow() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("bad login"); // с пробелом
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(user),
                "Логин с пробелом не должен проходить"
        );
    }

    @Test
    void createUsers_futureBirthday_shouldThrow() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user");
        user.setName("User Name");
        user.setBirthday(LocalDate.now().plusDays(1)); // завтра = будущее

        assertThrows(
                ValidationException.class,
                () -> controller.createUsers(user),
                "Дата рождения в будущем не должна проходить"
        );
    }

    @Test
    void createUsers_todayBirthday_shouldPass() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user");
        user.setName("User Name");
        user.setBirthday(LocalDate.now()); // сегодня ок

        assertDoesNotThrow(
                () -> controller.createUsers(user),
                "Дата рождения сегодня должна быть допустима"
        );
    }

    @Test
    void createUsers_emptyName_shouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("vasya");
        user.setName("   "); // имя пустое
        user.setBirthday(LocalDate.of(1990, 5, 20));

        User created = controller.createUsers(user);

        assertEquals("vasya", created.getName(),
                "Если имя пустое, оно должно подставиться из логина");
    }

    @Test
    void createUsers_validUser_shouldAssignIdAndStore() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("neo");
        user.setName("Thomas Anderson");
        user.setBirthday(LocalDate.of(1990, 3, 1));

        User created = controller.createUsers(user);

        assertNotNull(created.getId(), "После сохранения у пользователя должен быть id");
        assertEquals(1L, created.getId(), "Первый пользователь должен получить id=1");
        assertEquals("neo", created.getLogin());
    }
}
