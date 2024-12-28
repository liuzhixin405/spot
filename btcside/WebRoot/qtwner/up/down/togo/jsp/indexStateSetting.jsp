<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
<head>
    <base href="<%=actionPath%>">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>指数设置</title>
    <link rel="stylesheet" href="<%=jspPath %>css/reset.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath %>css/style.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath %>css/invalid.css" type="text/css" media="screen" />
    <script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
</head>
<body>
<div id="main-content">
    <div class="clear"></div>
    <!-- End .clear -->
    <div class="content-box">
        <!-- Start Content Box -->
        <div class="content-box-header">
            <h3>
                指数设置
            </h3>
            <div class="clear"></div>
        </div>

        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                <!-- end Notifications -->
                <div class="notification success png_bg">
                    <br>
                    <div>
                        <form action="" method="post">
                            <c:if test="${iState.okex_state==0}">
                                OKEX指数当前状态<font color="red">关闭</font>
                                <button type="button" onclick="updateState('确认要开启吗','<%=actionPath %>managerIndexSateSetting/updateState.do?state=1&indexName=okex_state')" class="button"> 开 启</button>
                            </c:if>
                            <c:if test="${iState.okex_state==1}">
                                OKEX指数当前状态<font color="red">开启</font>
                                <button type="button" onclick="updateState('确认要关闭吗','<%=actionPath%>managerIndexSateSetting/updateState.do?state=0&indexName=okex_state')" class="button"> 关 闭</button>
                            </c:if>
                        </form>
                        <br>
                        <form action="" method="post">
                            <c:if test="${iState.huobi_state==0}">
                                Huobi指数当前状态<font color="red">关闭</font>
                                <button type="button" onclick="updateState('确认要开启吗','<%=actionPath %>managerIndexSateSetting/updateState.do?state=1&indexName=huobi_state')" class="button"> 开 启</button>
                            </c:if>
                            <c:if test="${iState.huobi_state==1}">
                                Huobi指数当前状态<font color="red">开启</font>
                                <button  type="button" onclick="updateState('确认要关闭吗','<%=actionPath%>managerIndexSateSetting/updateState.do?state=0&indexName=huobi_state')" class="button"> 关 闭</button>
                            </c:if>
                        </form>
                        <br>
                        <form action="" method="post">
                            <c:if test="${iState.binance_state==0}">
                                Binance指数当前状态<font color="red">关闭</font>
                                <button type="button" onclick="updateState('确认要开启吗','<%=actionPath %>managerIndexSateSetting/updateState.do?state=1&indexName=binance_state')" class="button"> 开 启</button>
                            </c:if>
                            <c:if test="${iState.binance_state==1}">
                                Binance指数当前状态<font color="red">开启</font>
                                <button type="button" onclick="updateState('确认要关闭吗','<%=actionPath%>managerIndexSateSetting/updateState.do?state=0&indexName=binance_state')" class="button"> 关 闭</button>
                            </c:if>
                        </form>
                    </div>
                    <br>
                </div>
            </div>
        </div>
        <!-- End .content-box-content -->
    </div>
    <!-- End .content-box -->
</div>
<script type="text/javascript">
    function updateState(qr,url)
    {
        if(qr!=null)
        {
            if(confirm(qr))
            {
                document.location.href = url;
            }
        }
        else{
            document.location.href = url;
        }

    }
</script>
</body>
</html>