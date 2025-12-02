package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (updated == 0) {
            return null;
        }
        return findById(user.getId()).orElse(null);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, email, login, name, birthday FROM users";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        users.forEach(this::fillFriends);
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        users.forEach(this::fillFriends);
        return users.stream().findFirst();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "MERGE INTO friendships (user_id, friend_id) KEY (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM friendships f " +
                "JOIN users u ON f.friend_id = u.id WHERE f.user_id = ?";
        List<User> friends = jdbcTemplate.query(sql, this::mapRowToUser, userId);
        friends.forEach(this::fillFriends);
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM friendships f " +
                "JOIN friendships f2 ON f.friend_id = f2.friend_id " +
                "JOIN users u ON f.friend_id = u.id WHERE f.user_id = ? AND f2.user_id = ?";
        List<User> friends = jdbcTemplate.query(sql, this::mapRowToUser, userId, otherUserId);
        friends.forEach(this::fillFriends);
        return friends;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private void fillFriends(User user) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        Set<Long> friends = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), user.getId()));
        user.setFriends(friends);
    }
}