<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
<head>
    <base href="<%=actionPath%>">
    <title>抽奖概率设置</title>
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
               抽奖概率设置
            </h3>

            <div class="clear"></div>
        </div>
        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                    <br>
                    <div style="line-height: 24px;">
                        <form class="notification depth-form" action="<%=actionPath%>managerUserLottery/doSettingDraw.do" method="post">
                            <label for="symbol">ID：</label>
                            <input type="text" name="id" id="id" value="${bqLuckDrawSetting.id}" readonly></input>
                            <div class="clear"></div>

                            <input type="hidden" name="prize" value="${bqLuckDrawSetting.prize}" readonly></input>

                            <div class="clear"></div>
                            <label for="symbol">奖品中文名称：</label>
                            <input type="text" name="name_zh_CN" value="${bqLuckDrawSetting.name_zh_CN}"></input>
                            <div class="clear"></div>
                            <label for="symbol">奖品英文名称：</label>
                            <input type="text" name="name_en_US" value="${bqLuckDrawSetting.name_en_US}"></input>
                            <div class="clear"></div>
                            <label for="symbol">中奖概率：</label>
                            <input type="text" name="probability" value="${bqLuckDrawSetting.probability}"></input>
                            <div class="clear"></div>
                            <label for="symbol">实物奖品：</label>
                            <input type="text" name="prize_matter" value="${bqLuckDrawSetting.prize_matter}"></input>
                            <div class="clear"></div>
                            <label for="symbol">usdt奖品：</label>
                            <input type="text" name="prize_usdt" value="${bqLuckDrawSetting.prize_usdt}"></input>
                            <div class="clear"></div>
                            <label for="symbol">奖品说明：</label>
                            <input type="text" name="notes" value="${bqLuckDrawSetting.notes}"></input>
                            (设置奖品"感谢参与"时，这个地方不用写)
                            <div class="clear"></div>
                            <label for="symbol">照片：</label>
                            <input type="text" name="photo_name" id="photo_name" value="${bqLuckDrawSetting.photo_name }" />
<%--                            <input type="hidden" name="macro" id="macro" value="${bqLuckDrawSetting.macro }" readonly/>--%>
                            <div class="clear"></div>
                            <button type="submit" id="submit" class="button">
                                提 交
                            </button>
                        </form>
                    </div>
            </div>
            <font color="red">${msg}</font>

            <table id="changecolor">
                <thead>
                <tr>
                    <th>
                        ID
                    </th>
                    <th>
                        中文名
                    </th>
                    <th>
                        英文名
                    </th>
                    <th>
                        中奖概率
                    </th>
                    <th>
                        实物奖品
                    </th>
                    <th>
                        USDT奖品
                    </th>
                    <th>
                        说明
                    </th>
                    <th>
                        照片
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
                            <c:out value="${l.id}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.name_zh_CN}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.name_en_US}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.probability}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.prize_matter}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.prize_usdt}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.notes}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.photo_name}"></c:out>
                        </td>
                        <td>
                            <a href="<%=actionPath%>managerUserLottery/loadDraw.do?prize=${l.prize}">编辑</a>
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
                        <pg:pager url="managerUserLottery/loadDraw.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
                                  maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
                            <pg:param name="id" value="${bqLuckDrawSetting.id}" />
                            <pg:param name="prize" value="${bqLuckDrawSetting.prize}" />

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