package ru.javawebinar.topjava.repository.inmemory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private Map<Integer, List<Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(m -> save(1, m));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (userId == 0)
            return null;

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.merge(userId, new ArrayList<>(Arrays.asList(meal)), (oldList, newList) -> {
                oldList.addAll(newList);
                return oldList;
            });

            return meal;
        }

        repository.computeIfPresent(userId, (id, oldList) -> {
            oldList.removeIf(m -> m.getId().equals(meal.getId()));
            oldList.add(meal);
            return oldList;
        });
        return repository.get(userId).stream().filter(m -> meal.getId().equals(m.getId())).findFirst().orElseGet(null);
    }

    @Override
    public boolean delete(int userId, int mealId) {
        return repository.get(userId).removeIf(meal -> meal.getId() == mealId);
    }

    @Override
    public Meal get(int userId, int mealId) {
        return repository.get(userId).stream().filter(meal -> meal.getId() == mealId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        List<Meal> meals = repository.get(userId);
        List<Meal> opt = Optional.ofNullable(meals).orElse(Collections.emptyList());
        Collections.sort(opt, Collections.reverseOrder(Comparator.comparing(Meal::getDateTime)));
        return opt;
    }
}

