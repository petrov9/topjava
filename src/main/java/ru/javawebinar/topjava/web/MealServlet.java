package ru.javawebinar.topjava.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.CRUD;
import ru.javawebinar.topjava.util.InMemoryCRUD;
import ru.javawebinar.topjava.util.MealsUtil;

public class MealServlet extends HttpServlet {
    private CRUD crud = new InMemoryCRUD();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            List<MealTo> mealToList = MealsUtil.filteredByStreams(crud.readAll(), LocalTime.MIN, LocalTime.MAX, 2000);
            req.setAttribute("meals", mealToList);
            req.getRequestDispatcher("/meals.jsp").forward(req, resp);
        } else if (action.endsWith("update")) {
            req.setAttribute("id", req.getParameter("id"));
            req.setAttribute("date", req.getParameter("date"));
            req.setAttribute("description", req.getParameter("description"));
            req.setAttribute("calories", req.getParameter("calories"));
            req.getRequestDispatcher("/mealEdit.jsp").forward(req, resp);
        } else if (action.endsWith("delete")) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        int id = (int) req.getAttribute("id");

        String date = (String) req.getAttribute("date");
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String description = (String) req.getAttribute("desc");
        int calories = (int)req.getAttribute("calories");

        Meal meal = new Meal(id, dateTime, description, calories);

        crud.update(meal);

        resp.sendRedirect("/meals.jsp");
    }
}
