package ru.javawebinar.topjava.service;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

@ContextConfiguration({
    "classpath:spring/spring-app.xml",
    "classpath:spring/spring-db.xml",
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void getForAdmin() {
        Meal actual = service.get(100009, ADMIN_ID);
        assertEquals(ADMIN_MEAL, actual);
    }

    @Test
    public void getForUser() {
        Meal actual = service.get(100002, USER_ID);
        assertEquals(USER_MEAL, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getForUnauthorized() {
        service.get(100002, ADMIN_ID);
    }

    @Test
    public void deleteForAdmin() {
        service.delete(100009, ADMIN_ID);
        List<Meal> meals = service.getAll(ADMIN_ID);
        assertEquals(MEALS_FOR_ADMIN - 1, meals.size());
    }

    @Test
    public void deleteForUser() {
        service.delete(100002, USER_ID);
        List<Meal> meals = service.getAll(USER_ID);
        assertEquals(MEALS_FOR_USER - 1, meals.size());
    }

    @Test(expected = NotFoundException.class)
    public void deleteForUnauthorized() {
        service.delete(100002, ADMIN_ID);
    }

    @Test
    public void getBetweenHalfOpenForAdmin() {
        List<Meal> meals = service.getBetweenHalfOpen(LocalDate.of(2015, 5, 1),
            LocalDate.of(2015, 5, 1), ADMIN_ID);
        assertEquals(1, meals.size());
    }

    @Test
    public void getBetweenHalfOpenForUser() {
        List<Meal> meals = service.getBetweenHalfOpen(LocalDate.of(2020, 1, 30),
            LocalDate.of(2020, 1, 30), USER_ID);
        assertEquals(3, meals.size());
    }

    @Test
    public void getBetweenHalfOpenForUnauthorized() {
        List<Meal> meals = service.getBetweenHalfOpen(LocalDate.of(2020, 1, 30),
            LocalDate.of(2020, 1, 30), ADMIN_ID);
        assertEquals(0, meals.size());
    }

    @Test
    public void getAllForAdmin() {
        List<Meal> meals = service.getAll(ADMIN_ID);
        assertEquals(MEALS_FOR_ADMIN, meals.size());
    }

    @Test
    public void getAllForUser() {
        List<Meal> meals = service.getAll(USER_ID);
        assertEquals(MEALS_FOR_USER, meals.size());
    }

    @Test
    public void getAllForUnauthorized() {
        List<Meal> meals = service.getAll(1000);
        assertEquals(0, meals.size());
    }

    @Test
    public void updateAdminFood() {
        Meal toUpdate = ADMIN_MEAL;
        toUpdate.setCalories(1);
        toUpdate.setDescription("test description");
        toUpdate.setDateTime(LocalDateTime.of(2020, 3, 19, 11, 39));
        service.update(toUpdate, ADMIN_ID);

        Meal updated = service.get(toUpdate.getId(), ADMIN_ID);
        Assertions.assertThat(updated).isEqualToComparingFieldByField(toUpdate);
    }

    @Test
    public void updateUserFood() {
        Meal toUpdate = USER_MEAL;
        toUpdate.setCalories(5000);
        toUpdate.setDescription("русская надпись");
        toUpdate.setDateTime(LocalDateTime.of(2010, 3, 19, 11, 39));
        service.update(toUpdate, USER_ID);

        Meal updated = service.get(toUpdate.getId(), USER_ID);
        Assertions.assertThat(updated).isEqualToComparingFieldByField(toUpdate);
    }

    @Test(expected = NotFoundException.class)
    public void updateUnregisteredFood() {
        Meal toUpdate = UNREGISTERED_MEAL;
        service.update(toUpdate, USER_ID);
    }

    @Test
    public void createForAdmin() {
        Meal create = UNCREATED_MEAL;
        service.create(create, ADMIN_ID);
        List<Meal> meals = service.getAll(ADMIN_ID);
        assertEquals(MEALS_FOR_ADMIN + 1, meals.size());
    }

    @Test
    public void createForUser() {
        Meal create = UNCREATED_MEAL;
        create.setId(null);
        Meal created = service.create(create, USER_ID);
        Assertions.assertThat(created).isEqualToIgnoringGivenFields(create, "id");
    }

    //TODO: didnt realized
    /*@Test(expected = NotFoundException.class)
    public void createForUnregistered() {
        Meal create = UNREGISTERED_MEAL;
        service.create(create, 1000);
    }*/
}