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
            <form action="<c:url value="/management/resources/update"/>" method="POST" enctype="multipart/form-data">
                <label>更新整个资源包(kxy-resource-VERSION.jar)</label>
                <input name="file" type="file">
                <input type="submit" value="上传并更新">
            </form>
            <table class="table">
                <tr>
                    <th>Name</th>
                    <th>Initilized</th>
                    <th>Action</th>
                </tr>
                <c:forEach var="loader" items="${loaders}">
                    <tr>
                        <td><a href="<c:url value="/management/resources/${loader.name}"/>">${loader.name}</a></td>
                        <td>${loader.initilized}</td>
                        <td>
                            <c:if test="${loader.updatable}">
                                <label>更新此加载器(${loader.defaultResourceName})</label>
                                <form action="<c:url value="/management/resources/${loader.name}/update"/>" method="POST" enctype="multipart/form-data">
                                    <input name="file" type="file">
                                    <input type="submit" value="上传并更新">
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </body>
</html>
