package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.GlobalExceptionHandler;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.ErrorInfoTestData.ERROR_MATCHER;
import static ru.javawebinar.topjava.ErrorInfoTestData.create409ErrorEntity;
import static ru.javawebinar.topjava.ErrorInfoTestData.create422ErrorEntity;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.web.user.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(USER));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void register() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        User newUser = UserUtil.createNewFromTo(newTo);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.getId();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void registerInvalid() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        newTo.setPassword("1");

        String url = REST_URL + "/register";
        ResultActions action = perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(newTo)))
            .andDo(print())
            .andExpect(status().isUnprocessableEntity());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create422ErrorEntity(url,"[password] length must be between 5 and 32 characters");
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerWithExistEmail() throws Exception {
        UserTo newTo = new UserTo(null, "newName", ADMIN.getEmail(), "newPassword", 1500);

        String url = REST_URL + "/register";
        ResultActions action = perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(newTo)))
            .andDo(print())
            .andExpect(status().isConflict());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create409ErrorEntity(url,GlobalExceptionHandler.EMAIL_DUPLICATE_MESSAGE);
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), UserUtil.updateFromTo(new User(USER), updatedTo));
    }

    @Test
    void updateInvalid() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        updatedTo.setEmail("wrong email");
        User user = UserUtil.updateFromTo(new User(USER), updatedTo);

        String url = REST_URL;
        ResultActions action = perform(MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(USER))
            .content(JsonUtil.writeValue(updatedTo)))
            .andDo(print())
            .andExpect(status().isUnprocessableEntity() );

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create422ErrorEntity(url,"[email] must be a well-formed email address");
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateWithExistEmail() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", ADMIN.getEmail(), "newPassword", 1500);

        String url = REST_URL;
        ResultActions action = perform(MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(USER))
            .content(JsonUtil.writeValue(updatedTo)))
            .andDo(print())
            .andExpect(status().isConflict());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create409ErrorEntity(url, GlobalExceptionHandler.EMAIL_DUPLICATE_MESSAGE);
        ERROR_MATCHER.assertMatch(actual, expected);
    }
}