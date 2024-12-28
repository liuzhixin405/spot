<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="lt-wrap order-panel" style="display:none;" id="close-buy-position">
	<form action="">
		<div class="trade-main">
			<div class="trade-box">
				<div class="trade-detail tog-ht">
					<div class="toggle-wrap relative clearfix">
						<div class="lt">
						  <span class="label"> <em id="toggle-em"><i class="icon icons-show"></i><fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/> : </em> </span>
						  <b id="holdBuyAvgPrice">
						   <c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
						  		0
						  	</c:if>
						  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
						  		${buyMemberTransaction[0].averagePrice}
						  	</c:if>
						</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>
						</div>
					  <div class="clear"></div>
					</div>
				</div>
				
				<input type="hidden" id="buyCloseMId" 
					<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
				  		value="${buyMemberTransaction[0].mt_id}"
				  	</c:if>/>
				  	
				<input type="hidden" id="buyCloseMultiple" 
					<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
				  		value="${buyMemberTransaction[0].multiple}"
				  	</c:if>/>
				
				<div class="trade-detail relative ie-bug">
					<span class="label"><fmt:message key="msg.pg.trade.leverage"/> : </span>
					<p class="lever-control" id="buyCloseMultiples">
						<c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
							<a href="javascript:void(0);" 
								<c:choose>
									<c:when test="${l.index == 0}">class="buy-close-multiple current" </c:when>
									<c:otherwise>class="buy-close-multiple" </c:otherwise>
								</c:choose>
								id="buyCloseMultiple-${l.index}" onclick="buyMultipleClick(${tran.mt_id},${tran.multiple},this);">x${tran.multiple}
							</a>
						</c:forEach>
					</p>
					<div class="que-help que-ggbs" id="ggbs-txt">
						<div class="arrow-top"></div>
						<p> <fmt:message key="msg.pg.trade.leverageClose"/> </p>
					</div>
				</div>
				<!-- 切换计划委托和限价委托按钮start-->
				<div class="trade-detail relative">
					<button type="button" class="sub-entrust-btn-buy" id="closeLongBuy"><fmt:message key="msg.pg.trade.fulloBuyMore"/></button>
					<button type="button" class="sub-entrust-btn-buy currentrust" id="closeShortSell"><fmt:message key="msg.pg.trade.fulloSellMore"/></button>
				</div>
				<!-- 切换计划委托和限价委托按钮end -->
				<div class="trade-detail relative ie-bug1 "> 
					<input type="text" class="validate[required,number] form-control order-input" 
						value="${buytop5List[0].price}" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.closevalue"/>"
						id="closeBuyPrice" name="closeBuyPrice" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
					<div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
				</div>
				
				<div class="trade-detail relative"> 
					<input type="text" style="display: none;"/>
					<input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.trade.closevolumn"/>"
						id="closeBuyAmount" name="closeBuyAmount" maxlength="8" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});" />
					<div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
				</div>
				
				<div class="trade-detail relative">
					<span class="buy-percent">25%</span>
					<span class="buy-percent">50%</span>
					<span class="buy-percent">75%</span>
					<span class="buy-percent">100%</span>
					<div class="clear"></div>
				</div>

				<div class="trade-detail relative" style="margin-bottom: 5px;">
					<div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.fullCanFlatMore"/> : </span>
					<b id="holdBuyAmount">
						<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
					  		0
					  	</c:if>
					  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
					  		${buyMemberTransaction[0].holdCount}
					  	</c:if>
					</b>
					<b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
				</div>
		
				<div class="trade-alert suspend-div" <c:if test="${!currentTransaction.suspend && isTradeTime}">style='display:none;'</c:if>>
					<div class="warning suspend-text">
						<c:if test="${currentTransaction.suspend}"><fmt:message key='msg.trade.pause'/></c:if>
						<c:if test="${!currentTransaction.suspend && !isTradeTime}"><fmt:message key='msg.trade.noTradeTime'/></c:if>
					</div>
				</div>
				<div class="normal-trade-div" <c:if test="${currentTransaction.suspend || !isTradeTime}">style='display:none;'</c:if>>
					<div class="trade-btn-box" style="margin-bottom: 5px;">
						<div class="">
							<button type="button" class="btn trade-btn sell-bg" onclick="return pc_d_from_closePos('${currentTransaction.transaction_code.toLowerCase()}');">
							<fmt:message key="msg.pg.trade.fulloSellMore"/></button>
						</div>
					</div>
					<div class="price-item clearfix">
						<span><fmt:message key="msg.pg.trade.minPriceIs"/><b class="sell minSellText">${minSell }</b><b class="sell"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span>
					</div>
				</div>
				
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
	function buyMultipleClick(mId, multiple, sender) {
		$(".buy-close-multiple").removeClass("current");
		$(sender).addClass("current");
		
		var index = sender.id.split('-')[1];
		$("#holdBuyAvgPrice").html($("#buyAvgPrice" + index).html());
		$("#holdBuyAmount").html($("#buyHoldCount" + index).html());
		
		$("#buyCloseMId").val(mId);
		$("#buyCloseMultiple").val(multiple);
	}

	$(".buy-percent").on('click', function() {
		 $(".buy-percent").each(function(){
			 $(this).removeClass("active");
		 });
		 $(this).addClass("active");
		 
		var percent = $(this).html();
		var float = percent.replace("%","");
		float = float / 100;
		
		var canOpen = parseFloat($("#holdBuyAmount").html());
		if (!isNaN(canOpen)) {
			var amountDigits = Number($('#amountDigits').val());
			var amount = float * canOpen;
			var maxStr = amount + "";
			if (maxStr.indexOf('.') != -1 && maxStr.length - maxStr.indexOf('.') > amountDigits) {
				maxStr = maxStr.substring(0, maxStr.indexOf('.') + amountDigits + 1);
			}
			$("#closeBuyAmount").val(maxStr);
		}
	});
</script>