package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    @Autowired
    private CrudMealRepository crudRepository;

    @Autowired
    private CrudUserRepository userRepository;

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(userRepository.findById(userId).get());

        if (meal.isNew()) {
            return  crudRepository.save(meal);
        } else if (crudRepository.findByIdAndUserId(meal.getId(), userId) != null) {
            return crudRepository.save(meal);
        }

        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        crudRepository.deleteByIdAndUserId(id, userId);
        return crudRepository.findByIdAndUserId(id, userId) == null;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserIdOrderByDateTimeDesc(startDateTime, endDateTime, userId);
    }
}
