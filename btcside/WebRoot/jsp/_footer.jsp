<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="_basepage.jsp"%>
<!-- 底部footer -->
<div class="footer ${homeName=='home' ? 'margintp0' : ''}">
	<div class="container" style="background: none;">
		<div class="row lt slogan-row">
			<div class="footer-logo">
				<img class alt="" src="<%=basePath%>common/images/Btcside.png">
			</div>
			<div class="slogan-text">
				<p><fmt:message key="msg.pg.systemName"/></p>
<%--		        <p><fmt:message key="msg.pg.footer.devOps"/>：BitFuture Group  Ltd</p>--%>
			</div>
			<div class="slogan-text">
		        <p><fmt:message key="msg.pg.footer.serveEmail"/>：Btcsideservice@gmail.com</p>
			</div>
		</div>
		<div class="row lt">
			<h2>
				<fmt:message key="msg.pg.footer.about"/>
			</h2>
			<ul>
				<li>
					<c:if test="${locale eq 'zh_CN'}">
					<a href="<%=newsPath %>about.do"><fmt:message key="msg.pg.footer.aboutUS"/></a>
					</c:if>
					<c:if test="${locale eq 'en_US'}">
					<a href="<%=newsPath %>about.do"><fmt:message key="msg.pg.footer.aboutUS"/></a>
					</c:if>
				</li>
				<li>
					<a href="<%=newsPath %>about.do"><fmt:message key="msg.pg.footer.serviceAgreement"/></a>
				</li>
				<li>
					<a href="<%=newsPath %>about.do"><fmt:message key="msg.pg.footer.fees"/></a>
				</li>
<%--				<li>--%>
<%--					<a href="./docs/v1/contract.html" target="_blank"><fmt:message key="msg.pg.userCenter.applyApiDocs"/></a>--%>
<%--				</li>--%>

				<li>
					<a href="<%=orderPath %>orderList.do"><fmt:message key="msg.pg.footer.workOrder"/></a>
				</li>
			</ul>
		</div>
<%--		<div class="row lt">--%>
<%--			<h2>--%>
<%--				<fmt:message key="msg.pg.footer.listedProducts"/>--%>
<%--			</h2>--%>
<%--			<ul>--%>
<%--				<li>--%>
<%--					<a href="<%=helpPath %>help.do"><fmt:message key="msg.pg.footer.glossary"/></a>--%>
<%--				</li>--%>
				<!--
				<li>
					<a href="<%=helpPath %>rule.do">交易规则</a>
				</li> -->
<%--				<li>--%>
<%--					<a href="<%=helpPath %>safe.do"><fmt:message key="msg.pg.footer.educate"/></a>--%>
<%--				</li>--%>
			<%-- 	<li>
					<c:if test="${locale eq 'zh_CN'}">
					<a href="<%=basePath%>common/wbook/BFXNU.pdf" target="_blank"><fmt:message key="msg.pg.footer.whiteBook"/></a>
					</c:if>
					<c:if test="${locale eq 'en_US'}">
					<a href="<%=basePath%>common/wbook/BFXNUEN.pdf" target="_blank"><fmt:message key="msg.pg.footer.whiteBook"/></a>
					</c:if>
				</li> --%>
<%--				<li>--%>
<%--					<a href="<%=basePath%>help/graphic.do" target="_blank"><fmt:message key="msg.pg.head.course"/></a>--%>
<%--				</li>--%>
<%--				<li>--%>
<%--					<a href="./docs/v1/contract.html" target="_blank"><fmt:message key="msg.pg.userCenter.applyApiDocs"/></a>--%>
<%--				</li>--%>
				<%-- <li>
					<a href="<%=helpPath %>other.do"><fmt:message key="msg.pg.footer.others"/></a>
				</li> --%>
<%--			</ul>--%>
<%--		</div>--%>
		<div class="row1 lt">
<%--			<h2>--%>
<%--				<fmt:message key="msg.pg.footer.connectUs"/>--%>
<%--			</h2>--%>
<%--			<div>--%>
<%--				<a class="photo-teck" href="https://t.me/Btcsideglobal_CN" target="_blank">--%>
<%--					<span class="photo-telegram"></span>--%>
<%--				</a>--%>
<%--				<a class="photo-teck" href="https://twitter.com/Btcside_global" target="_blank">--%>
<%--					<span class="photo-twitter"></span>--%>
<%--				</a>--%>
<%--				<a class="photo-teck" href="https://www.instagram.com/Btcside_global/?hl=en" target="_blank">--%>
<%--					<span class="photo-instagram"></span>--%>
<%--				</a>--%>
<%--				<a class="photo-teck" href="https://www.facebook.com/Btcsideglobalexchange/" target="_blank">--%>
<%--					<span class="photo-facebook"></span>--%>
<%--				</a>--%>
<%--				<a class="photo-teck" href="https://www.tiktok.com/@Btcside_global?lang=en" target="_blank">--%>
<%--					<span class="photo-tiktok"></span>--%>
<%--				</a>--%>
<%--				<a class="photo-teck" href="https://www.youtube.com/channel/UCzLcqwqxf1u5hCf1z5A-IU" target="_blank">--%>
<%--					<span class="photo-youtube"></span>--%>
<%--				</a>--%>
<%--			</div>--%>
	        <div>
<%--				<a href="<%=orderPath %>orderList.do" class="order-a"><fmt:message key="msg.pg.head.orderService"/></a>--%>
<%--				<span class="order-time">9:00--23:00</span>--%>
<%--			</div>--%>
<%--			<a href="<%=orderPath %>orderList.do" class="order-a"><fmt:message key="msg.pg.footer.workOrder"/></a>--%>
<%--			<span class="show-service" onclick="showOnlineService()"><fmt:message key="msg.pg.footer.workOrder"/></span>--%>
		</div>
		<div class="row2 rt">
			<h2>
				<fmt:message key="msg.pg.footer.riskWarn"/>
			</h2>
			
			<p>
				<fmt:message key="msg.pg.footer.block"/>
			</p>
			<p style="margin-top: 10px;">
				<em style="color: #4980E3;"><fmt:message key="msg.pg.footer.promat"/></em>
			</p>
		</div>
		
	</div>
</div>
<div class="clear"></div>

<script>
	var registerInvid = "${invid}";
</script>
<script type="text/javascript" src="<%=basePath%>common/js/footer.js?<%=rjs%>"></script>

<!-- Start of xdk Zendesk Widget script -->
<%--<script id="ze-snippet" src="https://static.zdassets.com/ekr/snippet.js?key=dc1af1d5-27ea-4985-bb89-413d5feff832"> </script>--%>
<!-- End of xdk Zendesk Widget script -->

<%--<script type="text/javascript">--%>
<%--	zE('webWidget', 'setLocale', 'en');--%>
<%--</script>--%>

