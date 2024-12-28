<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>经纪人提成补发</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css"
			type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"
			type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css"
			type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8"
			src="<%=jspPath%>js/page.js"></script>
		<script language="javascript"
			src="<%=actionPath%>common/My97DatePicker/WdatePicker.js"
			type="text/javascript"></script>
		<script type="text/javascript"
			src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript">
		function postsure(qr) {
			if (confirm(qr)) {
				return true;
			}

			return false;
		}

		function gourl(qr,url) {
			if (confirm(qr)) {
				document.location.href = url;
			}
		}
		function checkFlag(){
			var uid = document.getElementById("userid").value;
			var ucredit = document.getElementById("userCredit").value;
			if(uid == "" || ucredit == "")
				return false;
			var qr = "确定要给" + uid + "增加" + ucredit + "个积分么？";
			
			if (confirm(qr)) {
				return true;
			}
			return false;
		}
		</script>

	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						经纪人提成补发
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<font color="red">${msg}</font>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>/managerBroker/runBroker.do"
									method="post">
									补发日期：
									<input type="text" name="date" value="${date}" id="date"
										class="Wdate"
										onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										readonly="readonly"></input>
									<button type="submit" class="button">
										提 交
									</button>
								</form>
							</div>
						</div>
						<script language="javascript" type="text/javascript">
<!--
//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
--></script>


					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
