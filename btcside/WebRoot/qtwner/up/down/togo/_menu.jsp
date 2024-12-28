<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="jsp/_basepage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf8">
    <link href="<%=jspPath%>css/admin.css" type="text/css" rel="stylesheet">
    <script language="javascript">
        function expand(el) {
            childObj = document.getElementById("child" + el);

            if (childObj.style.display == 'none') {
                childObj.style.display = 'block';
            }
            else {
                childObj.style.display = 'none';
            }
            return;
        }

    </script>
</head>
<body>
    <table height="100%" cellspacing="0" cellpadding="0" width="170" background="<%=jspPath%>images/menu_bg.jpg" border="0">
        <tr>
            <td valign="top" align="middle">
                <table cellspacing="0" cellpadding="0" width="100%" border="0">
                    <tr>
                        <td height="10">
                        </td>
                    </tr>
                </table>
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="<%=jspPath%>images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(1)" href="javascript:void(0);">基本功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child1" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                	<c:if test="${sessionScope.menuRoles.contains('managerAdmin') }">
                	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerAdmin/loadManager.do" target="main">管理员信息管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerMenu') }">
                	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerMenu/loadManagerMenu.do" target="main">菜单链接管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerNotice') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerNotice/inpage.do" target="main">公告资讯管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerExpressNews') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExpressNews/loadNews.do" target="main">快讯管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUser') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUser/loadUsers.do" target="main">用户管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSubUserCreate') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSubUserCreate/load.do" target="main">创建子账号</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserSearch') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserSearch/loadAccount.do" target="main">用户合约资产管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerCurrencyType') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCurrencyType/load.do" target="main">资产币种管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserSearch') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserSearch/loadUserSearch.do" target="main">用户资产查询</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerRecharge') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerRecharge/loadRecords.do" target="main">充值提现</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerRecharge') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerRecharge/loadAdmin.do?typeId=2" target="main">在线充值及奖励</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerWallet') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerWallet/loadRecords.do" target="main">收款地址管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerExchangeRate') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchangeRate/loadExchangeRate.do" target="main">汇率比例管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserSuggest') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserSuggest/loadUserSuggest.do" target="main">用户意见采纳管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUser') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUser/loadGoogle.do" target="main">解除谷歌绑定</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUser/loadModifyRecord.do" target="main">安全验证修改记录</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('adminpc') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>adminpc/queryPage.do" target="main">admin平仓</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('adminCancel') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>adminCancel/cancel.do" target="main">admin撤单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserMonitor') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserMonitor/list.do" target="main">用户监控管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserRiskControl') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserRiskControl/list.do" target="main">用户提币和兑换风控</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserTradeWhite') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserTradeWhite/list.do" target="main">用户交易白名单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerTransfer') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTransfer/loadTransferRecords.do" target="main">合约账户转入转出</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserApi') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserApi/load.do" target="main">用户交易api管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserApiSetting') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserApiSetting/load.do" target="main">api延迟设置</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserApiWhite') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserApiWhite/list.do" target="main">api白名单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerQuantSetting') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantSetting/load.do" target="main">合约量化自动撤单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSpotQuantSetting') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotQuantSetting/load.do" target="main">币币量化自动撤单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerDeliveryQuantSetting') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerDeliveryQuantSetting/load.do" target="main">交割量化自动撤单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerOverloadMatch') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerOverloadMatch/matchManager.do" target="main">爆仓撮合指定配置</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerHoldCostPrice') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerHoldCostPrice/costManager.do" target="main">持仓成本调整</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserOpenLimit') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerUserOpenLimit/list.do" target="main">用户合约限仓管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerDeliveryUserOpenLimit') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerDeliveryUserOpenLimit/list.do" target="main">交割合约用户限仓</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerMarketOrder') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerMarketOrder/load.do" target="main">市价委托设置管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSpotRiskControlSetting') }">
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerSpotRiskControlSetting/load.do" target="main">币币单币种风控设置</a>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerIndexSateSetting') }">
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerIndexSateSetting/load.do" target="main">指数设置</a>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserLottery') }">
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerUserLottery/loadDraw.do" target="main">抽奖概率设置</a>
                            </td>
                        </tr>
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerUserLottery/loadDrawRecord.do" target="main">抽奖记录</a>
                            </td>
                        </tr>
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerUserLottery/loadUserDraw.do" target="main">用户抽奖统计</a>
                            </td>
                        </tr>
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="<%=jspPath%>images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath %>managerUserLottery/loadAppointRecord.do" target="main">用户指定奖品记录</a>
                            </td>
                        </tr>
                    </c:if>
                    <tr height="4">
                        <td colspan="2">
                        </td>
                    </tr>
                </table>
                
                <c:if test="${sessionScope.menuRoles.contains('managerLoanSetting') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(28)" href="javascript:void(0);">质押借币管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child28" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerLoanSetting/loanSettingManager.do" target="main">质押借币配置</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerLoanSetting/loanRateSettingManager.do" target="main">借贷周期利率配置</a>
                        </td>
                    </tr>
					<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerLoanSetting/loanRecordManager.do" target="main">质押借币记录查询</a>
                        </td>
                    </tr>
                </table>
                </c:if>
                
                <c:if test="${sessionScope.menuRoles.contains('managerStockSetting') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(6)" href="javascript:void(0);">股指交易时间管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child6" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerStockSetting/load.do" target="main">正常交易时间管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerStockSetting/loadNotradeTime.do" target="main">休市时间管理</a>
                        </td>
                    </tr>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerFundingFee')}">
				<table cellspacing="0" cellpadding="0" width="150" border="0">
					<tr height="22">
						<td style="padding-left: 30px" background="images/menu_bt.jpg">
							<a class="menuParent" onclick="expand(20)" href="javascript:void(0);">资金费管理</a>
						</td>
					</tr>
					<tr height="4">
						<td></td>
					</tr>
				</table>
				<table id="child20" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
					<tr height="20">
						<td align="middle" width="30">
							<img height="9" src="images/menu_icon.gif" width="9">
						</td>
						<td>
							<a class="menuChild" href="<%=actionPath %>managerFundingFee/fundingFeeSettingManager.do" target="main">资金费率配置</a>
						</td>
					</tr>
					<tr height="20">
						<td align="middle" width="30">
							<img height="9" src="images/menu_icon.gif" width="9">
						</td>
						<td>
							<a class="menuChild" href="<%=actionPath %>managerFundingFee/list.do" target="main">资金费记录</a>
						</td>
					</tr>
				</table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerFinancial') || sessionScope.menuRoles.contains('transEachRanking')}">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(7)" href="javascript:void(0);">财务管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child7" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerFinancial/statisticsData.do" target="main">钱包账户统计</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerFinancial/contractAccountStatistics.do" target="main">合约账户统计</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFinancial/loadNetTrade.do" target="main">显示净交易</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFinancial/loadCount.do" target="main">查询统计 </a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFinancial/quantTradeAmount.do" target="main">量化账户成交额 </a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFinancial/overloadAmount.do" target="main">爆仓结余统计</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFinancial/deliveryOverloadAmount.do" target="main">交割爆仓结余统计</a>
                        </td>
                    </tr>
                    <c:if test="${sessionScope.menuRoles.contains('transEachRanking') }">
                        <tr height="20">
                            <td align="middle" width="30">
                                <img height="9" src="images/menu_icon.gif" width="9">
                            </td>
                            <td>
                                <a class="menuChild" href="<%=actionPath%>transEachRanking/loadFormation.do" target="main">合约资产排行</a>
                            </td>
                        </tr>
                    </c:if>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerSysProfitloss') ||
                	 sessionScope.menuRoles.contains('managerTransHold') || sessionScope.menuRoles.contains('managerUser') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(2)" href="javascript:void(0);">风控中心</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child2" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                	<c:if test="${sessionScope.menuRoles.contains('managerSysProfitloss')}">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerOneProfitloss/loadDegreeRisk.do" target="main">风险度排序</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerTransHold')}">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTransHold/loadRankings.do" target="main">各合约持仓排名</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserIp')}">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>/managerUserIp/loadUserIp.do" target="main">用户登陆IP监控</a>
                        </td>
                    </tr>
                    </c:if>
                    <tr height="4">
                        <td colspan="2">
                        </td>
                    </tr>
                </table>
                </c:if>
				<c:if test="${sessionScope.menuRoles.contains('managerTrans')  ||
                	 sessionScope.menuRoles.contains('managerTransType') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(3)" href="javascript:void(0);">合约管理功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child3" style="display: none;" cellspacing="0" cellpadding="0" width="150" border="0">
               		<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/loadTransPartition.do" target="main">合约分区管理</a>
                        </td>
                    </tr>
                    <c:if test="${sessionScope.menuRoles.contains('managerTransType')}">
                	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTransType/loadManagerTransType.do" target="main">合约类型管理</a>
                        </td>
                    </tr>
                    </c:if>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/loadManagerTrans.do" target="main">合约管理</a>
                        </td>
                    </tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/loadTrans.do" target="main">合约参数管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/feeRateList.do" target="main">合约费率管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/loadTransIntroduce.do" target="main">合约详情介绍管理</a>
                        </td>
                    </tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/transort.do" target="main">合约排序管理</a>
                        </td>
                    </tr>
                    <c:if test="${sessionScope.menuRoles.contains('managerFloatRate') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerFloatRate/loadFloatRate.do" target="main">合约浮动比例管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrans/loadMarkPriceSetting.do" target="main">标记价格配置管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td colspan="2">
                        </td>
                    </tr>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerCompositeIndex') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(23)" href="javascript:void(0);">综合指数配比管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child23" style="display: none;" cellspacing="0" cellpadding="0" width="150" border="0">
               		<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCompositeIndex/loadCompositeType.do" target="main">综合指数配比类型</a>
                        </td>
                    </tr>
               		<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCompositeIndex/loadCompositeIndex.do" target="main">综合指数配比详情</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCompositeIndex/loadBenchmarkPrice.do" target="main">基准指数价格查看</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td colspan="2">
                        </td>
                    </tr>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerTrade') || sessionScope.menuRoles.contains('managerPending') || sessionScope.menuRoles.contains('managerTradeStat') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(4)" href="javascript:void(0);">合约交易管理功能</a>
                        </td>
                    </tr>
                    <tr height="6">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child4" style="display: none;" cellspacing="0" cellpadding="0" width="150" border="0">
                	<c:if test="${sessionScope.menuRoles.contains('managerTrade') }">
                	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrade/loadTrades.do" target="main">交易信息</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerTradeStat') }">
					<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTradeStat/statsTrades.do" target="main">交易统计</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerTrade') }">
                	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerTrade/tradeQuantitative.do" target="main">量化手续费统计</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerPending') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerPending/loadBuy.do" target="main">挂牌买入</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerPending/loadSell.do" target="main">挂牌出售</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerPending/planPending.do" target="main">计划委托</a>
                        </td>
                    </tr>
                    </c:if>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerDataBroadCast')}">
				<table cellspacing="0" cellpadding="0" width="150" border="0">
					<tr height="22">
						<td style="padding-left: 30px" background="images/menu_bt.jpg">
							<a class="menuParent" onclick="expand(22)" href="javascript:void(0);">持仓数据播报管理</a>
						</td>
					</tr>
					<tr height="4">
						<td></td>
					</tr>
				</table>
				<table id="child22" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
					<tr height="20">
						<td align="middle" width="30">
							<img height="9" src="images/menu_icon.gif" width="9">
						</td>
						<td>
							<a class="menuChild" href="<%=actionPath %>managerDataBroadCast/dataBroadCastSetting.do" target="main">数据播报设置</a>
						</td>
					</tr>
					<tr height="20">
						<td align="middle" width="30">
							<img height="9" src="images/menu_icon.gif" width="9">
						</td>
						<td>
							<a class="menuChild" href="<%=actionPath %>managerDataBroadCast/data.do" target="main">数据播报</a>
						</td>
					</tr>
				</table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerQuantRisk') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(5)" href="javascript:void(0);">量化风控</a>
                        </td>
                    </tr>
                    <tr height="6">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child5" style="display: none;" cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantTradeDetail.do" target="main">用户交易明细</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/loadQuantHoldAndPending.do" target="main">用户持仓挂单等</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantRechargeList.do" target="main">充提记录</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantIncomeRanking.do" target="main">用户盈亏额排名</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantFundRanking.do" target="main">用户资产排序</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantInvite.do" target="main">邀请关系查询</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/loadUsers.do" target="main">用户管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerQuantRisk/quantTradeMakerTaker.do" target="main">量化交易信息</a>
                        </td>
                    </tr>
                </table>
                </c:if>
                <c:if test="${sessionScope.menuRoles.contains('managerWorkOrder') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(8)" href="javascript:void(0);">工单管理功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child8" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerWorkOrder/loadWorkOrder.do" target="main">用户工单管理</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerUserLevelRule') ||  sessionScope.menuRoles.contains('managerCredit')
                 	|| sessionScope.menuRoles.contains('managerUserRebate') || sessionScope.menuRoles.contains('managerInviteManagement') 
                 	|| sessionScope.menuRoles.contains('managerLevelBuyLock')}">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(9)" href="javascript:void(0);">等级积分管理功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                 </table>
                 <table id="child9" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                	 <c:if test="${sessionScope.menuRoles.contains('managerUserLevelRule') }">
	                <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerUserLevelRule/loadLevels.do" target="main">等级规则管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerLevelBuy/setting.do" target="main">VIP购买设置</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerUserLevelRule/loadLevelBuyAmountLog.do" target="main">用户购买VIP等级记录</a>
                        </td>
                    </tr>
					</c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerLevelBuyLock') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerLevelBuyLock/levelBuyLock.do" target="main">平台币销售查询</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerCredit') }">
	                <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCredit/loadRecords.do" target="main">用户积分管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerUserRebate') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerUserRebate/loadUserRebateLog.do" target="main">用户返佣日志</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerInviteManagement') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerInviteManagement/list.do" target="main">会员管理系统</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>managerInviteManagement/inviteList.do" target="main">邀请管理系统</a>
                        </td>
                    </tr>
                    </c:if>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerAgent') }">
                <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(26)" href="javascript:void(0);">代理系统管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child26" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerAgent/load.do" target="main">代理人管理</a>
                        </td>
                    </tr>
                </table>
                </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerArbitrage') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(10)" href="javascript:void(0);">平台套利系统</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child10" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerArbitrage/depthManager.do" target="main">深度系统管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerArbitrage/scalpingManager.do" target="main">刷单系统管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerArbitrage/hedgingManager.do" target="main">对平及外部对冲管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerArbitrage/hedgingBalanceNotice.do?type=1" target="main">合约对冲账户资产通知</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerExchange') || sessionScope.menuRoles.contains('managerArbitrage')}">
                  <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(11)" href="javascript:void(0);">币币兑换功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child11" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                	<c:if test="${sessionScope.menuRoles.contains('managerExchange') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchange/exchangeSetting.do?id=0" target="main">兑换功能参数设置</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchange/loadRecords.do?id=0&uid=0&toUsd=true" target="main">兑换记录查询</a>
                        </td>
                    </tr>
                    <tr height="20" style="display:none;">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchange/adminExchange.do" target="main">后台兑换功能</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerArbitrage') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerArbitrage/hedgingBalanceNotice.do?type=2" target="main">币币兑换账户资产通知</a>
                        </td>
                    </tr>
                    </c:if>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerExchangeBfc')}">
                  <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(24)" href="javascript:void(0);">Btcside与BFC互兑功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child24" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchangeBfc/exchangeSetting.do?id=0" target="main">兑换功能参数设置</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerExchangeBfc/loadRecords.do?id=0&uid=0&toUsd=true" target="main">兑换记录查询</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerSpotMarket') || sessionScope.menuRoles.contains('managerSpotPending')
                 	|| sessionScope.menuRoles.contains('managerSpotTrades') || sessionScope.menuRoles.contains('adminCancelSpot') 
                 	|| sessionScope.menuRoles.contains('managerSpotArbitrage')}">
                  <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(12)" href="javascript:void(0);">币币交易管理功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                 <table id="child12" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                 	<c:if test="${sessionScope.menuRoles.contains('managerSpotMarket') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotMarket/loadManagerSpotMarket.do" target="main">交易区管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotMarket/loadCoinPair.do" target="main">交易对管理</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSpotPending') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotPending/loadBuy.do" target="main">币币交易挂单买入</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotPending/loadSell.do" target="main">币币交易挂单卖出</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSpotTrades') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotTrades/loadTrades.do" target="main">币币交易信息</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('adminCancelSpot') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>adminCancelSpot/cancel.do" target="main">admin撤单</a>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.menuRoles.contains('managerSpotArbitrage') }">
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotArbitrage/depthManager.do" target="main">币币交易深度系统</a>
                        </td>
                    </tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotArbitrage/scalpingManager.do" target="main">币币交易刷单系统</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotArbitrage/hedgingManager.do" target="main">外部对冲管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerSpotArbitrage/hedgingBalanceNotice.do?type=3" target="main">对冲账户资产通知</a>
                        </td>
                    </tr>
                    </c:if>
                 </table>
                 </c:if>
				<c:if test="${sessionScope.menuRoles.contains('managerBfx') }">
    				<table cellspacing="0" cellpadding="0" width="150" border="0">
						<tr height="22">
							<td style="padding-left: 30px" background="images/menu_bt.jpg">
								<a class="menuParent" onclick="expand(21)" href="javascript:void(0);">平台币Btcside功能管理</a>
							</td>
						</tr>
						<tr height="4">
							<td> </td>
						</tr>
            		</table>
					<table id="child21" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
						<tr height="20">
						   	<td align="middle" width="30">
						       <img height="9" src="images/menu_icon.gif" width="9">
						   	</td>
						   	<td>
						       <a class="menuChild" href="<%=actionPath %>managerBfx/bfxVipManager.do" target="main">Btcside享Vip等级管理</a>
						    </td>
						</tr>
						<tr height="20">
						   	<td align="middle" width="30">
						       <img height="9" src="images/menu_icon.gif" width="9">
						   	</td>
						   	<td>
						       <a class="menuChild" href="<%=actionPath %>managerBfx/bfxFeeManager.do" target="main">Btcside抵扣手续费管理</a>
						    </td>
						</tr>
						<tr height="20">
						   	<td align="middle" width="30">
						       <img height="9" src="images/menu_icon.gif" width="9">
						   	</td>
						   	<td>
						       <a class="menuChild" href="<%=actionPath %>managerBfx/records.do" target="main">Btcside手续费抵扣记录</a>
						    </td>
						</tr>
					</table>
				</c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerMine') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(13)" href="javascript:void(0);">挖矿及分红功能</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
				<table id="child13" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
					<tr height="20">
					   	<td align="middle" width="30">
					       <img height="9" src="images/menu_icon.gif" width="9">
					   	</td>
					   	<td>
					       <a class="menuChild" href="<%=actionPath %>managerMine/mineManager.do" target="main">挖矿参数设置</a>
					    </td>
					</tr>
					<tr height="20">
					   	<td align="middle" width="30">
					       <img height="9" src="images/menu_icon.gif" width="9">
					   	</td>
					   	<td>
					       <a class="menuChild" href="<%=actionPath %>managerMine/mineRecords.do" target="main">用户挖矿记录</a>
					    </td>
					</tr>
					<tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/ieoSettingManager.do" target="main">IEO释放配置</a>
				       </td>
				   </tr>
					<tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/ieoAccounts.do" target="main">IEO账户统计</a>
				       </td>
				   </tr>
					<tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/ieoRecords.do" target="main">IEO在线充值提现</a>
				       </td>
				   </tr>
				   <tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/ieoUnlockRecords.do" target="main">IEO解冻记录</a>
				       </td>
				   </tr>
				   <tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/bonusManager.do" target="main">分红参数设置</a>
				       </td>
				   </tr>
				   <tr height="20">
					    <td align="middle" width="30">
					        <img height="9" src="images/menu_icon.gif" width="9">
					    </td>
					    <td>
					        <a class="menuChild" href="<%=actionPath %>managerMine/bonusRecords.do" target="main">用户分红记录</a>
				       </td>
				   </tr>
				</table>
				</c:if>
				<c:if test="${sessionScope.menuRoles.contains('managerIndexDesc') }">
				<table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(14)" href="javascript:void(0);">指数说明设置</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child14" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerIndexDesc/loadIndexDesc.do" target="main">指数说明设置</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('statisticalForm') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(15)" href="javascript:void(0);">平台统计报表</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
                <table id="child15" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>statisticalForm/dayDatas.do" target="main">永续合约-每日报表</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>statisticalForm/deliveryDayDatas.do" target="main">交割合约-每日报表</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>statisticalForm/hourlyDatas.do" target="main">永续合约-每小时报表</a>
                        </td>
                    </tr>
                    <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath%>statisticalForm/daySpotDatas.do" target="main">币币-每日报表</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 <c:if test="${sessionScope.menuRoles.contains('managerBftReward') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(16)" href="javascript:void(0);">BFC赠送奖励管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
				<table id="child16" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerBftReward/bftRewardManager.do" target="main">奖励赠送设置</a>
                        </td>
                    </tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerBftReward/rewardRecords.do" target="main">奖励赠送记录</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 
                 <c:if test="${sessionScope.menuRoles.contains('delivery') || sessionScope.menuRoles.contains('managerDeliveryTransHold') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(25)" href="javascript:void(0);">交割合约管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
				<table id="child25" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/loadManagerTransType.do" target="main">合约类型管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/loadManagerTrans.do" target="main">合约管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerFloatRate/loadFloatRate.do" target="main">合约浮动比例管理</a>
                       </td>
                   	</tr>
                    <tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/autoCreateSetting.do" target="main">日交割自动创建管理</a>
                       </td>
                   	</tr>
                    <tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/loadTransPartition.do" target="main">分区管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/loadTransIntroduce.do" target="main">合约详情管理</a>
                        </td>
                    </tr>
                   	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/feeRateList.do" target="main">合约费率管理</a>
                        </td>
                    </tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerArbitrage/depthManager.do" target="main">深度系统管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerArbitrage/scalpingManager.do" target="main">刷单系统管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerArbitrage/hedgingManager.do" target="main">对平及外部对冲管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerTrade/loadTrades.do" target="main">交易信息</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerPending/loadBuy.do" target="main">挂牌买入</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerPending/loadSell.do" target="main">挂牌出售</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerPending/planPending.do" target="main">计划委托</a>
                       </td>
                   	</tr>
                   	<!-- 
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>delivery/managerTrans/loadManagerTrans.do" target="main">合约资产管理</a>
                       </td>
                   	</tr>
                   	 -->
                   	<c:if test="${sessionScope.menuRoles.contains('managerDeliveryTransHold') }">
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>managerDeliveryTransHold/loadRankings.do" target="main">合约持仓排名</a>
                       </td>
                   	</tr>
                   	</c:if>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>delivery/managerOneProfitloss/loadDegreeRisk.do" target="main">风险度排序</a>
                        </td>
                    </tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>delivery/managerOverloadMatch/matchManager.do" target="main">爆仓撮合指定配置</a>
                        </td>
                    </tr>
                 </table>
                 </c:if>
                 
                 <c:if test="${sessionScope.menuRoles.contains('simulation') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(17)" href="javascript:void(0);">模拟交易管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
				<table id="child17" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>simulation/managerTrans/loadManagerTransType.do" target="main">模拟合约类型管理</a>
                        </td>
                    </tr>
                    <tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerTrans/loadManagerTrans.do" target="main">模拟合约管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>simulation/managerTrans/loadTransIntroduce.do" target="main">模拟合约详情管理</a>
                        </td>
                    </tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerArbitrage/depthManager.do" target="main">深度系统管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerArbitrage/scalpingManager.do" target="main">刷单系统管理</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerTrade/loadTrades.do" target="main">交易信息</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerPending/loadBuy.do" target="main">挂牌买入</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerPending/loadSell.do" target="main">挂牌出售</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerPending/planPending.do" target="main">计划委托</a>
                       </td>
                   	</tr>
                   	<!-- 
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerTrans/loadManagerTrans.do" target="main">合约资产管理</a>
                       </td>
                   	</tr>
                   	 -->
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerTransHold/loadRankings.do" target="main">合约持仓排名</a>
                       </td>
                   	</tr>
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>simulation/managerOverloadMatch/matchManager.do" target="main">爆仓撮合指定配置</a>
                        </td>
                    </tr>
                    <tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerFundingFee/fundingFeeSettingManager.do" target="main">资金费率配置</a>
                       </td>
                   	</tr>
                   	<tr height="20">
                       <td align="middle" width="30">
                           <img height="9" src="images/menu_icon.gif" width="9">
                       </td>
                       <td>
                           <a class="menuChild" href="<%=actionPath %>simulation/managerFundingFee/list.do" target="main">资金费记录</a>
                       </td>
                   	</tr>
                 </table>
                 </c:if>
                 
                 <c:if test="${sessionScope.menuRoles.contains('managerCertificateType') || sessionScope.menuRoles.contains('managerMemberCertificate') }">
                 <table cellspacing="0" cellpadding="0" width="150" border="0">
                    <tr height="22">
                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
                            <a class="menuParent" onclick="expand(18)" href="javascript:void(0);">荣誉证书管理</a>
                        </td>
                    </tr>
                    <tr height="4">
                        <td>
                        </td>
                    </tr>
                </table>
				<table id="child18" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
				 	<c:if test="${sessionScope.menuRoles.contains('managerCertificateType') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerCertificateType/load.do" target="main">证书类型</a>
                        </td>
                    </tr>
                    </c:if>
                     <c:if test="${sessionScope.menuRoles.contains('managerMemberCertificate') }">
                     <tr height="20">
                        <td align="middle" width="30">
                            <img height="9" src="images/menu_icon.gif" width="9">
                        </td>
                        <td>
                            <a class="menuChild" href="<%=actionPath %>managerMemberCertificate/load.do" target="main">用户荣誉证书</a>
                        </td>
                    </tr>
                    </c:if>
                 </table>
                 </c:if>
                 
                 <c:if test="${sessionScope.menuRoles.contains('managerApp')}">
	                 <table cellspacing="0" cellpadding="0" width="150" border="0">
	                    <tr height="22">
	                        <td style="padding-left: 30px" background="images/menu_bt.jpg">
	                            <a class="menuParent" onclick="expand(19)" href="javascript:void(0);">APP管理</a>
	                        </td>
	                    </tr>
	                    <tr height="4">
	                        <td>
	                        </td>
	                    </tr>
	                </table>
					<table id="child19" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
	                     <tr height="20">
	                        <td align="middle" width="30">
	                            <img height="9" src="images/menu_icon.gif" width="9">
	                        </td>
	                        <td>
	                            <a class="menuChild" href="<%=actionPath %>managerApp/appManager.do" target="main">APP管理</a>
	                        </td>
	                    </tr>
	                 </table>
                 </c:if>

                 <c:if test="${sessionScope.menuRoles.contains('managerSpotRiskControl')}">
                     <table cellspacing="0" cellpadding="0" width="150" border="0">
                         <tr height="22">
                             <td style="padding-left: 30px" background="images/menu_bt.jpg">
                                 <a class="menuParent" onclick="expand(30)" href="javascript:void(0);">币币风控</a>
                             </td>
                         </tr>
                         <tr height="4">
                             <td>
                             </td>
                         </tr>
                     </table>
                     <table id="child30" style="display: none" cellspacing="0" cellpadding="0" width="150" border="0">
                         <tr height="20">
                             <td align="middle" width="30">
                                 <img height="9" src="images/menu_icon.gif" width="9">
                             </td>
                             <td>
                                 <a class="menuChild" href="<%=actionPath %>managerSpotRiskControl/statisticsData.do" target="main">钱包账户统计</a>
                             </td>
                         </tr>
                         <tr height="20">
                             <td align="middle" width="30">
                                 <img height="9" src="images/menu_icon.gif" width="9">
                             </td>
                             <td>
                                 <a class="menuChild" href="<%=actionPath %>managerSpotRiskControl/loadBuy.do" target="main">币币挂单买入</a>
                             </td>
                         </tr>
                         <tr height="20">
                             <td align="middle" width="30">
                                 <img height="9" src="images/menu_icon.gif" width="9">
                             </td>
                             <td>
                                 <a class="menuChild" href="<%=actionPath %>managerSpotRiskControl/loadSell.do" target="main">币币挂单卖出</a>
                             </td>
                         </tr>
                         <tr height="20">
                             <td align="middle" width="30">
                                 <img height="9" src="images/menu_icon.gif" width="9">
                             </td>
                             <td>
                                 <a class="menuChild" href="<%=actionPath %>managerSpotRiskControl/loadTrades.do" target="main">币币交易信息</a>
                             </td>
                         </tr>
                     </table>
                 </c:if>
            </td>
            <td width="1" bgcolor="#d1e6f7">
            </td>
        </tr>
    </table>
    <script type="text/javascript">expand(1);</script>
</body>
</html>
