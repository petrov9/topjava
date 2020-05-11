package ru.javawebinar.topjava.web.meal;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.GlobalExceptionHandler;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.ErrorInfoTestData.ERROR_MATCHER;
import static ru.javawebinar.topjava.ErrorInfoTestData.create409ErrorEntity;
import static ru.javawebinar.topjava.ErrorInfoTestData.create422ErrorEntity;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static ru.javawebinar.topjava.util.MealsUtil.createTo;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService mealService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(MEAL1));
    }

    @Test
    void getUnauth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Meal updated = MealTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, START_SEQ), updated);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateWithDateTimeDuplicate() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setDateTime(MEAL2.getDateTime());

        String url = REST_URL + MEAL1_ID;
        ResultActions action = perform(MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(updated))
            .with(userHttpBasic(USER)))
            .andExpect(status().isConflict());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create409ErrorEntity(url, GlobalExceptionHandler.DATE_TIME_DUPLICATE_MESSAGE);
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateInvalid() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setCalories(1);

        String url = REST_URL + MEAL1_ID;
        ResultActions action = perform(MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(updated))
            .with(userHttpBasic(USER)))
            .andExpect(status().isUnprocessableEntity());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create422ErrorEntity(url,"[calories] must be between 10 and 5000");
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal))
                .with(userHttpBasic(USER)));

        Meal created = readFromJson(action, Meal.class);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    void createWithLocationInvalid() throws Exception {
        Meal newMeal = MealTestData.getNew();
        newMeal.setDescription("1");

        String url = REST_URL;
        ResultActions action = perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(newMeal))
            .with(userHttpBasic(USER)))
            .andExpect(status().isUnprocessableEntity());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create422ErrorEntity(url,"[description] size must be between 2 and 120");
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createWithLocationAndDateTimeDuplicated() throws Exception {
        Meal newMeal = MealTestData.getNew();
        newMeal.setDateTime(MEAL2.getDateTime());

        String url = REST_URL;
        ResultActions action = perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(newMeal))
            .with(userHttpBasic(USER)))
            .andExpect(status().isConflict());

        ErrorInfo actual = readFromJson(action, ErrorInfo.class);
        ErrorInfo expected = create409ErrorEntity(url,GlobalExceptionHandler.DATE_TIME_DUPLICATE_MESSAGE);
        ERROR_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(getTos(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void filter() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30").param("startTime", "07:00")
                .param("endDate", "2020-01-31").param("endTime", "11:00")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MEAL_TO_MATCHER.contentJson(createTo(MEAL5, true), createTo(MEAL1, false)));
    }

    @Test
    void filterAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=&endTime=")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(MEAL_TO_MATCHER.contentJson(getTos(MEALS, USER.getCaloriesPerDay())));
    }
}