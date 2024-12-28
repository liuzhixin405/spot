<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
<head>
    <base href="<%=actionPath%>">
    <title>用户统计</title>
    <link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
    <script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <style type="text/css">
        .clear {
            clear: both;
        }
    </style>

    <script type="text/javascript">
        function sure(qr, url) {
            if (qr != null) {
                if (confirm(qr)) {
                    document.location.href = url;
                }
            } else {
                document.location.href = url;
            }

        }
    </script>
</head>

<body>
<div id="main-content">
    <div class="clear"></div>
    <!-- End .clear -->
    <div class="content-box">
        <!-- Start Content Box -->
        <div class="content-box-header">
            <h3>
                用户统计
            </h3>

            <div class="clear"></div>
        </div>
        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                <br>
                <div style="line-height: 24px;">
                    <form class="notification depth-form" action="<%=actionPath%>managerUserLottery/updateUserAward.do" method="post">
                        <label for="symbol">用户ID：</label>
                        <input type="text" name="user_id" id="user_id" value="${uid}" readonly="readonly"></input>
                        <div class="clear"></div>
                        <label for="symbol">奖项：</label>
                        <select name="prize" id="prize">
                            <c:if test="${settings == null }">
                                <option value="" selected="selected">暂无奖品类型</option>
                            </c:if>
                            <c:if test="${settings != null }">
                                <c:forEach items="${settings}" var="l">
                                        <option value="${l.prize}">${l.name_zh_CN}</option>
                                </c:forEach>
                            </c:if>
                        </select>
                        <div class="clear"></div>
                        <label for="symbol">抽多少次可获得：</label>
                        <input type="text" name="number" id="number" value=""></input>
                        (指定两次，则抽第三次时获得；实际中奖是指定次数+1)
                        <div class="clear"></div>

                        <button type="submit" id="submit" class="button">
                            提 交
                        </button>
                    </form>
                </div>
            </div>
            <font color="red">${msg}</font>
        </div>
        <!-- End #tab2 -->
    </div>
    <!-- End .content-box-content -->
</div>
<!-- End .content-box -->
</body>

<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript">
</script>
</html>