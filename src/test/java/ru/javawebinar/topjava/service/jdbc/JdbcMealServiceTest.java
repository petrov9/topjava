package ru.javawebinar.topjava.service.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.MealServiceTest;

@ContextConfiguration({
    "classpath:spring/spring-app.xml",
    "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles(value = {Profiles.JDBC, Profiles.ACTIVE_DATABASE})
public class JdbcMealServiceTest extends MealServiceTest {

    @Test
    @Override
    public void delete() throws Exception {
        super.delete();
    }

    @Test
    @Override
    public void deleteNotFound() throws Exception {
        super.deleteNotFound();
    }

    @Test
    @Override
    public void deleteNotOwn() throws Exception {
        super.deleteNotOwn();
    }

    @Test
    @Override
    public void create() throws Exception {
        super.create();
    }

    @Test
    @Override
    public void get() throws Exception {
        super.get();
    }

    @Test
    @Override
    public void getNotFound() throws Exception {
        super.getNotFound();
    }

    @Test
    @Override
    public void getNotOwn() throws Exception {
        super.getNotOwn();
    }

    @Test
    @Override
    public void update() throws Exception {
        super.update();
    }

    @Test
    @Override
    public void updateNotFound() throws Exception {
        super.updateNotFound();
    }

    @Test
    @Override
    public void getAll() throws Exception {
        super.getAll();
    }

    @Test
    @Override
    public void getBetweenInclusive() throws Exception {
        super.getBetweenInclusive();
    }

    @Test
    @Override
    public void getBetweenWithNullDates() throws Exception {
        super.getBetweenWithNullDates();
    }
}