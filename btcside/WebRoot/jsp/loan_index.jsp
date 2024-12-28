<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.top.borrow"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/pledge.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>
		<div class="add-windows" id="backCoinWindow" style="display:none;position:fixed;">
			<div class="background_overlay">
				<div class="pledge-show-box">
					<div class="pledge-show-box-title">
						<h4>还币</h4>
						<a class="close-icon-btn" href="javascript:closeBackCoin()"><i class="iconfont icon-guanbi" style="font-size: 24px;"></i></a>
					</div>
					<div class="pledge-show-box-content">
						<p style="margin-bottom: 15px;"><span class="half-percent label-span">归还本息共计：</span><span id="backContent" class="half-percent val-span">1000 USDT</span></p>
						<p class=""><span class="half-percent label-span">释放质押数量：</span><span  id="releaseContent" class="half-percent val-span">1000 Btcside</span></p>
					</div>
					<input id="recordId" type="hidden" value="" />
					<div class="pledge-show-box-submit">
						<button onclick="doBackCoin()" id="backCoinBtn" class="pledge-box-btn">确认归还</button>
					</div>
			    </div>
		    </div>
		</div>
		
		<div class="add-windows" id="adjustCoinWindow" style="display:none;position:fixed;">
			<div class="background_overlay">
				<div class="pledge-show-box" style="width: 520px;">
					<div class="pledge-show-box-title">
						<h4 id="adjustTitle">追加质押金</h4>
						<a class="close-icon-btn" href="javascript:closeAdjustCoin()"><i class="iconfont icon-guanbi" style="font-size: 24px;"></i></a>
					</div>
					<div class="pledge-show-box-content">
						<p style="margin-bottom: 15px;text-align:left;"><span class="label-span">当前质押率：</span><span id="currRate" class="val-span">50.0%</span></p>
						<p style="margin-bottom: 15px;text-align:left;"><span class="label-span">调整后质押率：</span><span id="afterRate" class="val-span">50.0%</span></p>
					
						<div class="pledge-content-item pledge-border" style="margin-bottom: 0;">
							<div class="pledge-input-cnt">
								<span class="pledge-label" id="adjustLabel">追加数量</span>
								<input type="text" id="adjustAmount" name="adjustAmount" onkeyup="checkInputDigits(this,8);onAdjustAmount();" class="pledge-input" maxlength="18" 
									placeholder="可用<fmt:formatNumber pattern="#0.########" value='${balance}'/>" spellcheck="false" autocomplete="off">
								<span class="pledge-label link" id="initAdjust" onClick="initAdjust()">初始率</span>
								<span class="pledge-label link" id="allAdjust" onClick="allAdjust()" style="border-left: 1px solid #f1f3f4;">全部</span>
							</div>
							<div class="pledge-coin-select" style="width:auto;padding: 0 15px;">${setting.pledge_symbol}</div>
						</div>
					</div>
					<input id="recordId1" type="hidden" value="" />
					<input id="adjustBorrow" type="hidden" value="" />
					<input id="adjustPledge" type="hidden" value="" />
					<input id="adjustInitRate" type="hidden" value="" />
					<input id="coefficient" type="hidden" value="1" />
					
					<div class="pledge-show-box-submit">
						<button onclick="doAdjustCoin()" id="adjustCoinBtn" class="pledge-box-btn">确认调整</button>
					</div>
			    </div>
		    </div>
		</div>
		
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="pledge-title-content">
			<div class="pledge-title">
				<h2><fmt:message key="msg.pg.top.borrow"/></h2>
				<h3>借贷数字资产，尊享急速到账体验。</h3>
			</div>
		</div>
		
		<div class="pledge-content">
			<div class="pledge-content-div pledge-border">
				<div class="pledge-content-title">
					<h4><fmt:message key="msg.pg.top.borrow"/></h4>
				</div>
				
				<form autocomplete="off" method="post" onsubmit="return false;">
					<div class="pledge-content-cnt">
						<div class="pledge-content-item pledge-border">
							<div class="pledge-input-cnt">
								<span class="pledge-label">借入数量</span>
								<input type="text" id="borrow" name="borrow" required onkeyup="checkInputDigits(this,${setting.borrow_digits});onBorrowChanage();" class="pledge-input" maxlength="18" placeholder="起借数量 <fmt:formatNumber pattern="###0.########" value='${setting.min_borrow_amount}'/> ${setting.borrow_symbol}" spellcheck="false" autocomplete="off">
								<span class="pledge-label link" id="allPledge" onClick="allPledgeAction()">全部</span>
							</div>
							<div class="pledge-coin-select"><span id="borrowCoin">${setting.borrow_symbol}</span></div>
						</div>
						<div class="pledge-content-item pledge-border">
							<div class="pledge-input-cnt" style="background:#f4f5f7;">
								<span class="pledge-label">质押数量</span>
								<input type="text" id="pledge" name="pledge" class="pledge-input" readonly="readonly" maxlength="18" placeholder="可质押数量  <fmt:formatNumber pattern="#0.########" value='${balance}'/> ${setting.pledge_symbol}" spellcheck="false" autocomplete="off">
							</div>
							<div class="pledge-coin-select"><span id="pledgeCoin">${setting.pledge_symbol}</span></div>
						</div>
						
						<div class="pledge-content-item" style="flex-direction: column;">
							<div class="pledge-interest-text">
								<span class="pledge-tip">初始质押率:</span> <span><fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.init_pledge_rate}" /></span>
								<div class="show-contract-box display-none" style="bottom: auto;">
                            		<div class="triangle"></div>
									<div class="content-detail">
										<p class="scroll_div"><h4>初始质押率</h4>
											当使用${setting.pledge_symbol}币种作为质押物时，可借贷的资产折合${setting.borrow_symbol}价值/质押资产${setting.borrow_symbol}价值≤建议的初始比例<fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.init_pledge_rate}" />。
										</p>
									</div>
                            	</div>
							</div>
							<div class="pledge-interest-text">
								<span class="pledge-tip">补仓质押率:</span> <span><fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.curr_pledge_rate}" /></span>
								<div class="show-contract-box display-none" style="bottom: auto;">
                            		<div class="triangle"></div>
									<div class="content-detail">
										<p class="scroll_div"><h4>补仓质押率</h4>
											当使用${setting.pledge_symbol}作为质押物时，当本订单借贷资产加累计利息${setting.borrow_symbol}价值/质押资产${setting.borrow_symbol}价值≥<fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.curr_pledge_rate}" />时，系统会提醒您补充质押资产。
										</p>
									</div>
                            	</div>
							</div>
							<div class="pledge-interest-text">
								<span class="pledge-tip">平仓质押率:</span> <span><fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.close_pledge_rate}" /></span>
								<div class="show-contract-box display-none" style="bottom: auto;">
                            		<div class="triangle"></div>
									<div class="content-detail">
										<p class="scroll_div"><h4>平仓质押率</h4>
											当使用${setting.pledge_symbol}作为质押物时，当本订单借贷资产加累计利息${setting.borrow_symbol}价值/质押资产${setting.borrow_symbol}价值≥<fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${setting.close_pledge_rate}" />时，系统会自动平仓您的质押资产来偿还借币。请及时补充质押金避免平仓。
										</p>
									</div>
                            	</div>
							</div>
						</div>
						
						<div>借币期限</div>
						<div class="pledge-content-item">
							<c:forEach items="${rateList}" var="rate" varStatus="status">
								<div class="pledge-cycle pledge-border ${status.index == 0 ? 'pledge-active' : ''}" 
									onClick="pledgeCycleAction(this)" cycle="${rate.cycle}" rate="${rate.day_rate}">
									<span>${rate.cycle}天</span>
									<c:if test="${status.index == 0 }">
										<input type="hidden" id="cycle" name="cycle" value="${rate.cycle}"/>
										<input type="hidden" id="dayRate" name="dayRate" value="${rate.day_rate}"/>
									</c:if>
								</div>
							</c:forEach>
						</div>
						
						<div class="pledge-content-item" style="flex-direction: column;">
							<div class="pledge-interest-text">日币息率: <span id="dayRateText">0.00%</span></div>
							<div class="pledge-interest-text">年化利率: <span id="yearRateText">0.00%</span></div>
							<div class="pledge-interest-text">总币息: <span id="allInterest">0.00000000</span> ${setting.borrow_symbol}</div>
						</div>
						
						<div class="pledge-content-item" style="border-bottom: 1px solid #f1f3f4;"></div>
						
						<div class="pledge-content-item">
							<div class="pledge-rate-text">到账数量:</div><div style="font-size:20px;"><span id="allBack">0.00000000</span> ${setting.borrow_symbol}</div>
						</div>
						
						<div class="pledge-content-item" style="margin-bottom:0px;">
							<input type="submit" class="btn" onClick="pledgeAction()" id="btn-pt" value="立即借币" />
						</div>
					</div>
				</form>
			</div>
			
			<div class="pledge-account">
				<div class="pledge-content-div pledge-border" style="flex:${isBorrow ? 'none' : '1'};">
					<div class="pledge-content-title">
						<h4>我的资产</h4>
						<c:if test="${sessionScope.userinfo==null }">
							<a class="link" href="../login/login.do" style="font-size:12px;">登录查看</a>
						</c:if>
					</div>
					<div class="pledge-account-cnt">
						<div class="pledge-content-item pledge-account-item">
							<div class="">已借金额折合 </div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${allBorrow}'/> USDT</div>
						</div>
						<div class="pledge-content-item pledge-account-item" style="margin-bottom: 5px;">
							<div class="">当前质押金额折合 </div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${allPledge}'/> USDT</div>
						</div>
					</div>
				</div>
				
				<c:if test="${isBorrow}">
				<div class="pledge-content-div pledge-border" style="margin-top: 20px;">
					<div class="pledge-content-title">
						<h4>理财借出</h4>
						<a class="link" href="out/orders.do?type=0" style="font-size:12px;">查看明细</a>
					</div>
					<div class="pledge-account-cnt">
						<div class="pledge-content-item pledge-account-item">
							<div class="">可用余额</div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${borrowBalance}'/> ${setting.borrow_symbol}</div>
						</div>
						<div class="pledge-content-item pledge-account-item">
							<div class="">理财中 [已借] </div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${nowBorrowOut}'/> ${setting.borrow_symbol}</div>
						</div>
						<div class="pledge-content-item pledge-account-item">
							<div class="">预计收益</div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${willIncome}'/> ${setting.borrow_symbol}</div>
						</div>
						<div class="pledge-content-item pledge-account-item">
							<div class="">累计理财</div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${allBorrowOut}'/> ${setting.borrow_symbol}</div>
						</div>
						<div class="pledge-content-item pledge-account-item">
							<div class="">累计收益</div>
							<div class="pledge-account-val"><fmt:formatNumber pattern="#0.00000000" value='${haveIncome}'/> ${setting.borrow_symbol}</div>
						</div>
					</div>
				</div>
				</c:if>
			</div>
		</div>
		
		<c:if test="${sessionScope.userinfo!=null }">
		<div class="pledge-content" style="padding-bottom:24px;margin:20px auto;">
			<div class="pledge-content-div pledge-border">
				<div class="pledge-content-title">
					<h4>在借订单</h4>
					<a class="link" href="orders.do?type=1" style="font-size:12px;">查看历史订单</a> 
				</div>
				
				<div class="trade-manage-main">
			        <table cellpadding="0" cellspacing="0">
						<thead>
						  <tr>
						    <th>借入时间</th>
						    <th>币种</th>
						    <th>借入数量</th>
						    <th>周期</th>
						    <th>还款日期</th>
						    <th>质押币种</th>
						    <th>质押数量</th>
						    <th>状态</th>
						    <th>操作</th>
						  </tr>
						</thead>
						<tbody>
							<c:forEach items="${pm.datas}" var="r" varStatus="status">
								<tr>
									<td><ut:formatDate dateline="${r.record_time}"></ut:formatDate></td>
									<td>${r.borrow_symbol}</td>
									<td>${r.borrow_amount.doubleValue()}</td>
									<td>${r.cycle}</td>
									<td><ut:formatDate dateline="${r.record_time + r.cycle * 24 * 3600}"></ut:formatDate></td>
									<td>${r.pledge_symbol}</td>
									<td>${r.pledge_amount.doubleValue()}</td>
									<td>
									<c:if test="${r.state == 0}">申请中</c:if>
									<c:if test="${r.state == 1}">${(r.record_time + r.cycle * 24 * 3600) > nowTime ? '计息中' : '已逾期'}</c:if>
									<c:if test="${r.state == 2}">已还款</c:if>
									<c:if test="${r.state == 3}">已平仓</c:if>
									
									</td>
									<td><a title="${(r.record_time + r.cycle * 24 * 3600) > nowTime ? '未到期暂不能还币' : ''}" class="link ${(r.record_time + r.cycle * 24 * 3600) > nowTime ? 'disable' : ''}"
										<c:if test="${(r.record_time + r.cycle * 24 * 3600) <= nowTime}">
											onClick="backCoin(${r.id},'${r.borrow_symbol}',${r.borrow_amount},${r.pledge_amount},'${r.pledge_symbol}',${r.cycle},${r.day_rate})"
										</c:if>>一键还币</a>
									 <a class="link" style="margin-left: 15px;" onClick="adjustCoin(${r.id},${r.borrow_amount},'${r.pledge_symbol}',${r.pledge_amount},${r.cycle},${r.pledge_rate})">调整质押金</a></td>
								</tr>
							</c:forEach>
						</tbody>
			          </table>
			          <ul class="pagination">
							<pg:pager url="financial/pledge.do" items="${pm.total}"
								maxIndexPages="10" maxPageItems="${pm.maxitems}"
								export="currentPageNumber=pageNumber">
									<pg:prev> <li class="disabled">
										<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a></li>
									</pg:prev>
							
							
									<pg:pages>
										<c:choose>
											<c:when test="${currentPageNumber eq pageNumber }">
												<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
											</c:when>
											<c:otherwise><li>
												<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
											</c:otherwise>
										</c:choose>
									</pg:pages>
									<pg:next>
									<li><a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a></li>
								</pg:next>
							</pg:pager>
						</ul>
						
						<c:if test="${pm.datas.size() == 0}">
							<div class="pledge-no-record">暂无记录</div>
						</c:if>
		          </div>
			</div>
		</div>
		</c:if>

		<jsp:include page="_footer.jsp"></jsp:include>
		
		<div class="show-msg-box" id="showBox" style="display: none;"><div id="showBoxText" class="successNew">借币成功！</div></div>
	</body>
	
	<script>
	var pledgeRate = ${setting.init_pledge_rate};
	var lastPrice = ${lastPrice};
	
	function closeAdjustCoin() {
		$("#adjustCoinWindow").hide();
	}
	
	function closeBackCoin() {
		$("#backCoinWindow").hide();
	}
	
	function doAdjustCoin() {
		var recordId = $("#recordId1").val();
		
		var adjust = $("#adjustAmount").val();
		var conf = Number($("#coefficient").val());
		if (adjust != "") {
			$('#adjustCoinBtn').attr("disabled",true);
			$('#adjustCoinBtn').html("提交中...");
			$.post(basePath + 'financial/adjustCoin.do', {
				recordId: recordId,
				amount: (adjust * conf)
			}, function(data) {
				if (data.code == 'success') {
					showMsgBox(data.code, "调整成功");
				} else {
					showMsgBox(data.code, data.msg);
					$('#adjustCoinBtn').attr("disabled",false);
					$('#adjustCoinBtn').html("确认调整");
				}
			}, "json");
		} else {
			showMsgBox("failed", "请填写数量！");
		}
	}
	
	function doBackCoin() {
		var recordId = $("#recordId").val();
		$('#backCoinBtn').attr("disabled",true);
		$('#backCoinBtn').html("提交中...");
		$.post(basePath + 'financial/backCoin.do', {
			recordId: recordId
		}, function(data) {
			if (data.code == 'success') {
				showMsgBox(data.code, "还币成功");
			} else {
				showMsgBox(data.code, data.msg);
				$('#backCoinBtn').attr("disabled",false);
				$('#backCoinBtn').html("确认归还");
			}
		}, "json");
	}
	
	function backCoin(id, symbol, amount, pledge, pledgeSymbol, cycle, rate) {
		$("#recordId").val(id);
		
		$("#backContent").html(amount + " " + symbol);
		$("#releaseContent").html(pledge + " " + pledgeSymbol);
		$("#backCoinWindow").show();
	}
	
	function adjustCoin(id, borrow, symbol, amount, cycle, rate) {
		$("#recordId1").val(id);
		$("#adjustBorrow").val(borrow);
		$("#adjustPledge").val(amount);
		$("#adjustInitRate").val(rate);
		
		var realPledge = borrow / lastPrice;
		var nowr = parseFloat((realPledge / amount).toFixed(8));
		var nowRate = parseFloat((nowr * 100).toFixed(8));
		$("#currRate").html(nowRate + "%");
		$("#afterRate").html(nowRate + "%");
		
		if (nowr >= rate) { // 追加
			$("#adjustTitle").html("追加质押金");
			$("#adjustLabel").html("追加数量");
			$("#coefficient").val(1);
			$("#allAdjust").show();
		} else {
			$("#adjustTitle").html("减少质押金");
			$("#adjustLabel").html("减少数量");
			$("#coefficient").val(-1);
			$("#allAdjust").hide();
		}
		
		$("#adjustCoinWindow").show();
	}
	
	function initAdjust() {
		var rate = $("#adjustInitRate").val();
		var conf = Number($("#coefficient").val());
		var borrow = Number($("#adjustBorrow").val());
		var amount = Number($("#adjustPledge").val());
		var realPledge = borrow / lastPrice;
		var allPledge = Number(realPledge) / Number(rate);
		var adjust = (Number(allPledge) - amount) * conf;
		$("#adjustAmount").val(parseFloat(adjust.toFixed(8)));
		$("#afterRate").html(parseFloat(Number(rate) * 100) + "%");
	}
	
	function allAdjust() {
		$("#adjustAmount").val(${balance});
		onAdjustAmount();
	}
	
	function onAdjustAmount() {
		var adjust = $("#adjustAmount").val();
		var conf = Number($("#coefficient").val());
		if (adjust != "") {
			var borrow = Number($("#adjustBorrow").val());
			var amount = Number($("#adjustPledge").val());
			
			var realPledge = borrow / lastPrice;
			var rate = Number(realPledge) / (Number(amount) + Number(adjust) * conf) * 100.0;
			var nowRate = parseFloat(rate.toFixed(8));
			$("#afterRate").html(nowRate + "%");
		} else {
			$("#afterRate").html($("#currRate").html());
		}
	}
	
	function formatNumberDown(number, digits) {
		var numStr = number + "";
		if (numStr.indexOf('.') != -1 && numStr.length - numStr.indexOf('.') > digits) {
			numStr = numStr.substring(0, numStr.indexOf('.') + digits + 1);
		}
		
		return Number(numStr);	
	}
	
	function onBorrowChanage() {
		var borrow = 0;
		if ($("#borrow").val() != "") {
			borrow = parseFloat($("#borrow").val());
		}
		
		var dayRate = parseFloat($("#dayRate").val());
		if (lastPrice > 0 && borrow > 0) {
			var cycle = $("#cycle").val();
			var dayRate = $("#dayRate").val();
			var pledge = borrow / lastPrice / pledgeRate;
			var interest = borrow * cycle * dayRate;
			
			$("#pledge").val(pledge.toFixed(8));
			$("#allInterest").html(interest.toFixed(8));
			$("#allBack").html((borrow - interest).toFixed(8));
		} else {
			$("#pledge").val("");
			$("#allInterest").html("0.00000000");
			$("#allBack").html("0.00000000");
		}
	}
	
	function showMsgBox(code, text) {
		if(text.length > 0){
			if (code == "success") {
				$("#showBoxText").removeClass('warningNew');
				$("#showBoxText").addClass('successNew');
			} else {
				$("#showBoxText").addClass('warningNew');
				$("#showBoxText").removeClass('successNew');
			}
			
			$("#showBoxText").html(text);
			
			var time = 2000;
			if (code == 'error') {
				time = 3000;
			}
			
			$("#showBox").css("display", "block");
			
			setTimeout(function() {
				$("#showBox").css("display", "none");
				if (code == "success") {
					location.reload();
				}
			}, time);
		}
	}
	
	function pledgeAction() {
		var borrow = $("#borrow").val();
		if (borrow != "" && borrow > 0) {
			var cycle = $("#cycle").val();
			
			$('#btn-pt').attr("disabled",true);
			$('#btn-pt').val("提交中...");
			
			$.post(basePath + 'financial/pledgedo.do', {
				borrow: borrow,
				cycle: cycle,
				loanId: ${setting.id }
			}, function(data) {
				if (data.code == 'success') {
					showMsgBox(data.code, "借币成功！");
					$("#borrow").val("");
					onBorrowChanage();
				} else {
					showMsgBox(data.code, data.msg);
				}
				$('#btn-pt').val("立即借币");
				$('#btn-pt').attr("disabled",false);
			}, "json");
		}
	}
	
	function pledgeCycleAction(sender) {
		console.log($(sender));
		$(sender).addClass("pledge-active").siblings().removeClass("pledge-active");
		$("#cycle").val($(sender).attr("cycle"));
		$("#dayRate").val($(sender).attr("rate"));
		
		var dayRate = parseFloat($(sender).attr("rate"));
		var percent = (dayRate * 100).toFixed(6) + "%";
		$("#dayRateText").html(percent);
		percent = (dayRate * 100 * 365).toFixed(2) + "%";
		$("#yearRateText").html(percent);
		
		onBorrowChanage();
	}
	
	function allPledgeAction() {
		if (lastPrice > 0) {
			var borrow = ${balance} * lastPrice * pledgeRate;
			$("#borrow").val(formatNumberDown(borrow, ${setting.borrow_digits}));
			onBorrowChanage();
		}
	}
	
	$(function() {
		$(".pledge-cycle.pledge-active").click();
	});
	
	</script>
</html>

