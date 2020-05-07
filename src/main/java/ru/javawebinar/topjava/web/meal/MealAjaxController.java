package ru.javawebinar.topjava.web.meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

@RestController
@RequestMapping(value = "/ajax/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealAjaxController extends AbstractMealController{

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @Override
    @GetMapping()
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @PostMapping()
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createMeal(
        @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime dateTime,
        @RequestParam String description,
        @RequestParam int calories) {
        Meal meal = new Meal(dateTime, description, calories);
        super.create(meal);
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }

    @Override
    @GetMapping("/between")
    public List<MealTo> getBetween(
        @RequestParam @Nullable LocalDate startDate,
        @RequestParam @Nullable LocalTime startTime,
        @RequestParam @Nullable LocalDate endDate,
        @RequestParam @Nullable LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}
