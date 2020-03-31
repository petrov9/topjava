package ru.javawebinar.topjava.service.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

@ContextConfiguration({
    "classpath:spring/spring-app.xml",
    "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles(value = {Profiles.JDBC, Profiles.ACTIVE_DATABASE})
public class JdbcUserServiceTest extends UserServiceTest {

    @Test
    @Override
    public void create() throws Exception {
        super.create();
    }

    @Test(expected = DataAccessException.class)
    @Override
    public void duplicateMailCreate() throws Exception {
        super.duplicateMailCreate();
    }

    @Test(expected = NotFoundException.class)
    @Override
    public void deletedNotFound() throws Exception {
        super.deletedNotFound();
    }

    @Test
    @Override
    public void get() throws Exception {
        super.get();
    }

    @Test(expected = NotFoundException.class)
    @Override
    public void getNotFound() throws Exception {
        super.getNotFound();
    }

    @Test
    @Override
    public void getByEmail() throws Exception {
        super.getByEmail();
    }

    @Test
    @Override
    public void update() throws Exception {
        super.update();
    }

    @Test
    @Override
    public void getAll() throws Exception {
        super.getAll();
    }
}