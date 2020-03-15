package ru.javawebinar.topjava.web.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;

    public MealTo save(Meal meal) {
        log.info("Save {}", meal);
        return MealsUtil.getTos(Arrays.asList(service.save(SecurityUtil.getAuthUserId(), meal)), SecurityUtil.authUserCaloriesPerDay())
            .stream().findFirst().get();
    }

    public boolean delete(int mealId) {
        log.info("Delete {}", mealId);
        return service.delete(SecurityUtil.getAuthUserId(), mealId);
    }

    public MealTo get(int mealId) {
        int userId = SecurityUtil.getAuthUserId();
        log.info("Read meal {}", mealId);
        return MealsUtil.getTos(Arrays.asList(service.get(userId, mealId)), SecurityUtil.authUserCaloriesPerDay())
            .stream().findFirst().get();
    }

    public List<MealTo> getAll(LocalDate dateFrom, LocalDate dateTo, LocalTime timeFrom, LocalTime timeTo) {
        int userId = SecurityUtil.getAuthUserId();
        log.info("Read all for user {}", userId);

        return MealsUtil.filteredByStreams(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay(),
            m -> {
                boolean compare = true;
                if (dateFrom != null && dateTo != null) {
                    compare = m.getDate().compareTo(dateFrom) >= 0 && m.getDate().compareTo(dateTo) <= 0;
                }

                if (timeFrom != null && timeTo != null) {
                    compare &= m.getDateTime().toLocalTime().compareTo(timeFrom) >= 0 && m.getDateTime().toLocalTime().compareTo(timeTo) <= 0;
                }

                return compare;
            });
    }
}