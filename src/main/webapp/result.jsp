<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Результат</title>
</head>
<body>
    <h1>${sessionScope.result}</h   1>
    <a href="hello">Начать заново</a>

    <hr>
    <h3>Статистика сессии</h3>
    <p>ID сессии: ${pageContext.session.id}</p>
    <p>Время создания: ${pageContext.session.creationTime}</p>

</body>
</html>