<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!-- 交易页面资金划转弹框 -->
<div class="add-windows" id="slider-multiple-windows">
	<div id="background_overlay" class="background_overlay"></div>
	<div class="risk-box-funer">
			<div class="trade-sliderMultiple-wrap">
				<h2>
					<fmt:message key="msg.pg.trade.adjustLeverageMultiple" />
				</h2>
				<a class="close-icon-btn" href="javascript:closeSliderBlock()"><i class="iconfont icon-guanbi"></i></a>

				<div class="trade-sliderMultiple-input">
					<input type="text" class="trade-transferNumber" readonly id="sliderMultipleNum" value="">
				</div>
				<div class="slider-wrap">
					<input type="hidden" class="single-slider" value="20" disabled="true"/>
				</div>
				<div class="trade-sliderMultiple-createAccount">
					<button type="submit" onclick="submitMultipleNum();" class="sliderMultiple-btn">
					<fmt:message key="msg.pg.userCenter.affirm"/>
					</button>
					<p class="error display-none" id="amountmsg" style="color: red; margin-top: 5px;"></p>
				</div>
			</div>
		<div class="clear"></div>
    </div>
</div>
<script type="text/javascript" src="<%=basePath%>common/slidingProgressBar/jquery.range.js?<%=rjs%>"></script>
