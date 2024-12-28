<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript" src="<%=basePath%>common/js/charting_library/charting_library.min.js?v=1.15"></script>
<script type="text/javascript" src="<%=basePath%>common/js/datafeeds/udf/dist/polyfills.js?v=1.15"></script>
<script type="text/javascript" src="<%=basePath%>common/js/datafeeds/udf/dist/bundle.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/datafeeds/udf/index_pc_all.js?<%=rjs%>"></script>
   
<!-- HTML -->
<div id="tv_chart_container" style="margin:0px; height: 100%;"></div>