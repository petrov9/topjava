package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController{

    @Autowired
    public JspMealController(MealService mealService) {
        this.service = mealService;
    }

    @GetMapping(value = "/create")
    public String create(Model model) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        model.addAttribute("isCreate", true);

        return "mealForm";
    }

    @GetMapping(value = "/update")
    public String update(Model model, HttpServletRequest request) {
        Meal meal = get(Integer.parseInt(request.getParameter("id")));
        model.addAttribute("meal", meal);
        model.addAttribute("isCreate", false);

        return "mealForm";
    }

    @GetMapping(value = "/delete")
    public String delete(HttpServletRequest request) {
        int mealId = Integer.parseInt(request.getParameter("id"));
        delete(mealId);

        return "redirect:/meals";
    }

    @GetMapping()
    public String showAll(Model model) {
        model.addAttribute("meals", getAll());

        return "meals";
    }

    @GetMapping(value = "/filter")
    public String filter(HttpServletRequest request, Model model) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));

        model.addAttribute("meals", getBetween(startDate, startTime, endDate, endTime));

        return "meals";
    }

    @PostMapping()
    public String createOrUpdate(HttpServletRequest request) throws Exception{
        Meal meal = new Meal(
            LocalDateTime.parse(request.getParameter("dateTime")),
            request.getParameter("description"),
            Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.isEmpty(request.getParameter("id"))) {
            create(meal);
        } else {
            update(meal, Integer.parseInt(request.getParameter("id")));
        }

        return "redirect:meals";
    }
}
