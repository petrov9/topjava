package ru.javawebinar.topjava.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
            mealToList.sort(Comparator.comparing(MealTo::getDateTime));

            req.setAttribute("meals", mealToList);
            req.getRequestDispatcher("/meals.jsp").forward(req, resp);
        } else if (action.equalsIgnoreCase("update") ||
                    action.equalsIgnoreCase("create")) {
            String id = req.getParameter("id");
            Meal meal;

            if (id == null) {
                meal = new Meal(0, LocalDateTime.now(), "", 0);
            } else {
                meal = crud.read(Integer.parseInt(req.getParameter("id")));
            }
            req.setAttribute("meal", meal);
            req.getRequestDispatcher("/mealEdit.jsp").forward(req, resp);
        } else if (action.equalsIgnoreCase("delete")) {
            crud.delete(Integer.parseInt(req.getParameter("id")));
            resp.sendRedirect("meals");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String id = req.getParameter("id");

        String date = req.getParameter("date");
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

        String description = req.getParameter("desc");
        int calories = Integer.parseInt(req.getParameter("calories"));

        Meal meal = new Meal(Integer.parseInt(id), dateTime, description, calories);

        if (id.equalsIgnoreCase("0")) {
            crud.create(meal);
        } else {
            crud.update(meal);
        }

        resp.sendRedirect("meals");
    }
}
