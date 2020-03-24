package ru.javawebinar.topjava.repository.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = em.getReference(User.class, userId);
        if (user == null) {
            return null;
        }

        meal.setUser(user);

        if (meal.isNew()) {
            em.persist(meal);

            return meal;
        } else {
            Meal dbMeal = em.find(Meal.class, meal.getId());
            if (dbMeal == null || dbMeal.getUser().getId() != userId) {
                return null;
            }

            return em.merge(meal);
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery("DELETE FROM Meal m WHERE m.id = :id and m.user.id = :userId")
            .setParameter("id", id)
            .setParameter("userId", userId)
            .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = (List<Meal>) em.createQuery("SELECT m FROM Meal m WHERE m.id = :id and m.user.id = :userId")
            .setParameter("id", id)
            .setParameter("userId", userId)
            .getResultList();

        return meals.isEmpty() ? null : DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id = :userId ORDER BY m.dateTime DESC")
            .setParameter("userId", userId)
            .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return em.createQuery("SELECT m FROM Meal m "
            + "WHERE m.dateTime >= :startDate and m.dateTime < :endDate and m.user.id = :userId ORDER BY m.dateTime DESC")
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("userId", userId)
            .getResultList();
    }
}