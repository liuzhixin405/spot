<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
    <base href="<%=actionPath%>">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>币币单币种风控设置</title>
    <link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
    <script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
    <script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
</head>
<body>

<div id="main-content">
    <div class="clear"></div>
    <div class="content-box">
        <div class="content-box-header">
            <h3>币币单币种风控设置</h3>
            <div class="clear"></div>
        </div>

        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                <div class="notification success png_bg">
                    <br>
                    <div>
                        <form action="<%=actionPath %>managerSpotRiskControlSetting/addOrUpadateCoins.do" method="post" >
                            <label>管理员名称：</label>
                            <div>
                                <input name="userName" value="${name}">
                            </div>
                            <div class="clear"></div>
                            <%--for="bqCurrencyTypes"  for="userName"--%>
                            <label>币币权限: </label>
                            <div>
                                <c:forEach var="bqCurrencyType" varStatus="stat" items="${bqCurrencyTypes}">
                            <span style="width: 300px;display: inline-block;">
                                <input type="checkbox" value="${bqCurrencyType.type_name }" name="bqCurrencyTypes"/>${bqCurrencyType.type_name }
                            </span>
                                </c:forEach>
                            </div>
                            <div class="clear"></div>
                            <button type="submit" id="submit" class="button">提交</button>
                        </form>
                        <font color="red">${msg}</font>
                    </div>
                </div>

                <table id="changecolor">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>管理员名称</th>
                        <th>币币权限</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach items="${pm.datas}" var="l" varStatus="status">
                        <tr>
                            <td><c:out value="${l.user_id }"></c:out></td>
                            <td><c:out value="${l.user_name }"></c:out></td>
                            <td><c:out value="${l.user_coins }"></c:out></td>
                        </tr>
                    </c:forEach>
                    </tbody>

                    <tfoot>
                    <tr>
                        <td colspan="12">
                            <div class="pagination">
                                <!-- ********** 分页开始 **********	-->
                                <pg:pager url="managerSpotRiskControlSetting/load.do"
                                          items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
                                          maxPageItems="${pm.maxitems}"
                                          export="currentPageNumber=pageNumber">
<%--                                    <pg:param name="partition" value="${partition}" />--%>
<%--                                    <pg:param name="code" value="${code}" />--%>

                                    <pg:first>
                                        <a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
                                    </pg:first>
                                    <pg:prev>
                                        <a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
                                    </pg:prev>
                                    <pg:pages>
                                        <c:choose>
                                            <c:when test="${currentPageNumber eq pageNumber }">
                                                <font color="red">${pageNumber }</font>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </pg:pages>
                                    <pg:next>
                                        <a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
                                    </pg:next>

                                    <pg:last>
                                        <a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
                                    </pg:last>
                                </pg:pager>
                                共 ${pm.total} 条
                            </div>
                            <div class="clear"></div>
                        </td>
                    </tr>
                    </tfoot>
                </table>
                <script language="javascript" type="text/javascript">
                    <!--
                    //senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
                    senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
                    --></script>
            </div>
        </div>
    </div>
</div>
</body>
</html>
