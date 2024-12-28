package com.btc100.btc_q.action.client;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQWorkOrder;
import com.btc100.btc_q.entity.BQWorkOrderDetail;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQWorkOrderDetailService;
import com.btc100.btc_q.service.BQWorkOrderService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping("/order")
public class ContactServiceAction extends BaseAction {
	
	@Autowired
	private BQWorkOrderDetailService bqWorkOrderDetailService;
	
	@Autowired
	private BQWorkOrderService bqWorkOrderService;
	
	/**
	 * 工单服务
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/orderList")
	public String order(HttpServletRequest request, Model model) {

		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return super.toLogin();
		}
		long userId = user.getF_user_id();
		PagerModel pm = bqWorkOrderService.readOrderByUserId(userId, null);
		model.addAttribute("pm", pm);
		return "jsp/user_order_list";
	}
	
	/**
	 * 添加工单
	 */
	@RequestMapping("/addOrder")
	public String addOrder(HttpServletRequest request, Model model) {
		
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return super.toLogin();
		}
		return "jsp/user_order_add";
	}
	
	@RequestMapping("/doAddOrder")
	public String doaddOrder(HttpServletRequest request, BQWorkOrder order, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			if (user == null) {
				return super.toLogin();
			}
		}
		
		if (!StringUtil.validateString(order.getF_order_type()) || !StringUtil.validateString(order.getF_theme())
				|| !StringUtil.validateString(order.getF_reply_content())
				|| !StringUtil.validateString(order.getF_from_order_state())
				|| !StringUtil.validateString(order.getF_to_order_state())
				|| !StringUtil.validateString(order.getF_user_email())
				|| !StringUtil.validateString(order.getF_file())) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.illegalCharacters", Contents.Local.LOCALE.get()));
			return "jsp/user_order_add";
		}
		
		String basePath = super.getRequestRoot(request);
		List<String> typeList = new ArrayList<String>();
		typeList.add("ACCOUNT");
		typeList.add("TRADE");
		typeList.add("RW");
		typeList.add("COMPLAIN");
		typeList.add("COOP");
		
		if(!typeList.contains(order.getF_order_type())){
			model.addAttribute("error", SpringContext.getText("msg.workOrder.request.typeError", Contents.Local.LOCALE.get()));
			return "jsp/user_order_add";
		}
		if(StringUtil.isEmpty(order.getF_theme()) || StringUtil.isEmpty(order.getF_reply_content())){
			model.addAttribute("error", SpringContext.getText("msg.workOrder.request.themeOrContentNull", Contents.Local.LOCALE.get()));
			return "jsp/user_order_add";
		}
		
		bqWorkOrderService.saveWorkOrder(order, user);
		
		return "redirect:" + basePath + "order/orderList.do";
	}
	
	/**
	 * 查看工单详情
	 */
	@RequestMapping("/orderDetail")
	public String orderDetail(HttpServletRequest request, Model model, String id) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return super.toLogin();
		}
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(id, 0L));
		//更新工单状态为已读
		if ("UNREAD".equals(order.getF_from_order_state())) {
			order.setF_from_order_state("READ");
			order.setF_to_order_state("READ");
		}
		
		bqWorkOrderService.updateWorkOrder(order);
		model.addAttribute("order", order);
		
		return "jsp/user_order_detail";
	}
	
	/**
	 * 提交工单回复 
	 */
	@RequestMapping("/orderReply")
	public void orderReply(HttpServletRequest request, HttpServletResponse response,String content, String file, String orderId, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return;
		}
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.workOrder.request.replySuccess", Contents.Local.LOCALE.get()));
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(orderId, 0L));
		BQWorkOrderDetail detail = new BQWorkOrderDetail();
		if("CLOSE".equals(order.getF_to_order_state()) || "CLOSE".equals(order.getF_from_order_state())){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.workOrder.request.orderIsClosed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!StringUtil.validateString(content) || !StringUtil.validateString(file)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalCharacters", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if(StringUtil.isEmpty(content) && StringUtil.isEmpty(file)){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.workOrder.request.replyContentNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
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
		
		if("success".equalsIgnoreCase(checkVO.getCode())){
			bqWorkOrderService.updateWorkOrder(order);
			bqWorkOrderDetailService.saveWorkOrderDetail(detail);
			toWeb(response, gson.toJson(checkVO));
		}
	}
	
	/**
	 * 加载工单回复信息
	 */
	@RequestMapping("/loadReply")
	public String loadReply(HttpServletRequest request, Model model, String orderId) throws Exception{
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return super.toLogin();
		}
		
		List<BQWorkOrderDetail> details = bqWorkOrderDetailService.readListByOrderId(ConvertUtil.toLong(orderId, 0L));
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(orderId, 0L));
		model.addAttribute("details", details);
		model.addAttribute("order", order);
		//更新工单状态为已读
		if ("UNREAD".equals(order.getF_from_order_state())) {
			order.setF_from_order_state("READ");
			order.setF_to_order_state("READ");
			bqWorkOrderService.updateWorkOrder(order);
		}
		
		return "jsp/user_order_reply";
	}
	
	/**
	 * 用户新消息提醒
	 */
	@RequestMapping("/getMessage")
	public void getMessage(HttpServletRequest request, HttpServletResponse response, Model model) {
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return;
		}
		JSONObject object = new JSONObject();
		
		List<BQWorkOrder> unreadOrders = bqWorkOrderService.readListByState("UNREAD", user.getF_user_id());
		
		if (null != unreadOrders && unreadOrders.size() > 0) {
			try {
				object.put("unReadCount", unreadOrders.size());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		toWeb(response, object);
	}
	
	@RequestMapping("/getUnreadOrders")
	public void getUnreadOrders(HttpServletRequest request, HttpServletResponse response, Model model) {
		BQMember user = super.findCurrUser(request, false);
		if(user==null){
			return;
		}
		
		List<BQWorkOrder> unreadOrders = bqWorkOrderService.readListByState("UNREAD", user.getF_user_id());
		Gson gson = new Gson();
		
		toWeb(response, gson.toJson(unreadOrders));
	}
	
}
