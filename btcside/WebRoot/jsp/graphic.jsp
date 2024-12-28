<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>--<fmt:message key="msg.pg.title.graphichelp"/></title>
		<jsp:include page="_common.jsp"></jsp:include>
		<link href="<%=basePath%>favicon.ico" rel="SHORTCUT ICON" />
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
	</head>
	<body class="graphic-Bg">
		<jsp:include page="_top.jsp"></jsp:include>
		<div>
			<div class="graphic-box">
				<c:if test="${locale == 'zh_CN' }">
					<img alt="" src=".././common/images/tutorial-bfx-cn.jpg">
				</c:if>
				<c:if test="${locale ne 'zh_CN' }">
					<img alt="" src=".././common/images/tutorial-bfx-en.jpg">
				</c:if>
			</div>	
		</div>
</body>
<script type="text/javascript">
	//登录后顶部菜单
  $(function(){
    $(".menu-control").mouseover(function() {
      // 鼠标移动到.menu-control菜单显示，鼠标从菜单上移开，菜单消失
      $(".top-menu").show().mouseleave(function() {
        $(this).hide();
      });
    });
    $(".menu-control").mouseout(function() {
      // 鼠标从.menu-control移开，菜单消失
      $(".top-menu").hide();
    }); 
  })
  
  function changeTo(locale) {
		$.get(basePath + "home/setLocale.do?locale=" + locale, function(data) {
			window.location.reload(true);
		});
	}
	
  function getMessageTest() {
		$.ajax({
				type:"get",
				url:basePath + "order/getMessage.do",
				data:{
				},
				dataType:"json",
				success:function(data) {
					if(null != data) {
						$("#messageCountNum").html("");
						if(data["unReadCount"] > 0 ) {
							$("#messageCountDiv").css("display","block"); 
							$("#messageCountNum").html(data["unReadCount"]);
						} else {
							$("#messageCountDiv").css("display","none"); 
						}
					}
					setTimeout(getMessageTest, 30000);
					/* setTimeout(getOrderList, 30000); */
				},
				error:function() {
					setTimeout(getMessageTest, 120000);
				}
			});
	}
	getMessageTest();
	function changeTo(locale) {
		$.get(basePath + "home/setLocale.do?locale=" + locale, function(data) {
			window.location.reload(true);
		});
	}
	
	function getMessageTestaa() {
		$.ajax({
				type:"get",
				url:basePath + "order/getUnreadOrders.do",
				data:{
				},
				dataType:"json",
				success:function(data) {
					if(data != null) {
						$(".message-control").mouseover(function() {
							$(".message-top-menu").show();
							$(".icons-ring").css("opacity",0.7);	
						})
						$(".message-control").mouseout(function() {
							$(".message-top-menu").hide();
							$(".message-top-menu").css('border','none');
							$(".icons-ring").css("opacity",1);		
						})
						var htmls = "<div class='arrow-top' style='z-index: 50px;left: 125px;'></div><div class='megunread-box'>";
						for (var index = 0; index < data.length; index++) {
							htmls += "<div class='megunread-pri'>"
								+ "<a class='megunread-link' style='color: black;display: block;' href='" + basePath + "order/orderDetail.do?id=" + data[index].id + "'>"
								+ "<span>"
								+"[" + data[index].f_order_code + "]"
								+"</span><span style='margin-left: 10px; width: 180px; overflow: hidden;white-space: nowrap;text-overflow: ellipsis;'><fmt:message key='msg.pg.top.messagetheme'/>：" 
								+ data[index].f_theme +"</span><div class='clear'></div><span style='float:right;'>" + dateFmt(data[index].f_update_time*1000) + "</span>" 
								+ "<div class='clear'></div>"
								+ "</a>"
								+ "</div>"			
						}
						$(".message-top-menu").html(htmls + "</div>")	
					}
					if(data.length == 0) {
						$(".message-control").mouseover(function() {
							$(".message-top-menu").hide();	
						})
					}
					setTimeout(getMessageTestaa, 30000);
				},
				error:function() {
					setTimeout(getMessageTestaa, 120000);
				}
			});
	}
	getMessageTestaa();
</script>

</html>	    
