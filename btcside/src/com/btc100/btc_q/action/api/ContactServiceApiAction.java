package com.btc100.btc_q.action.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQWorkOrder;
import com.btc100.btc_q.entity.BQWorkOrderDetail;
import com.btc100.btc_q.service.BQWorkOrderDetailService;
import com.btc100.btc_q.service.BQWorkOrderService;
import com.btc100.btc_q.util.StringUtil;
import com.google.gson.Gson;

/**
 * 工单API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/contactServiceApi")
public class ContactServiceApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(ContactServiceApiAction.class);
	
	@Autowired
	private BQWorkOrderDetailService bqWorkOrderDetailService;
	@Autowired
	private BQWorkOrderService bqWorkOrderService;
	
	/**
	 * 用户工单列表
	 * 
	 */
	@RequestMapping("/list")
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		
		JSONArray array = new JSONArray();
		List<BQWorkOrder> list = bqWorkOrderService.readByUserAndSize(user.getF_user_id(), nFrom, direct, start, end, size);
		for (BQWorkOrder workOrder : list) {
			Map<String,Object> orderMap = new HashMap<String, Object>();
			orderMap.put("id", workOrder.getId()); // 工单id
			orderMap.put("orderCode", workOrder.getF_order_code()); // 工单号
			orderMap.put("dateline", workOrder.getF_create_time()); // 时间
			orderMap.put("orderState", workOrder.getF_from_order_state()); // 工单状态
			orderMap.put("ordertype", workOrder.getF_order_type()); // 工单类型
			orderMap.put("theme", workOrder.getF_theme()); // 工单主题
			orderMap.put("content", workOrder.getF_reply_content()); // 工单内容
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(orderMap)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 单条工单
	 * 
	 */
	@RequestMapping("/detail")
	public String detail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String id = request.getParameter("orderId");
		long orderId = Convert.toLong(id, -1);
		if (orderId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数orderId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQWorkOrder workOrder = bqWorkOrderService.readById(orderId);
		Map<String,Object> orderMap = new HashMap<String, Object>();
		orderMap.put("id", workOrder.getId()); // 工单id
		orderMap.put("orderCode", workOrder.getF_order_code()); // 工单号
		orderMap.put("dateline", workOrder.getF_create_time()); // 时间
		orderMap.put("orderState", workOrder.getF_from_order_state()); // 工单状态
		orderMap.put("ordertype", workOrder.getF_order_type()); // 工单类型
		orderMap.put("theme", workOrder.getF_theme()); // 工单主题
		orderMap.put("content", workOrder.getF_reply_content()); // 工单内容
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(orderMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 查看工单回复详情
	 * 
	 */
	@RequestMapping("/replyDetail")
	public String replyDetail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String id = request.getParameter("orderId");
		long orderId = Convert.toLong(id, -1);
		if (orderId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数orderId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		List<BQWorkOrderDetail> details = bqWorkOrderDetailService.readListByOrderId(orderId);
		JSONArray array = new JSONArray();
		for (BQWorkOrderDetail orderDetail : details) {
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(orderDetail)));
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 添加工单
	 * 
	 */
	@RequestMapping(value = "/addOrder", method = RequestMethod.POST)
	public String addOrder(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String orderType = request.getParameter("orderType");
		String theme = request.getParameter("theme");
		String content = request.getParameter("content");
		String file = request.getParameter("file");
		
		if(StringUtil.isEmpty(orderType) || StringUtil.isEmpty(content)){
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "工单内容或主题错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		List<String> typeList = new ArrayList<String>();
		typeList.add("ACCOUNT");
		typeList.add("TRADE");
		typeList.add("RW");
		typeList.add("COMPLAIN");
		typeList.add("COOP");
		if(!typeList.contains(orderType)){
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "工单类型错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.validateString(orderType) || !StringUtil.validateString(theme)
				|| !StringUtil.validateString(content)
				|| !StringUtil.validateString(file)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提交内容包含非法参数");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQWorkOrder order = new BQWorkOrder();
		order.setF_file(file);
		order.setF_order_type(orderType);
		order.setF_reply_content(content);
		order.setF_theme(theme);
		bqWorkOrderService.saveWorkOrder(order, user);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 提交工单回复
	 * 
	 */
	@RequestMapping(value = "/orderReply", method = RequestMethod.POST)
	public String orderReply(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String content = request.getParameter("content");
		String file = request.getParameter("file");
		String id = request.getParameter("orderId");
		long orderId = Convert.toLong(id, -1);
		if (orderId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数orderId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQWorkOrder order = bqWorkOrderService.readById(orderId);
		if("CLOSE".equals(order.getF_to_order_state()) || "CLOSE".equals(order.getF_from_order_state())){
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "工单已关闭");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.validateString(content) || !StringUtil.validateString(file)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提交内容包含非法字符");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if(StringUtil.isEmpty(content) && StringUtil.isEmpty(file)){
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "内容或图片错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQWorkOrderDetail detail = new BQWorkOrderDetail();
		detail.setF_order_id(order.getId());
		detail.setF_order_code(order.getF_order_code());
		detail.setF_direction("USER");
		detail.setF_reply_content(content);
		detail.setF_file(file);
		detail.setF_create_time(bqWorkOrderDetailService.readTime());
		
		//更新工单状态
		order.setF_from_order_state("SEND"); //发起方已发送
		order.setF_to_order_state("UNREAD"); //接收方未读
		order.setF_update_time(bqWorkOrderService.readTime());
		
		bqWorkOrderService.updateWorkOrder(order);
		bqWorkOrderDetailService.saveWorkOrderDetail(detail);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户新消息提醒
	 * 
	 */
	@RequestMapping("/getMessage")
	public String getMessage(HttpServletRequest request, HttpServletResponse response, Model model, BQWorkOrder order) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		List<BQWorkOrder> unreadOrders = bqWorkOrderService.readListByState("UNREAD", user.getF_user_id());
		userJson.put("data", unreadOrders.size());
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户未读工单列表
	 * 
	 */
	@RequestMapping("/getUnreadOrders")
	public String getUnreadOrders(HttpServletRequest request, HttpServletResponse response, Model model, BQWorkOrder order) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		List<BQWorkOrder> unreadOrders = bqWorkOrderService.readListByState("UNREAD", user.getF_user_id());
		
		JSONArray array = new JSONArray(); 
		if (null != unreadOrders && unreadOrders.size() > 0) {
			try {
				for (BQWorkOrder workOrder : unreadOrders) {
					Map<String,Object> orderMap = new HashMap<String, Object>();
					orderMap.put("id", workOrder.getId()); // 工单id
					orderMap.put("orderCode", workOrder.getF_order_code()); // 工单号
					orderMap.put("dateline", workOrder.getF_create_time()); // 时间
					orderMap.put("orderState", workOrder.getF_from_order_state()); // 工单状态
					orderMap.put("ordertype", workOrder.getF_order_type()); // 工单类型
					orderMap.put("theme", workOrder.getF_theme()); // 工单主题
					orderMap.put("content", workOrder.getF_reply_content()); // 工单内容
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(orderMap)));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
}
