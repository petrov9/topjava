<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meal edit</title>
</head>
<body>
    <form method="post" action="meals">
        <table cellspacing="10">
            <tr>
                <td>Id</td>
                <td>${id}</td>
            </tr>
            <tr>
                <td>Date</td>
                <td><input type="datetime-local" name="date" value="${date}"/></td>
            </tr>
            <tr>
                <td>Description</td>
                <td><input type="text" name="desc" value="${description}"/></td>
            </tr>
            <tr>
                <td>Calories</td>
                <td><input type="text" name="calories" value="${calories}"/></td>
            </tr>
        </table>
        <button type="submit">Save</button>
        <button type="reset">Reset</button>
    </form>
</body>
</html>
