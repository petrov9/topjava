<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
    <table cellspacing="10">
        <thead>
            <tr>
                <th>Date</th>
                <th>Description</th>
                <th>Calories</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${meals}" var="meal">
                <tr style="color: ${meal.excess ? "red" : "green"}">
                    <%--<c:out value="${meal.dateTime}"/>--%>
                    <td>${meal.id}</td>
                    <td>
                        <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}" />
                    </td>
                    <td><c:out value="${meal.description}"/></td>
                    <td><c:out value="${meal.calories}"/></td>
                    <td>
                        <form method="get" action="meals">
                            <input hidden="hidden" type="text" name="action" value="update">
                            <input hidden="hidden" type="text" name="id" value="${meal.id}">
                            <input hidden="hidden" type="text" name="date" value="${parsedDateTime}">
                            <input hidden="hidden" type="text" name="description" value="${meal.description}">
                            <input hidden="hidden" type="text" name="calories" value="${meal.calories}">
                            <button type="submit">Edit</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</body>
</html>
