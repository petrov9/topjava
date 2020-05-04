package ru.javawebinar.topjava.web.meal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.web.meal.MealRestController.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

class MealRestControllerTest extends AbstractControllerTest {

    @Autowired
    private MealService mealService;

    @Test
    void restGet() throws Exception {
        perform(get(REST_MEALS + "/" + MEAL1.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MEAL_MATCHER.contentJson(MEAL1));
    }

    @Test
    void restDelete() throws Exception{
        perform(delete(REST_MEALS + "/" + MEAL1.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        List<Meal> meals = new ArrayList<>(MEALS);
        meals.remove(MEAL1);

        MEAL_MATCHER.assertMatch(mealService.getAll(USER_ID), meals);
    }

    @Test
    void restUpdate() throws Exception{
        Meal updateMeal = MealTestData.getUpdated();

        perform(put(REST_MEALS).contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(updateMeal)))
            .andDo(print())
            .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(mealService.get(updateMeal.getId(), USER_ID), updateMeal);
    }

    @Test
    void restCreate() throws Exception {
        Meal newMeal = MealTestData.getNew();

        ResultActions action = perform(post(REST_MEALS).contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(newMeal)))
            .andDo(print())
            .andExpect(status().isCreated());

        Meal created = TestUtil.readFromJson(action, Meal.class);
        newMeal.setId(created.getId());

        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(created.getId(), SecurityUtil.authUserId()), newMeal);
    }

    @Autowired
    WebApplicationContext webApplicationContext;

    @Test
    void filterDate() throws Exception{
        String startDate = DateTimeUtil.toString(LocalDate.of(2020, 1, 31));
        String endDate = DateTimeUtil.toString(LocalDate.of(2020, 2, 1));

        String filterUrl = String.format("%s/filterDate?startDate=%s&endDate=%s",
            REST_MEALS, startDate, endDate);

        List<MealTo> expected = MealsUtil.getFilteredTos(Arrays.asList(MEAL7, MEAL6, MEAL5, MEAL4),
            SecurityUtil.authUserCaloriesPerDay(), null, null);

        perform(get(filterUrl))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MEALTO_MATCHER.contentJson(expected));
    }

    @Test
    void filterTime() throws Exception{
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(20, 1);

        String startTimeStr = DateTimeUtil.toString(startTime);
        String endTimeStr = DateTimeUtil.toString(endTime);

        String filterUrl = String.format("%s/filterTime?startTime=%s&endTime=%s",
            REST_MEALS, startTimeStr, endTimeStr);

        List<MealTo> expected = MealsUtil.getFilteredTos(Arrays.asList(MEAL7, MEAL6, MEAL3, MEAL2),
            SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);

        perform(get(filterUrl))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MEALTO_MATCHER.contentJson(expected));
    }
}