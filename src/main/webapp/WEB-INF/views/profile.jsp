<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Spittr</title>
    <link rel="stylesheet" type="text/css" href="<c:out value="/resource/style.css"/>">
</head>
<body>
    <h1>Your Profile</h1>
    <c:out value="${spitter.username}" /><br/>
    <c:out value="${spitter.firstName}" />
    <c:out value="${spitter.lastName}" />
</body>
</html>