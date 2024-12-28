<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/jsp/_basepage.jsp"%>

<!DOCTYPE HTML>
<html>
	<head>
		<title>系统提示！</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=7">
		<style type="text/css">
		<!--
		body
		{
		    margin-left: 0px;
		    margin-top: 0px;
		    margin-right: 0px;
		    margin-bottom: 8px;
		    font-size: 12px;
		    font-family: Arial, Helvetica, sans-serif;
		    background-color: #ffffff;
		}
		h1, h2, h3, h4, h5, form, p, ul
		{
		    margin: 0px;
		    padding: 0px;
		}
		
		.msg a
		{
		    font-size: 16px;
		    font-weight: 700;
		    text-decoration: none;
		    font-family: 微软雅黑,宋体;
		    color: #00CC33;
		}
		.error a
		{
		    font-size: 16px;
		    font-weight: 700;
		    text-decoration: none;
		    font-family: 微软雅黑,宋体;
		    color: #FF0000;
		}
		.message
		{
		    font-size: 12px;
		    margin-top: 2px;
		    line-height: 200%;
		    text-align: center;
		}
		
		.shadow
		{
		    -moz-box-shadow: 3px 3px 5px #44A8D1;
		    -webkit-box-shadow: 3px 3px 5px #44A8D1;
		    box-shadow: 3px 3px 5px #44A8D1; /* For IE 8 */
		    -ms-filter: "progid:DXImageTransform.Microsoft.Shadow(Strength=4, Direction=135, Color='#44A8D1')"; /* For IE 5.5 - 7 */
		    filter: progid:DXImageTransform.Microsoft.Shadow(Strength=4, Direction=135, Color='#44A8D1');
		}
		-->
		</style>
	</head>
<body>
<table width="600" border="0" align="center" cellpadding="6" cellspacing="0" style="font-size: 14px;
    margin-top: 160px; border: 1px #44A8D1 solid; margin-bottom: 100px;" class="shadow">
    <tr>
        <td bgcolor="#44A8D1" style="color: #FFF;">
            <strong>系统提示：</strong>
        </td>
    </tr>
    <tr>
        <td height="22" bgcolor="#FFFFFF">
            <table width="100%" border="0" align="center" cellpadding="0" cellspacing="20">
                <tr>
                    <td style="width: 60px;">
                        <c:choose>
				          	<c:when test="${type==1}">
				          		<img src="<%=basePath%>error/images/right.png"></img>
				          	</c:when>
				          	<c:when test="${type==2}">
				          		<img src="<%=basePath%>error/images/error.png"></img>
				          	</c:when>
			          	</c:choose>
                    </td>
                    <td>
                        <div class="message">
	                        <c:choose>
		                        <c:when test="${type==1}">
		                            <div class="msg"><a href="${ref}">${msg }</a></div>
		                        </c:when>
		                        <c:when test="${type==2}">
		                            <div class="error"><a href="${ref}">${msg }</a></div>
		                        </c:when>
	                        </c:choose>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
详细信息: ${info }
</body>
</html>
