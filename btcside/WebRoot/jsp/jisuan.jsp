<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!--  计算 -->
<div class="add-windows" id="jisuan-windows">
	<div class="jisuanqi">
		<a class="jisuan-close-icon" href="javascript:closeJisuan()"><i class="iconfont icon-guanbi"></i></a>
	
		<div class="jisuanqi-title">
			<div class="jisuanqi-title-text"><fmt:message key="msg.pg.trade.calculator"/></div>
			<div class="jisuanqi-tab">
				<span class="current" onClick="jisuanTabClick(1)"><fmt:message key="msg.pg.trade.ProfitLossCalculation"/></span>
				<c:if test="${sessionScope.userinfo!=null}">
				<span onClick="jisuanTabClick(2)"><fmt:message key="msg.pg.trade.triggerPrice"/></span>
				</c:if>
			</div>
		</div>
		
		<div class="jisuanqi-income">
			<div class="jisuanqi-input lt">
				<div class="jisuanqi-direction">
					<span class="jisuanqi-direction-type1 current" onClick="jisuanIncomeClick(1)"><fmt:message key="msg.pg.trade.Long"/></span>
					<span class="jisuanqi-direction-type2" onClick="jisuanIncomeClick(2)"><fmt:message key="msg.pg.trade.Short"/></span>
					<input type="hidden" id="jisuanType" value="1" />
				</div>
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.leverageMultiple"/></label>
					<%-- <span class="multiple" id="jisuanqiMultiple">x${multiple[0].multiple}</span> --%>
					<select id="MultipleSelect" style="width: 210px;height: 42px;color: #2B364B;font-size:14px;border-radius: 4px;border: solid 1px #F1F3F4;padding-left: 148px;appearance:none;-moz-appearance:none;-webkit-appearance:none;cursor: pointer;">
						<c:forEach items="${multiple}" varStatus="l" var="m">
							<c:if test="${m.state==1}">
								<option id="jisuanqiMultiple">x${m.multiple}</option>
							</c:if>
						</c:forEach>
					</select>
					<em class="jisuanqi-select"></em>
				</div>
				
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.openPosition"/></label>
					<input type="number" id="jisuanOpenAmount" />
				</div>
				
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.openPriceCalculator"/></label>
					<input type="number" id="jisuanOpenPrice" />
				</div>
				
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.closevalue"/></label>
					<input type="number" id="jisuanClosePrice" />
				</div>
				<div class="jisuanqi-input-item">
					<button onclick="jisuanIncome(true)"><fmt:message key="msg.pg.trade.beginToCalculate"/></button>
				</div>
				<div id="jisuanError1" style="color:red;line-height:16px;"></div>
			</div>
			
			<div class="jisuanqi-result rt">
				<!-- <div class="jisuan-btn" onclick="jisuanIncome(true)">
					<div class="jisuan-btn-text">计算</div>
				</div> -->
				<div class="jisuanqi-result-content">
					<div class="jisuanqi-result-item" style="font-size:14px;">
						<label><fmt:message key="msg.pg.trade.calculatorResult"/></label>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.trade.depositCash"/>：</label>
						<span id="jisuanDeposit"></span>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.tradeQuery.money"/>：</label>
						<span id="jisuanIncome"></span>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.trade.rateOfReturn"/>：</label>
						<span id="jisuanIncomeRate"></span>
					</div>
					<c:if test="${sessionScope.userinfo!=null}">
						<div class="jisuanqi-result-item">
							<label><fmt:message key="msg.pg.trade.pendingRate"/>：</label>
							<span id="orderPendingRate"></span>
						</div>
						<div class="jisuanqi-result-item">
							<label><fmt:message key="msg.pg.trade.myCommissionRate"/>：</label>
							<span id="eatMyCommissionRate"></span>
						</div>
						<div class="jisuanqi-result-item">
							<label><fmt:message key="msg.pg.trade.feeRateTips2"/></label>
						</div>
					</c:if>
				</div>
			</div>
			
			<div class="clear"></div>
			
			<div class="jisuan-note"><span style="color: red;">*</span><fmt:message key="msg.pg.trade.calculateTips"/></div>
		</div>
		
		<div class="jisuanqi-qpj" style="display: none;">
			<div class="jisuanqi-input lt">
				<div class="jisuanqi-direction">
					<span class="jisuanqi-direction-type1 current" onClick="jisuanQpjClick(1)"><fmt:message key="msg.pg.trade.Long"/></span>
					<span class="jisuanqi-direction-type2" onClick="jisuanQpjClick(2)"><fmt:message key="msg.pg.trade.Short"/></span>
					<input type="hidden" id="jisuanQpjType" value="1" />
				</div>
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.leverageMultiple"/></label>
					<%-- <span class="multiple" id="jisuanqiQpjMultiple">x${multiple[0].multiple}</span> --%>
					<select id="MultipleSelectQ" style="width: 210px;height: 42px;color: #2B364B;font-size:14px;border-radius: 4px;border: solid 1px #F1F3F4;padding-left: 148px;appearance:none;-moz-appearance:none;-webkit-appearance:none;cursor: pointer;">
						<c:forEach items="${multiple}" varStatus="l" var="m">
							<c:if test="${m.state==1}">
								<option id="jisuanqiMultipleQ">x${m.multiple}</option>
							</c:if>
						</c:forEach>
					</select>
					<em class="jisuanqi-select"></em>
				</div>
				
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.addingPositions"/></label>
					<input type="number" id="jisuanQpjAmount" />
				</div>
				
				<div class="jisuanqi-input-item">
					<label><fmt:message key="msg.pg.trade.openPriceCalculator"/></label>
					<input type="number" id="jisuanQpjPrice" />
				</div>
				
				<div class="">
					<label><fmt:message key="msg.pg.trade.CurContractAvailable"/>：</label>
					<span id="jisuanqiBalance">0</span>
					${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}
				</div>
				<div class="jisuanqi-input-item">
					<button onclick="jisuanQpj(true)"><fmt:message key="msg.pg.trade.beginToCalculate"/></button>
				</div>
				<div id="jisuanError2" style="color:red;line-height:16px;"></div>
			</div>
			
			<div class="jisuanqi-result rt">
<!-- 				<div class="jisuan-btn" onclick="jisuanQpj(true)">
					<div class="jisuan-btn-text">计算</div>
				</div> -->
				
				<div class="jisuanqi-result-content1">
					<div class="jisuanqi-result-item" style="font-size:14px;">
						<label><fmt:message key="msg.pg.trade.calculatorResult"/></label>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.trade.theCurrentPosition"/>：</label>
						<span id="jisuanQpjHold"></span>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.trade.afterSubmission"/>：</label>
						<span id="jisuanQpjHold2"></span>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/>：</label>
						<span id="jisuanAvgPrice"></span>
					</div>
					<div class="jisuanqi-result-item">
						<label><fmt:message key="msg.pg.trade.triggerPrice"/>：</label>
						<span id="jisuanQpjTriPrice"></span>
					</div>
					
					<div class="jisuanqi-qpj-note"><span style="color: red;">*</span> <fmt:message key="msg.pg.trade.calculateTips2"/></div>
				</div>
			</div>
			
			<div class="clear"></div>
			
			<div class="jisuan-note"><span style="color: red;">*</span><fmt:message key="msg.pg.trade.calculateTips"/></div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<%=basePath%>common/js/jisuanqi.js?<%=rjs%>"></script>
