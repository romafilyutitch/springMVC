<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Spittle</title>
    <link rel="stylesheet" type="text/css" href="<c:out value="/resource/style.css"/>">
</head>
<body>
    <div class="spittleView">
    <div class="spittleMessage"><c:out value="${spittle.message}" /></div>
    <div>
    <span class="spittleTime"><c:out value="${spittle.time}" /></span>
    </div>
    </div>
</body>
</html>