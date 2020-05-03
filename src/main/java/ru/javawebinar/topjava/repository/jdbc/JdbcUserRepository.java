package ru.javawebinar.topjava.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.List;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public User save(@NotNull User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());

            @NotEmpty
            Set<Role> roles = user.getRoles();

            jdbcTemplate.batchUpdate("insert into user_roles values (?,?)",
                roles,
                roles.size(),
                (preparedStatement, role) -> {
                    preparedStatement.setInt(1, user.getId());
                    preparedStatement.setString(2, role.name());
                });
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        }
        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);

        if (user != null) {
            user.setRoles(getRoles(user.getId()));
        }

        return user;
    }

    @Override
    public User getByEmail(@Email String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);

        if (user != null) {
            user.setRoles(getRoles(user.getId()));
        }

        return user;
    }

    private List<Role> getRoles(int userId) {
        return jdbcTemplate.query("select role from user_roles where user_id=" + userId,
            (resultSet, i) -> Role.valueOf(resultSet.getString(1)));
    }

    @Override
    public List<User> getAll() {
        @NotEmpty
        List<User> users = jdbcTemplate.query("SELECT * FROM users u left join user_roles ur on ur.user_id = u.id ORDER BY name, email",
            new RowMapper<User>() {
                Map<Integer, Set<Role>> roles = new HashMap<>();

                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    int userId = resultSet.getInt("id");
                    @NotNull
                    Role role = Role.valueOf(resultSet.getString("role"));

                    if (roles.containsKey(userId)) {
                        roles.get(userId).add(role);
                        return null;
                    }

                    roles.computeIfAbsent(userId, value -> {
                        Set<Role> set = new HashSet<>();
                        set.add(role);

                        return set;
                    });

                    User user = new User();
                    user.setId(userId);
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRegistered(resultSet.getDate("registered"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setCaloriesPerDay(resultSet.getInt("calories_per_day"));
                    user.setRoles(roles.get(userId));

                    return user;
                }
            });
        users.remove(null);

        return users;
    }
}
