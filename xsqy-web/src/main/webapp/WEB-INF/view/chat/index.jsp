<%--
    Document   : index
    Created on : 2018-10-23, 12:21:15
    Author     : Azige
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chat Management</title>
        <link rel="stylesheet" href="<c:url value="/static/bootstrap.css"/>">
        <script src="<c:url value="/static/jquery-3.3.1.js"/>"></script>
        <script>
            $(function () {
                var currentState = $("#current-state");

                $.getJSON("sendingBroadcastState", function (data) {
                    currentState.html(JSON.stringify(data.content));
                });

                var createTaskButton = $("#create-task-button");
                var cancleTaskButton = $("#cancle-task-button");

                createTaskButton.click(function () {
                    createTaskButton.attr("disabled", "disabled");
                    cancleTaskButton.attr("disabled", "disabled");
                    var message = $("#message-input").val();
                    var interval = $("#interval-input").val();
                    $.post("createSendingBroadcastTask", {message: message, interval: interval}, function (data) {
                        currentState.html(JSON.stringify(data.content));
                        createTaskButton.removeAttr("disabled");
                        cancleTaskButton.removeAttr("disabled");
                    });
                });

                cancleTaskButton.click(function () {
                    createTaskButton.attr("disabled", "disabled");
                    cancleTaskButton.attr("disabled", "disabled");
                    $.post("cancleSendingBroadcastTask", "", function () {
                        currentState.html("null");
                        createTaskButton.removeAttr("disabled");
                        cancleTaskButton.removeAttr("disabled");
                    });
                });
            });
        </script>
    </head>
    <body>
        <div class="container">
            <div>
                <h3>设置循环广播</h3>
                <div>当前状态</div>
                <div>
                    <pre id="current-state"></pre>
                </div>
                <table>
                    <tr>
                        <td>
                            <label>需要发送的消息</label>
                        </td>
                        <td>
                            <textarea id="message-input" style="width: 1000px"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>时间间隔</label>
                        </td>
                        <td>
                            <input id="interval-input" type="text">
                            <label>秒</label>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <button id="create-task-button">创建新的广播任务</button>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <button id="cancle-task-button">取消当前广播任务</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </body>
</html>
