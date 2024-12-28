<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

	<script src="<%=basePath%>common/js/amcharts.js?<%=rjs%>"></script>
    <script src="<%=basePath%>common/js/serial.js"></script>
    <script src="<%=basePath%>common/js/dataloader.min.js?v=101"></script>
    <script src="<%=basePath%>common/js/light.js"></script>
    <script>
    	var bqCode = $('#transaction_code').val();
    </script>
    <script src="<%=basePath%>common/js/depathdraw.js"></script>
    <!-- HTML -->
    <div id="chart-depath"></div>
    
    