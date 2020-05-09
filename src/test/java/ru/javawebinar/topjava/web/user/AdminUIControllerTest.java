package ru.javawebinar.topjava.web.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.web.AbstractControllerTest;

class AdminUIControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = AdminUIController.AJAX_ADMIN_USERS + '/';

    @Autowired
    private UserService userService;

    @Test
    void changeEnable() throws Exception {
        perform(MockMvcRequestBuilders.post(BASE_URL + USER.getId()))
                .andExpect(status().isNoContent());

        User changed = userService.get(USER_ID);
        changed.changeEnable();

        USER_MATCHER.assertMatch(userService.get(USER_ID), USER);
    }
}