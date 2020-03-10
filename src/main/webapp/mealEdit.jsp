<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/jquery.datetimepicker.css"/>
    <script src="js/jquery.js"></script>
    <script src="js/jquery.datetimepicker.full.min.js"></script>
    <title>Meal edit</title>
</head>
<body>
    <script>
        $(function () {
            $('input[name="date"]').datetimepicker();
        });
    </script>

    <form method="post" action="meals">
        <table cellspacing="10">
            <tr>
                <td>Id</td>
                <td><input type="text" readonly="readonly" name="id" value="<c:out value="${meal.id}"/>"></td>
            </tr>
            <tr>
                <td>Date</td>
                <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both"/>
                <td><input type="text" name="date" value="<fmt:formatDate pattern="yyyy/MM/dd HH:mm" value="${parsedDateTime}"/>"/></td>
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
