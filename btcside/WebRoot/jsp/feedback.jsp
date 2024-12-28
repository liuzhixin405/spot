<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.suggest.add"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath %>common/css/style.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body class="fb-bg">
		<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		  <!-- 主体内容 -->
		  <div class="feedback-wrap">
		    <div class="fb-main fb-main-top-bg">
		      <h3><fmt:message key="msg.pg.suggest.add"/><em style="font-size:14px;color:#999;"><fmt:message key="msg.pg.suggest.note"/></em></h3>
		      <form name="suggestForm" action="<%=suggestPath %>feedBack.do">
		        <div class="fb-input">
		          <label for="f_sug_type" style="line-height:38px;" class="label-us"><fmt:message key="msg.pg.suggest.type"/>：</label>
		          <select name="f_sug_type" id="f_sug_type">
		            <option value="1"><fmt:message key="msg.pg.suggest.type1"/></option>
		            <option value="2"><fmt:message key="msg.pg.suggest.type2"/></option>
		            <option value="3"><fmt:message key="msg.pg.suggest.type3"/></option>
		            <option value="4"><fmt:message key="msg.pg.suggest.type4"/></option>
		          </select>
		        </div>
		        <div class="fb-input">
		          <label for="" class="label-us"><fmt:message key="msg.pg.suggest.detail"/>：</label>
		          <textarea maxlength=300 name="f_sug_content" id="f_sug_content" cols="90" rows="7" class="textArea-us"></textarea>
		        </div>
		        <div class="fb-btn fb-btn-us">
		          <a href="javascript:;" onclick="submiteSugForm();" ><fmt:message key="msg.pg.common.submit"/></a>
		        </div>
		      </form>
		    </div>
		    <div class="fb-main fb-main-bg">
		      <h3><fmt:message key="msg.pg.suggest.mySuggest"/></h3>
		      <ul>
		        <c:forEach var="sug" items="${sugList}">
		        <li> 
		        <c:choose>  
				    <c:when test="${fn:length(sug.f_sug_content) > 20}">  
				        <c:out value="${fn:substring(sug.f_sug_content, 0, 20)} ......" />  
				    </c:when>  
				   <c:otherwise>  
				      <c:out value="${sug.f_sug_content}" />  
				    </c:otherwise>  
						</c:choose> 
						
						<c:choose>  
						    <c:when test="${sug.f_sug_state ==1}">  
						        <span class="icons-fb"><fmt:message key="msg.pg.suggest.adopt"/></span>
						    </c:when>  
						   <c:otherwise>  
						    </c:otherwise>  
						</c:choose> 
				        </li>
				        </c:forEach>
		      </ul>
		    </div>
		    <div class="fb-bg-footer"></div>
		  </div>
  		<div class="clear"></div>
  
		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	function submiteSugForm() {
		var f_sug_type = $("#f_sug_type").val();
		var f_sug_content = $("#f_sug_content").val();
		//$("#f_sug_content").val('');
		if (f_sug_content == '') {
			tipAlert('<fmt:message key="msg.pg.suggest.suggestNull"/>');
			return false;
		}
		$.ajax({
			type : "POST",
			dataType : "json",
			url : basePath + "suggest/feedBack.do",
			data : {
				f_sug_type : f_sug_type,
				f_sug_content : f_sug_content
			},
			success : function(json) {
				if (json.code == 'success') {
					location.reload();
					return;
				} else {
					tipAlert(json.msg);
					return;
				}
			}
		});

	}
	
	var ld = '${locale}';
	if(ld == 'en_US') {
		$('.label-us').css('width','235px');
		$('.fb-btn-us').css("padding-left","249px");
		$('.textArea-us').css("width","590px");
	}
	
	var message = {};
	message["zh_CN"] = {
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		warmTip : "Warm prompt",
		sureTip : "Sure"
	};
	message["ja_JP"] = {
		warmTip : "ヒント",
		sureTip : "確定",
	};
	message["ko_KR"] = {
		warmTip : "팁",
		sureTip : "결정",
	};
	message["ru_RU"] = {
		warmTip : "подсказка",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
</script>

</html>
