<%--
    Document   : resources-management
    Created on : 2017-7-17, 11:41:36
    Author     : Azige
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resource Management</title>
        <link rel="stylesheet" href="<c:url value="/static/bootstrap.css"/>">
    </head>
    <body>
        <div class="container">
            <h1>${message}</h1>
            <pre>${jsonText}</pre>
        </div>
    </body>
</html>
