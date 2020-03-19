package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;
import ru.javawebinar.topjava.util.DateTimeUtil;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert jdbcInsert;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("meals")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource mealMap = new MapSqlParameterSource()
            .addValue("id", meal.getId())
            .addValue("dateTime", meal.getDateTime())
            .addValue("description", meal.getDescription())
            .addValue("calories", meal.getCalories());

        if (meal.isNew()) {
            Number id = jdbcInsert.executeAndReturnKey(mealMap);
            meal.setId(id.intValue());
            jdbcTemplate.execute(String.format("insert into user_meal values (%s,%s)", userId, meal.getId()));
        } else if (namedParameterJdbcTemplate.update("update meals set dateTime=:dateTime, description=:description," + "calories=:calories "
            + "where id=:id", mealMap) == 0) {
            return null;
        }

        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("delete from meals "
            + "where exists(select * from user_meal where user_id = ? and meal_id = ?) and id = ?", userId, id, id) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query("select m.id, m.datetime, m.description, m.calories from meals m inner join user_meal us on us.meal_id = m.id where us.meal_id = ? and us.user_id = ?",
            ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("select m.id, m.datetime, m.description, m.calories from meals m "
                + "inner join user_meal us on us.meal_id = m.id "
                + "where us.user_id = ? order by m.datetime desc",
            ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return jdbcTemplate.query("select m.id, m.datetime, m.description, m.calories from meals m "
                + "inner join user_meal us on us.meal_id = m.id where us.user_id = ? "
                + "and m.datetime >= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') "
                + "and m.datetime <= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') "
                + "order by m.datetime desc",
            ROW_MAPPER, userId, DateTimeUtil.toString(startDate), DateTimeUtil.toString(endDate));
    }
}
