<%--
    Document   : index
    Created on : 2018-11-14, 12:10:56
    Author     : Azige
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Auction Management</title>
        <link rel="stylesheet" href="<c:url value="/static/bootstrap.css"/>">
        <script src="<c:url value="/static/jquery-3.3.1.js"/>"></script>
        <script>
            $(function () {
                $.ajaxSetup({
                    contentType: "application/json"
                });
                $(document).ajaxError(function (event, jqxhr) {
                    $("#message-box").html(JSON.stringify(jqxhr.responseJSON.error));
                });

                function formatDate(time, format = 'YY-MM-DD hh:mm:ss') {
                    if (!time) {
                        return "";
                    }

                    var date = new Date(time);

                    var year = date.getFullYear(),
                        month = date.getMonth() + 1, //月份是从0开始的
                        day = date.getDate(),
                        hour = date.getHours(),
                        min = date.getMinutes(),
                        sec = date.getSeconds();
                    var preArr = Array.apply(null, Array(10)).map(function (elem, index) {
                        return '0' + index;
                    });////开个长度为10的数组 格式为 00 01 02 03

                    var newTime = format.replace(/YY/g, year)
                        .replace(/MM/g, preArr[month] || month)
                        .replace(/DD/g, preArr[day] || day)
                        .replace(/hh/g, preArr[hour] || hour)
                        .replace(/mm/g, preArr[min] || min)
                        .replace(/ss/g, preArr[sec] || sec);

                    return newTime;
                }

                function renderCommodity(node) {
                    var commodity = node.data("commodity");
                    node.find("td").eq(0).html(commodity.id);
                    node.find("td").eq(1).html(commodity.definitionId);
                    node.find("td").eq(2).html(commodity.queueNumber);
                    node.find("td").eq(3).html(commodity.commodityStatus);
                    node.find("td").eq(4).html(commodity.lastBid);
                    node.find("td").eq(5).html(commodity.lastBidderAccountId);
                    node.find("td").eq(6).html(formatDate(commodity.deadline));
                    node.find("td").eq(7).html(commodity.broadcastPublished.toString());
                    node.find("td").eq(8).html(commodity.delivered.toString());
                }

                var lastRow = $("#last-row");
                var recordPrototype = $("#record-prototype");
                var newRecordPrototype = $("#new-record-prototype");
                var modifyRecordPrototype = $("#modify-record-prototype");

                function createRecordNode(commodity) {
                    var record = recordPrototype.clone().removeAttr("id").removeAttr("hidden");
                    record.data("commodity", commodity);
                    renderCommodity(record);

                    record.find("button").eq(0).click(function () {
                        var modifyNode = createModifyRecordNode(record);
                        modifyNode.insertBefore(record);
                        record.attr("hidden", "hidden");
                    });

                    var deleteButton = record.find("button").eq(1);
                    deleteButton.click(function () {
                        deleteButton.html("再次点击确认");
                        deleteButton.click(function () {
                            $.post("commodity/" + commodity.id + "/delete", function (response) {
                                record.remove();
                            });
                        });
                    });

                    return record;
                }

                function createModifyRecordNode(recordNode) {
                    var modifyNode = modifyRecordPrototype.clone().removeAttr("id").removeAttr("hidden");
                    var commodity = recordNode.data("commodity");
                    modifyNode.find("td").eq(0).html(commodity.id);
                    modifyNode.find("td").eq(1).find("input").val(commodity.definitionId);
                    modifyNode.find("td").eq(2).find("input").val(commodity.queueNumber);
                    modifyNode.find("td").eq(3).html(commodity.commodityStatus);
                    modifyNode.find("td").eq(4).html(commodity.lastBid);
                    modifyNode.find("td").eq(5).html(commodity.lastBidderAccountId);
                    modifyNode.find("td").eq(6).html(formatDate(commodity.deadline));
                    modifyNode.find("td").eq(7).html(commodity.broadcastPublished.toString());
                    modifyNode.find("td").eq(8).html(commodity.delivered.toString());

                    modifyNode.find("button").eq(0).click(function () {
                        $.post("commodity/" + commodity.id, JSON.stringify({
                            definitionId: modifyNode.find("input").eq(0).val(),
                            queueNumber: modifyNode.find("input").eq(1).val()
                        }), function (response) {
                            modifyNode.remove();
                            recordNode.data("commodity", response.content);
                            renderCommodity(recordNode);
                            recordNode.removeAttr("hidden");
                        });
                    });

                    modifyNode.find("button").eq(1).click(function () {
                        modifyNode.remove();
                        recordNode.removeAttr("hidden");
                    });

                    return modifyNode;
                }

                lastRow.find("button").click(function () {
                    var newRecord = newRecordPrototype.clone().removeAttr("id").removeAttr("hidden");
                    newRecord.find("button").eq(0).click(function () {
                        $.post("commodity/create", JSON.stringify({
                            definitionId: newRecord.find("input").eq(0).val(),
                            queueNumber: newRecord.find("input").eq(1).val()
                        }), function (response) {
                            createRecordNode(response.content).insertBefore(newRecord);
                            newRecord.remove();
                        });
                    });
                    newRecord.find("button").eq(1).click(function () {
                        newRecord.remove();
                    });
                    newRecord.insertBefore(lastRow);
                });

                $.getJSON("commodity/", function (response) {
                    var commodities = response.content;
                    for (var i = 0; i < commodities.length; i++) {
                        createRecordNode(commodities[i]).insertBefore(lastRow);
                    }
                });
            });
        </script>
    </head>
    <body>
        <div class="container">
            <h3>拍卖品管理</h3>
            <div id="message-box" style="color: red"></div>
            <div><a href="commodity.csv">导出CSV</a></div>
            <div>
                <form class="form-inline" action="commodity.csv" method="POST" enctype="multipart/form-data">
                    <div class="form-group">
                        <input type="file" name="file">
                    </div>
                    <input class="form-control btn-link" type="submit" value="导入CSV">
                </form>
            </div>
            <table class="table">
                <tr>
                    <th>id</th>
                    <th>definitionId</th>
                    <th>queueNumber</th>
                    <th>commodityStatus</th>
                    <th>lastBid</th>
                    <th>lastBidderAccountId</th>
                    <th>deadline</th>
                    <th>broadcastPublished</th>
                    <th>delivered</th>
                    <th>操作</th>
                </tr>
                <tr id="record-prototype" hidden="hidden">
                    <td>1</td>
                    <td>123</td>
                    <td>123</td>
                    <td>ON_SALE</td>
                    <td>100</td>
                    <td>321</td>
                    <td>2010-10-10 10:10:10</td>
                    <td>false</td>
                    <td>false</td>
                    <td>
                        <button type="button" class="btn btn-default">修改</button>
                        <button type="button" class="btn btn-danger">删除</button>
                    </td>
                </tr>
                <tr id="new-record-prototype" hidden="hidden">
                    <td>new</td>
                    <td style="width: 100px"><input type="text" class="form-control"></td>
                    <td style="width: 100px"><input type="text" class="form-control"></td>
                    <td>QUEUING</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td>
                        <button type="button" class="btn btn-default">创建</button>
                        <button type="button" class="btn btn-default">取消</button>
                    </td>
                </tr>
                <tr id="modify-record-prototype" hidden="hidden">
                    <td></td>
                    <td style="width: 100px"><input type="text" class="form-control"></td>
                    <td style="width: 100px"><input type="text" class="form-control"></td>
                    <td>QUEUING</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td>
                        <button type="button" class="btn btn-default">提交</button>
                        <button type="button" class="btn btn-default">取消</button>
                    </td>
                </tr>
                <tr id="last-row">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td>
                        <button type="button" class="btn btn-default">新增</button>
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>
