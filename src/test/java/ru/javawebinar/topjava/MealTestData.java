package ru.javawebinar.topjava;

import java.time.LocalDateTime;
import java.time.Month;
import ru.javawebinar.topjava.model.Meal;

public class MealTestData {

    public static final int MEALS_FOR_ADMIN = 2;
    public static final int MEALS_FOR_USER = 7;

    public static final int USER_ID = 100000;
    public static final int ADMIN_ID = 100001;

    public static final Meal ADMIN_MEAL = new Meal(100009, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 510);
    public static final Meal USER_MEAL = new Meal(100002, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal UNREGISTERED_MEAL = new Meal(10, LocalDateTime.of(2017, Month.JANUARY, 30, 10, 0), "Еда без пользователя", 2);
    public static final Meal UNCREATED_MEAL = new Meal(LocalDateTime.of(2019, Month.JANUARY, 30, 11, 0), "Едля для создания", 20);

}
