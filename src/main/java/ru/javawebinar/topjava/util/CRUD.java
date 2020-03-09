package ru.javawebinar.topjava.util;

import java.util.List;
import ru.javawebinar.topjava.model.Meal;

public interface CRUD {
    void create(Meal meal);
    Meal read(int id);
    List<Meal> readAll();
    void update(Meal meal);
    void delete(int id);
}
