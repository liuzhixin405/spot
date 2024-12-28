<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
<head>
    <base href="<%=actionPath%>">
    <title>抽奖记录</title>
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
                用户抽奖记录
            </h3>

            <div class="clear"></div>
        </div>
        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                <br>
                <div style="line-height: 24px;">
                    <form class="notification depth-form" action="<%=actionPath%>managerUserLottery/loadDrawRecord.do" method="post">
                        <label for="symbol">用户ID：</label>
                        <input type="text" name="uid" id="uid" value="${uid}"></input>
                        <div class="clear"></div>
                        <label for="symbol">奖品类型：</label>
<%--                        onchange="javascript:document.check.submit();"--%>
                        <select name="award" id="award" >
                            <option value="" id="">全部</option>
                            <c:if test="${settings == null }">
                                <option value="" selected="selected">暂无奖品类型</option>
                            </c:if>
                            <c:if test="${settings != null }">
                                <c:forEach items="${settings}" var="l">
                                    <c:if test="${award == l.prize }">
                                        <option value="${l.prize}" selected="selected">${l.name_zh_CN}</option>
                                    </c:if>
                                    <c:if test="${award != l.prize }">
                                        <option value="${l.prize}">${l.name_zh_CN}</option>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </select>
                        <button type="submit" id="submit" class="button">
                            查询
                        </button>
                        <div class="clear"></div>
                        <label for="symbol">当前用户发放代币总量：</label>
                        <font color="red">${userCount}</font>
                        <div class="clear"></div>
                        <label for="symbol">发放代币总量：</label>
                        <font color="red">${count}</font>
                    </form>
                </div>
            </div>
            <table id="changecolor">
                <thead>
                <tr>
                    <th>
                        ID
                    </th>
                    <th>
                        用户ID
                    </th>
                    <th>
                        奖项
                    </th>
                    <th>
                        奖品名称
                    </th>
                    <th>
                        抽奖时间
                    </th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${pm.datas}" var="l" varStatus="status">
                    <tr>
                        <td>
                            <c:out value="${l.id}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.user_id}"></c:out>
                        </td>
                        <td>
                            <c:if test="${l.prize == 'first'}">
                            <c:out value="一等奖"></c:out>
                            </c:if>
                            <c:if test="${l.prize == 'second'}">
                            <c:out value="二等奖"></c:out>
                            </c:if>
                            <c:if test="${l.prize == 'third'}">
                            <c:out value="三等奖"></c:out>
                            </c:if>
                            <c:if test="${l.prize == 'fourth'}">
                            <c:out value="四等奖"></c:out>
                            </c:if>
                            <c:if test="${l.prize == 'fifth'}">
                            <c:out value="五等奖"></c:out>
                            </c:if>
                            <c:if test="${l.prize == 'noprize'}">
                            <c:out value="感谢参与"></c:out>
                            </c:if>
                        </td>
                        <td>
                            <c:out value="${l.prize_name}"></c:out>
                        </td>
                        <td>
                            <ut:formatDate dateline="${l.draw_time}"></ut:formatDate>
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
                        <pg:pager url="managerUserLottery/loadDrawRecord.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
                                  maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
                            <pg:param name="uid" value="${uid}" />
                            <pg:param name="award" value="${award}" />

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