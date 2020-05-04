package ru.javawebinar.topjava.web.meal;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

@RestController
@RequestMapping(value = MealRestController.REST_MEALS, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {

    public static final String REST_MEALS = "/rest/meals";

    @GetMapping("/{id}")
    public ResponseEntity<Meal> restGet(@PathVariable int id) {
        Meal meal = super.get(id);
        return ResponseEntity.ok(meal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal) {
        super.update(meal, meal.getId());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> restCreate(@RequestBody Meal meal) {
        Meal created = super.create(meal);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(REST_MEALS + "/{id}")
            .buildAndExpand(created.getId())
            .toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/filterDate")
    public ResponseEntity<List<MealTo>> filterDate(
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate) {

        List<MealTo> meals = super.getBetween(startDate, endDate);
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/filterTime")
    public ResponseEntity<List<MealTo>> filterTime(
        @RequestParam LocalTime startTime,
        @RequestParam LocalTime endTime) {

        List<MealTo> meals = super.getBetween(startTime, endTime);
        return ResponseEntity.ok(meals);
    }
}