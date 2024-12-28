<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="_basepage.jsp"%>

<script type="text/javascript">
	var isLogin = ${sessionScope.userinfo != null };
	var locale = "${locale}";
	if ("" == locale) {
		locale = "en_US";
	}
</script>

<div class="left-menu">
  <div class="menu-block">
    <div class="menu-title">
      <h3> <span class="icons-news-btn"></span><fmt:message key="msg.pg.index.newsInfo"/> </h3>
    </div>
    <ul  class="dt_list left-menu-ul">
      <li class="${actionName=='notice'||actionName=='noticedo'  ? 'current' : ''}"> <a href="<%=newsPath %>notice.do"> <span></span><fmt:message key="msg.pg.selfHelp.stationIn"/></a> </li>
      <li class="${actionName=='news'||actionName=='newsdo' ? 'current' : ''}"> <a href="<%=newsPath %>news.do"><span></span><fmt:message key="msg.pg.news.infos"/></a> </li>
      <li class="${actionName=='about'||actionName=='aboutdo' ? 'current' : ''}"> <a href="<%=newsPath %>about.do"><span></span><fmt:message key="msg.pg.selfHelp.infoDiscure"/></a> </li>
    </ul>
  </div>
<%--  <div class="menu-block">--%>
<%--    <div class="menu-title">--%>
<%--      <h3> <span class="icons-sscp-btn"></span><fmt:message key="msg.pg.footer.listedProducts"/> </h3>--%>
<%--    </div>--%>
<%--    <ul class="left-menu-ul">--%>
<%--    	<li class="${actionName=='btc'  ? 'current' : ''}"> <a href="<%=helpPath%>btc.do"> <span></span>BTC<fmt:message key="msg.pg.index.contract"/></a> </li>--%>
<%--    	<li class="${actionName=='sz'  ? 'current' : ''}"> <a href="<%=helpPath%>sz.do"> <span></span>EOS<fmt:message key="msg.pg.index.contract"/></a> </li>--%>
<%--    	<li class="${actionName=='eth'  ? 'current' : ''}"> <a href="<%=helpPath%>eth.do"> <span></span>ETH<fmt:message key="msg.pg.index.contract"/></a> </li>--%>
<%--		<li class="${actionName=='help'  ? 'current' : ''}"> <a href="<%=helpPath%>help.do"> <span></span><fmt:message key="msg.pg.selfHelp.explainOfNoun"/></a> </li>--%>
<%--		<li class="${actionName=='safe'  ? 'current' : ''}"> <a href="<%=helpPath%>safe.do"> <span></span><fmt:message key="msg.pg.selfHelp.riskEduate"/></a> </li>--%>
<%--    </ul>--%>
<%--  </div>--%>
  <div class="menu-block">
    <div class="menu-title">
      <h3> <span class="icons-qt-btn"></span><fmt:message key="msg.pg.selfHelp.thereHelp"/></h3>
    </div>
    <ul class="left-menu-ul">
      <li class="${actionName=='other'||actionName=='otherdo'  ? 'current' : ''}"> <a href="<%=newsPath%>other.do"> <span></span><fmt:message key="msg.pg.selfHelp.thereHelp"/></a> </li>
    </ul>
  </div>
</div>
