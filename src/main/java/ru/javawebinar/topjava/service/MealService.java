package ru.javawebinar.topjava.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

@Service
public class MealService {

    @Autowired
    private MealRepository repository;

    public Meal save(int userId, Meal meal) {
        return repository.save(userId, meal);
    }

    public boolean delete(int userId, int mealId) {
        return repository.delete(userId, mealId);
    }

    public Meal get(int userId, int mealId) {
        return Optional.ofNullable(repository.get(userId, mealId))
            .orElseThrow(() -> new NotFoundException(""));
    }

    public List<Meal> getAll(int userId) {
        return (List<Meal>) repository.getAll(userId);
    }

}