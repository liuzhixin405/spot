<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
<head>
    <base href="<%=actionPath%>">
    <title>安全验证修改记录</title>
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
                安全验证修改记录
            </h3>
            <div class="clear"></div>
        </div>
        <div class="content-box-content">
            <div class="tab-content default-tab" id="tab1">
                <br>
                <div style="line-height: 24px;">
                    <form class="notification depth-form" action="<%=actionPath%>managerUser/loadModifyRecord.do">
                        用户ID：
                        <input type="text" name="userId" id="userId" value="${uid}"></input>
                         修改类型:
                        <select id="type" name="type">
                            <option value="${type}" selected disabled hidden>
                                <c:if test="${type == 0 || type == null}">
                                    全部
                                </c:if>
                                <c:if test="${type == 1}">
                                    手机
                                </c:if>
                                <c:if test="${type == 2}">
                                    邮箱
                                </c:if>
                                <c:if test="${type == 3}">
                                    邮箱
                                </c:if>
                            </option>
                            <option value="0">全部</option>
                            <option value="1">手机</option>
                            <option value="2">邮箱</option>
                            <option value="3">谷歌</option>
                        </select>
                         修改人：
                        <input type="text" name="modifyUser" id="modifyUser" value="${modifyUser}"></input>
                         账户名：
                        <input type="text" name="name" id="name" value="${name}"></input>
                        操作类型：
                        <select id="opertionType" name="opertionType">
                            <option value="${opertionType}" selected disabled hidden>
                                <c:if test="${opertionType == 0 || opertionType == null}">
                                    全部
                                </c:if>
                                <c:if test="${opertionType == 1}">
                                    注册
                                </c:if>
                                <c:if test="${opertionType == 2}">
                                    绑定
                                </c:if>
                                <c:if test="${opertionType == 3}">
                                    更改
                                </c:if>
                            </option>
                            <option value="0">全部</option>
                            <option value="1">注册</option>
                            <option value="2">绑定</option>
                            <option value="3">更改</option>
                        </select>
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
                        ID
                    </th>
                    <th>
                        用户ID
                    </th>
                    <th>
                        原安全验证
                    </th>
                    <th>
                        现安全验证
                    </th>
                    <th>
                        修改类型
                    </th>
                    <th>
                        操作类型
                    </th>
                    <th>
                        修改时间
                    </th>
                    <th>
                        修改ip地址
                    </th>
                    <th>
                        修改设备
                    </th>
                    <th>
                        修改人
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
                            <c:out value="${l.f_user_id}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_old_information}"></c:out>
                        </td>
                        <td>
                            <c:out value="${l.f_new_information}"></c:out>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${l.f_information_type eq 1}">
                                    <b class="tel">手机</b>
                                </c:when>
                                <c:when test="${l.f_information_type eq 2}">
                                    <b class="email">邮箱</b>
                                </c:when>
                                <c:when test="${l.f_information_type eq 3}">
                                    <b class="google">谷歌</b>
                                </c:when>
                                <c:otherwise>暂无</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${l.f_operation_type eq 1}">
                                    <b class="rest">注册</b>
                                </c:when>
                                <c:when test="${l.f_operation_type eq 2}">
                                    <b class="bind">绑定</b>
                                </c:when>
                                <c:when test="${l.f_operation_type eq 3}">
                                    <b class="update">修改</b>
                                </c:when>
                                <c:otherwise>暂无</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <ut:formatDate dateline="${l.f_modify_time}"></ut:formatDate>
                        </td>
                        <td>
                            <c:out value="${l.f_modify_ip}"></c:out>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${l.f_modify_server eq 1}">
                                    <b class="pc">PC</b>
                                </c:when>
                                <c:when test="${l.f_modify_server eq 2}">
                                    <b class="api">api</b>
                                </c:when>
                                <c:otherwise>class="multiple" </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:out value="${l.f_modify_user}"></c:out>
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
                        <pg:pager url="managerUser/loadModifyRecord.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
                                  maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
                            <pg:param name="userId" value="${uid}" />
                            <pg:param name="type" value="${type}" />
                            <pg:param name="modifyUser" value="${modifyUser}" />
                            <pg:param name="name" value="${name}" />
                            <pg:param name="opertionType" value="${opertionType}" />
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