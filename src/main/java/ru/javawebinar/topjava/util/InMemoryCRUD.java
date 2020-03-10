package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import ru.javawebinar.topjava.model.Meal;

public class InMemoryCRUD implements CRUD{
    private static final AtomicInteger idInit = new AtomicInteger(1);

    public static final List<Meal> MEALS = new ArrayList<>(Arrays.asList(
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
        new Meal(idInit.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    ));

    @Override
    public void create(Meal meal) {
        Meal newMeal = new Meal(
            idInit.getAndIncrement(),
            meal.getDateTime(),
            meal.getDescription(),
            meal.getCalories());

        synchronized (MEALS) {
            MEALS.add(newMeal);
        }
    }

    @Override
    public Meal read(int id) {
        return  MEALS.stream().filter(m -> m.getId() == id).findFirst().get();
    }

    @Override
    public List<Meal> readAll() {
        return MEALS;
    }

    @Override
    public void update(Meal meal) {
        Meal toUpdateMeal = read(meal.getId());
        toUpdateMeal.setDateTime(meal.getDateTime());
        toUpdateMeal.setDescription(meal.getDescription());
        toUpdateMeal.setCalories(meal.getCalories());
    }

    @Override
    public void delete(int id) {
        synchronized (MEALS) {
            MEALS.remove(read(id));
        }
    }
}
