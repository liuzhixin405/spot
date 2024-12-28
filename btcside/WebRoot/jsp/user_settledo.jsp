<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>

<html>

<head>
  <title>Btcside区块链资产合约交易平台-盈利结算
  </title>
  <link type="text/css" rel="stylesheet" href="<%=basePath %>common/css/style.css?<%=rjs%>">
  <script type="text/javascript" src="<%=basePath %>common/js/jquery.1.7.1.min.js"></script>
  <!--[if lte IE 6]>
<script src="js/ie6png.js" type="text/javascript"></script>
    <script type="text/javascript">
        DD_belatedPNG.fix('.test, ul, img, li, input , a, div,span,em,i');
    </script>
<![endif]-->
</head>


<body>

	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
  <!-- 主体内容 -->
  <div class="container center-cont">
    <div class="left-menu">
      <jsp:include page="_user_left.jsp"></jsp:include>
    </div>
    <div class="main-rt-box min-height">

      <div class="main-wrap">
        <div class="trade-title-lt">
			<span>请选择合约：</span>
				<c:forEach var="transaction" items="${transactionList}">
					<a href="javascript:;" name='${transaction.transaction_code}'
						onclick="changeTransaction('${transaction.transaction_code}');"
						<c:if test="${transaction.transaction_code eq currentTransaction.transaction_code}" > class="current" </c:if>>
						${fn:toUpperCase(transaction.transaction_code)} </a>
				</c:forEach>
			</div>
        <div class="info-box">
        	<div class="alert">
        	<c:if test="${!empty success}">
	            <div class="success">${success}</div>
	            </c:if>
        	<c:if test="${!empty error}">
	            <div class="warning">${error}</div>
	            </c:if>
          	</div>
          <div class="info-wrap">
            <ul class="info-list1">
              <li>
                <span>待结算盈利：</span>
                <b><fmt:formatNumber value="${data.usergain}" pattern="#.####"></fmt:formatNumber>USDT</b>

              </li>
              <li>
                <span>系统亏损均摊比例：</span>
                <b><fmt:formatNumber type="percent" pattern="#.##%" value="${data.rate}"></fmt:formatNumber></b>
              </li>

              <li>
                <span>均摊数额:</span>
                <b><fmt:formatNumber value="${data.apportion}" pattern="#.####"></fmt:formatNumber>USDT</b>
              </li>

              <li>
                <span>合约当前余额:</span>
                <b><fmt:formatNumber value="${data.shallget}" pattern="#.####"></fmt:formatNumber>USDT</b>
              </li>
            </ul>
            <div class="info-hint">由于时间关系此处显示的数据与您提交后所得数据 可能略有误差，具体以您提交后所得数据为准。
            </div>
          </div>
          <div class="clear"></div>
          <form action="<c:if test="${kjs!=0}"><%=basePath%>finishgain/finish.do</c:if>" class="form-wrap">
            <!-- 如果没有设置资金密码，隐藏输入框，默认隐藏 -->
           <!-- <div class="form-group">
  <label for="">资金密码</label>
  <input type="text" placeholder="请输入资金密码">
  <span class="error">资金密码错误</span>
</div>
 -->		
 			<input type="hidden" name="transaction_code" value="${currentTransaction.transaction_code}"/>
            <div class="form-group-btn">
              <button type="submit" class="btn sub-btn <c:if test="${kjs==0}">disabled</c:if>">结算</button>
              <a href="<%=userPath%>center.do" class="btn sub-btn-return">返回</a>
            </div>
          </form>
        </div>
      </div>
      <div class="main-wrap">
        <div class="block-title">
          <h2 class="lt">盈利结算记录</h2>
          <a href="<%=tradePath %>settle.do" class="rt">查看更多>></a>
        </div>
        <div class="trade-manage-main">
          <table cellpadding="0" cellspacing="0">
            <thead>
              <tr>
                <th>结算时间</th>
                <th>合约名称</th>
                <th>结算盈利</th>
                <th>系统亏损均摊比例</th>
                <th>均摊额</th>
                <th>实际得到</th>
              </tr>
            </thead>
            <tbody>
            <c:forEach items="${data.list}" var="l" varStatus="m">
              <tr>
                <td><ut:formatDate dateline="${l.f_settle_time}"></ut:formatDate></td>
                <td>${l.transaction_code}</td>
                <td><fmt:formatNumber value="${l.f_settle_amount+l.f_settle_apportion}" pattern="#.########"></fmt:formatNumber>BTC</td>
                <td><fmt:formatNumber type="percent" pattern="#.##%" value="${l.f_settle_gain==0?0:l.f_settle_loss/l.f_settle_gain}"></fmt:formatNumber></td>
                <td><fmt:formatNumber value="${l.f_settle_apportion}" pattern="#.########"></fmt:formatNumber>BTC</td>
                <td><fmt:formatNumber value="${l.f_settle_amount}" pattern="#.########"></fmt:formatNumber></td>
              </tr>
              </c:forEach>
            </tbody>
          </table>

        </div>
      </div>

    </div>
  </div>
  <div class="clear"></div>
  <!-- 底部footer -->
  <jsp:include page="_footer.jsp"></jsp:include>
  <script type="text/javascript">
  $(function() {
    $("#no-input").click(function(event) {
      $("#user-alert").show();
      $("#close").click(function() {
        $("#user-alert").hide();

      });
    });
  });

  function changeTransaction(url){
	  document.location.href=basePath+"finishgain/loadgain.do?transaction_code="+url;
  }
  </script>
</body>

</html>
