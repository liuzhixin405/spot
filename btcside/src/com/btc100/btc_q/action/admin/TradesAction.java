package com.btc100.btc_q.action.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.MemberTradeDetail;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.ExcelExportUtil;
import com.btc100.btc_q.util.ExcelExportUtil.CellMap;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.TradeDetailVo;

@Controller
@RequestMapping("/managerTrade")
public class TradesAction extends BaseAction {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQMemberService bqMemberService;

	
	@RequestMapping("/loadTradeLogs") 
	public String loadDetail(int id, String code, Model model) throws Exception {
		if (id > 0) {
			BQTransaction currentTransaction = null;
			if (!StringUtil.isEmpty(code) ) {
				currentTransaction = transactionService.readTransByCode(code);
			}
			
			if (currentTransaction == null) {
				return super.url_JSP("tradeLogList");
			}
			
			model.addAttribute("currentTransaction", currentTransaction);
			PagerModel tradeLogList = new PagerModel();
			tradeLogList = tradeLogService.findByUserIdAndTransaction(code, id, 0, 0, 0);

			model.addAttribute("pm", tradeLogList);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("code", code);
		return super.url_JSP("tradeLogList");
	}

	@RequestMapping("/loadTrades")
	public String loadTrades(Model mo, String tcode, String buyuser, String selluser, String startTimeStr,
			String endTimeStr, String quantitativeId, String tag) throws ParseException {
		List<BQTransaction> transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("lstTrans", transactionList);
		
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(tcode)) {
				tcode = transactionList.get(0).getTransaction_code();
			}
		} else {
			PagerModel pm = new PagerModel();
			pm.setDatas(null);
			pm.setTotal(0);
			mo.addAttribute("pm", pm);
			
			return super.url_JSP("tradelists");
		}
		
		int buyuserid = ConvertUtil.toInt(buyuser, 0);
		int selluserid = ConvertUtil.toInt(selluser, 0);
		if (buyuserid > 0)
			mo.addAttribute("buyuser", buyuserid);
		if (selluserid > 0)
			mo.addAttribute("selluser", selluserid);
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (!StringUtil.isEmpty(startTimeStr)) {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (!StringUtil.isEmpty(endTimeStr)) {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		mo.addAttribute("startTime", startTimeStr);
		mo.addAttribute("endTime", endTimeStr);
		
		if (StringUtil.isEmpty(quantitativeId)) {
			quantitativeId = "8670,8774";
		}
		if (!StringUtil.isEmpty(tag)) {
			mo.addAttribute("tag", tag);
		}
		mo.addAttribute("quantitativeId", quantitativeId);
		
		mo.addAttribute("hyh", tcode);
		mo.addAttribute("current", tcode);
		
		mo.addAttribute("pm", memberTradeDetailService.findModel(tcode, buyuserid, selluserid, startTime, endTime, quantitativeId, tag));
		double itotal = memberTradeDetailService.findTotal(tcode, buyuserid, selluserid, startTime, endTime, quantitativeId, tag);
		mo.addAttribute("total", itotal);
		
		mo.addAttribute("dc", bqMemberTransactionService.readAllUserAmount(tcode, 1, 1)); //② 多单持仓量 多单持仓可用
		mo.addAttribute("dd", bqMemberTransactionService.readAllUserAmount(tcode, 0, 1)); //③ 多单冻结量 多单持仓冻结
		mo.addAttribute("db", pendingDelegationService.getSumByAction(tcode, Contents.DIRECTION_BUY, Contents.PRIORITY_BC)); //④ 多单爆仓量
		
		mo.addAttribute("kc", bqMemberTransactionService.readAllUserAmount(tcode, 1, 2)); //⑥ 空单持仓量 空单持仓可用
		mo.addAttribute("kd", bqMemberTransactionService.readAllUserAmount(tcode, 0, 2)); //⑦ 空单冻结量 空单持仓冻结
		mo.addAttribute("kb", pendingDelegationService.getSumByAction(tcode, Contents.DIRECTION_SELL, Contents.PRIORITY_BC)); //⑧ 空单爆仓量
		
		return super.url_JSP("tradelists");
	}
	
	@RequestMapping("/tradeQuantitative")
	public String tradeQuantitative(Model mo, String code, String startTimeStr, String endTimeStr, String userName,
			String userId) throws ParseException {
		List<BQTransaction> transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("lstTrans", transactionList);
		PagerModel pm = new PagerModel();
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(code)) {
				code = transactionList.get(0).getTransaction_code();
			}
		} else {
			pm.setDatas(null);
			pm.setTotal(0);
			mo.addAttribute("pm", pm);
			
			return super.url_JSP("tradeQuantitative");
		}
		
		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
			uId = user.getF_user_id();
		}
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		pm = memberTradeDetailService.findQuantitativeModel(code, startTime, endTime, uId);
		BigDecimal fee = memberTradeDetailService.getQuantitativeFee(code, startTime, endTime, uId);
		double totalFee = fee.doubleValue();
		
		mo.addAttribute("pm", pm);
		mo.addAttribute("totalFee", totalFee);
		mo.addAttribute("current", code);
		mo.addAttribute("userId", userId);
		mo.addAttribute("uId", uId > 0 ? uId : "");
		mo.addAttribute("userName", userName);
		mo.addAttribute("startTime", startTimeStr);
		mo.addAttribute("endTime", endTimeStr);
		
		return super.url_JSP("tradeQuantitative");
	}
	
	@RequestMapping("/export")
    public void export(String tcode, String buyuser, String selluser, String startTimeStr,
			String endTimeStr, String quantitativeId, String tag, HttpServletResponse response, HttpServletRequest request) throws ParseException {
		int buyuserid = ConvertUtil.toInt(buyuser, 0);
		int selluserid = ConvertUtil.toInt(selluser, 0);
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (!StringUtil.isEmpty(startTimeStr)) {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (!StringUtil.isEmpty(endTimeStr)) {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		List<MemberTradeDetail> details = memberTradeDetailService.findByTime(tcode, buyuserid, selluserid, startTime, endTime, quantitativeId, tag);
		
		List<TradeDetailVo> list = new ArrayList<TradeDetailVo>();
		for (int i = 0; i < details.size(); i++) {
			TradeDetailVo detailVo = new TradeDetailVo();
			detailVo.setId(i + 1);
			detailVo.setCode(details.get(i).getTransaction_code());
			Integer buy_type_id = details.get(i).getTrade_buy_type_id();
			String buyTradeType = getTradeTypeStr(buy_type_id);
			detailVo.setBuyer(String.valueOf(details.get(i).getF_buy_uid()) + "--" + details.get(i).getF_buy_user_email() + "--" + buyTradeType);
			Integer sell_type_id = details.get(i).getTrade_sell_type_id();
			String sellTradeType = getTradeTypeStr(sell_type_id);
			detailVo.setSeller(String.valueOf(details.get(i).getF_sell_uid()) + "--" + details.get(i).getF_sell_user_email() + "--" + sellTradeType);
			detailVo.setTradePrice(details.get(i).getTrade_price().doubleValue());
			detailVo.setTradeCount(details.get(i).getTrade_count().doubleValue());;
			detailVo.setTradeTime(formatDate.format(details.get(i).getTrade_time() * 1000));
			list.add(detailVo);
		}
		
        ServletOutputStream outputStream = null;
		List<CellMap> cellMapList = new ArrayList<CellMap>();
		cellMapList.add(new CellMap("序号", "id"));
		cellMapList.add(new CellMap("合约号", "code"));
		cellMapList.add(new CellMap("买多方", "buyer"));
		cellMapList.add(new CellMap("卖空方", "seller"));
		cellMapList.add(new CellMap("成交价", "tradePrice"));
		cellMapList.add(new CellMap("成交数量", "tradeCount"));
		cellMapList.add(new CellMap("成交时间", "tradeTime"));
		
		int rowAccessWindowSize = 100;
		if (list.size() > 1000 && list.size() < 10000) {
			rowAccessWindowSize = 500;
		} else if (list.size() > 10000) {
			rowAccessWindowSize = 1000;
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) + ".xlsx");
			outputStream = response.getOutputStream();
			ExcelExportUtil.exportSXSSFExcel("交易信息", cellMapList, list, rowAccessWindowSize, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
        
    }
	
	private String getTradeTypeStr (int type) {
		String tradeType = "";
		switch (type) {
		case 1:
			tradeType = "多单开仓";
			break;
		case 2:
			tradeType = "多单平仓";
			break;
		case 3:
			tradeType = "空头开仓";
			break;
		case 4:
			tradeType = "空头平仓";
			break;
		case 5:
			tradeType = "多单爆仓";
			break;
		case 6:
			tradeType = "空单爆仓";
			break;

		default:
			break;
		}
		
		return tradeType;
	}

}
