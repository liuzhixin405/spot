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
                    <form class="notification depth-form" action="<%=actionPath%>managerUserLottery/loadUserDraw.do" method="post">
                        <label for="symbol">用户ID：</label>
                        <input type="text" name="uid" id="uid" value="${uid}"></input>
                        <div class="clear"></div>
                        <button type="submit" id="submit" class="button">
                            查 询
                        </button>
                    </form>
                </div>
            </div>

            <table id="changecolor">
                <thead>
                <tr>
                    <th>
                        用户ID
                    </th>
                    <th>
                        可抽奖次数
                    </th>
                    <th>
                        已抽奖次数
                    </th>
                    <th>
                        今日已产生usdt手续费
                    </th>
                    <th>
                        今日已产生usdr手续费
                    </th>
                    <th>
                        收货地址
                    </th>
                    <th>
                        取余手续费
                    </th>
                    <th>
                        操作
                    </th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${pm.datas}" var="l" varStatus="status">
                    <tr>
                        <td>
                            <c:out value="${l.f_user_id}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_draw_count}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_already_draw_count}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_produce_fee_usdt.doubleValue()}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_produce_fee_usdr.doubleValue()}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.address}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_surplus_fee.doubleValue()}"></c:out>
                        </td>
                        <td>
                            <a href="<%=actionPath%>managerUserLottery/loadOnlyUser.do?uid=${l.f_user_id}">编辑</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <tfoot>
            <tr>
                <td colspan="11">
                    <div class="pagination">
                        <!-- ********** 分页开始 **********	-->
                        <pg:pager url="managerUserLottery/loadUserDraw.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
                                  maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
                            <pg:param name="uid" value="${uid}" />

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

                    </div>
                    <div class="clear"></div>
                </td>
            </tr>
            </tfoot>
            <script language="javascript" type="text/javascript">
                senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
            </script>
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