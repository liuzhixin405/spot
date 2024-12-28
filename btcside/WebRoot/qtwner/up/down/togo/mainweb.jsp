<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="jsp/_basepage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
<head>
    <title>管理中心 V1.0 ${aaaa}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=uft-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="-1000">
    <link href="<%=jspPath %>css/admin.css" type="text/css" rel="stylesheet">
</head>
<frameset border="0" framespacing="0" rows="60, *" frameborder="0">
<FRAME name=header src="<%=jspPath %>_header.jsp" frameBorder=0 noResize scrolling=no>
<FRAMESET cols="170, *">
<FRAME name="menu" src="<%=jspPath %>_menu.jsp" frameBorder=0 noResize>
<FRAME name="main" src="<%=jspPath %>jsp/welcome.jsp" frameBorder=0 noResize scrolling=yes>
</FRAMESET>
</frameset>
<noframes>
</noframes>
</html>

