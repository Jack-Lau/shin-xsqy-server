<%--
    Document   : module
    Created on : 2018-9-20, 15:39:39
    Author     : Azige
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>KXY API Panel</title>
        <link rel="stylesheet" href="<c:url value="/static/bootstrap.css"/>">
        <script src="<c:url value="/static/jquery-3.3.1.js"/>">
        </script>
        <style>
            .fixed-nav {
                position: fixed;
                left: 0;
                overflow: scroll;
                height: 100%;
                width: 300px;
            }
        </style>
        <script>
            function createModuleNode(module, navs, baseUri = "") {
                var id = module.name;
                var node = $("<div>").attr("id", id);
                var subNavs = [];
                navs.push({id: id, name: module.name, subNavs: subNavs});
                node.append($("<hr>"))
                    .append($("<h1>").append($("<a>").attr("href", "#" + id).text("模块：" + module.name)));
                if (module.webInterfaces.length > 0) {
                    node.append($("<h3>").text("包含的 Web 接口"));
                    for (var i = 0; i < module.webInterfaces.length; i++) {
                        node.append(createWebInterfaceNode(module.webInterfaces[i], baseUri + module.baseUri, module.name + "_", subNavs));
                    }
                }
                if (module.webNotifications.length > 0) {
                    var notificationId = module.name + "_notification";
                    subNavs.push({id: notificationId, name: "通知消息"});
                    node.append($("<h3>").text("包含的 Web 通知").attr("id", notificationId));
                    node.append(createWebNotificationsNode(module.webNotifications));
                }
                for (var i = 0; i < module.submodules.length; i++) {
                    node.append(createModuleNode(module.submodules[i], navs, baseUri + module.baseUri));
                }
                return node;
            }

            function createWebInterfaceNode(webInterface, baseUri, baseName, navs) {
                var id = baseName + webInterface.name;
                navs.push({id: id, name: webInterface.name});
                var tbody = $("<tbody>");
                tbody.append(tableRow("URI", baseUri + webInterface.uri, true))
                    .append(tableRow("描述", webInterface.description))
                    .append(tableRow("HTTP 方法", webInterface.httpMethod, true));
                if (webInterface.requestMediaType) {
                    tbody.append(tableRow("请求媒体类型", webInterface.requestMediaType, true));
                }
                if (webInterface.requestBodyJsonType) {
                    tbody.append(tableRow("请求体数据类型", webInterface.requestBodyJsonType, true));
                }
                if (webInterface.requestParameters.length > 0) {
                    var requestParametersNode = $("<tr>")
                        .append($("<th>").attr("class", "col-md-3").text("请求参数"))
                        .append($("<td>").attr("class", "col-md-9").append(requestParameterTable(webInterface.requestParameters)));
                    tbody.append(requestParametersNode);
                } else {
                    tbody.append(tableRow("请求参数", "无"));
                }
                tbody.append(tableRow("响应媒体类型", webInterface.responseMediaType, true))
                    .append(tableRow("响应数据类型", webInterface.responseJsonType, true));
                if (webInterface.responseElementType) {
                    tbody.append(tableRow("响应集合元素类型", webInterface.responseElementType, true));
                }
                tbody.append(tableRow("响应描述", webInterface.responseDescription));
                if (webInterface.expectableErrors.length > 0) {
                    var expectableErrorsNode = $("<tr>")
                        .append($("<th>").attr("class", "col-md-3").text("可能的异常"))
                        .append($("<td>").attr("class", "col-md-9").append(errorCodeTable(webInterface.expectableErrors)));
                    tbody.append(expectableErrorsNode);
                }

                var panelBody = $("<div>").attr("class", "panel-body")
                    .append($("<h4>").append($("<a>").attr("href", "#" + id).text(webInterface.name)))
                    .append($("<table>").attr("class", "table table-condensed").append(tbody));
                var node = $("<div>").attr("class", "panel panel-default").attr("id", id)
                    .append(panelBody);
                return node;
            }

            function tableRow(name, value, code = false) {
                if (code) {
                    return $("<tr>").append($("<th>").attr("class", "col-md-3").text(name)).append($("<td>").attr("class", "col-md-9").append($("<code>").text(value)));
                } else {
                    return $("<tr>").append($("<th>").attr("class", "col-md-3").text(name)).append($("<td>").attr("class", "col-md-9").text(value));
                }
            }

            function requestParameterTable(parameters) {
                var tbody = $("<tbody>");
                var row = $("<tr>").append($("<th>").text("类型"))
                    .append($("<th>").text("名字"))
                    .append($("<th>").text("描述"));
                tbody.append(row);
                for (var i = 0; i < parameters.length; i++) {
                    var param = parameters[i];
                    row = $("<tr>").append($("<td>").append($("<code>").text(param.type)))
                    .append($("<td>").text(param.name))
                    .append($("<td>").text(param.description));
                    tbody.append(row);
                }

                return $("<table>").attr("class", "table table-condensed").append(tbody);
            }

            function errorCodeTable(errorCodes) {
                var tbody = $("<tbody>");
                var row = $("<tr>").append($("<th>").text("错误码"))
                    .append($("<th>").text("描述"));
                tbody.append(row);
                for (var i = 0; i < errorCodes.length; i++) {
                    var ec = errorCodes[i];
                    row = $("<tr>").append($("<td>").text(ec.errorCode))
                    .append($("<td>").text(ec.description));
                    tbody.append(row);
                }

                return $("<table>").attr("class", "table table-condensed").append(tbody);
            }

            function createWebNotificationsNode(webNotifications) {
                var tbody = $("<tbody>");
                var row = $("<tr>").append($("<th>").text("路径"))
                    .append($("<th>").text("描述"))
                    .append($("<th>").text("数据类型"));
                tbody.append(row);
                for (var i = 0; i < webNotifications.length; i++) {
                    var wn = webNotifications[i];
                    row = $("<tr>").append($("<td>").append($("<code>").text(wn.destination)))
                    .append($("<td>").text(wn.description))
                    .append($("<td>").append($("<code>").text(wn.messageJsonType)));
                    tbody.append(row);
                }

                return $("<table>").attr("class", "table table-condensed").append(tbody);
            }

            function createNavs(navs, depth) {
                var node = $("<ul>");
                for (var i = 0; i < navs.length; i++) {
                    var nav = navs[i];
                    var listItem = $("<li>").append($("<a>").attr("href", "#" + nav.id).text(nav.name));
                    if (nav.subNavs) {
                        listItem.append(createNavs(nav.subNavs));
                    }
                    node.append(listItem);
                }
                return node;
            }

            $(function () {
                $.getJSON("<c:url value="/api/view"/>", function (data) {
                    var rootModule = data.content;
                    var navbar = $("<nav>").attr("class", "fixed-nav").append($("<h1>").text("目录"));
                    var modulesNode = $("<div>");
                    var navs = [];
                    for (var i = 0; i < rootModule.submodules.length; i++) {
                        modulesNode.append(createModuleNode(rootModule.submodules[i], navs));
                    }
                    navbar.append(createNavs(navs, 0));
                    $(".container").attr("style", "padding-left: 300px").append(navbar).append(modulesNode);

                    // 构造完节点后，让窗口重新定位锚点
                    $(decodeURIComponent(window.location.hash))[0].scrollIntoView();
                });
            });
        </script>
    </head>
    <body>
        <div class="container"></div>
    </body>
</html>
