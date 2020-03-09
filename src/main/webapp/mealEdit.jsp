<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meal edit</title>
</head>
<body>
    <script>
        $(function () {
            $('input[name="date"]').datepicker();
        });
    </script>

    <form method="post" action="meals">
        <table cellspacing="10">
            <tr>
                <td>Id</td>
                <td><c:out value="${meal.id}"/></td>
            </tr>
            <tr>
                <td>Date</td>
                <td><input type="datetime-local" name="date" value="<c:out value="${meal.date}"/>"/></td>
            </tr>
            <tr>
                <td>Description</td>
                <td><input type="text" name="desc" value="<c:out value="${meal.description}"/>"/></td>
            </tr>
            <tr>
                <td>Calories</td>
                <td><input type="text" name="calories" value="<c:out value="${meal.calories}"/>"/></td>
            </tr>
        </table>
        <button type="submit">Save</button>
        <button type="reset">Reset</button>
    </form>
</body>
</html>
