package com.btc100.btc_q.action.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.entity.BQWorkOrder;
import com.btc100.btc_q.entity.BQWorkOrderDetail;
import com.btc100.btc_q.service.BQWorkOrderDetailService;
import com.btc100.btc_q.service.BQWorkOrderService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping("/managerWorkOrder")
public class ManagerWorkOrderAction extends BaseAction {
	
	private static final Logger logger = Logger.getLogger(ManagerWorkOrderAction.class);
	@Autowired
	private BQWorkOrderDetailService bqWorkOrderDetailService;
	
	@Autowired
	private BQWorkOrderService bqWorkOrderService;
	
	/**
	 * 工单列表
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/loadWorkOrder")
	public String order(Model model) {

		PagerModel pm = bqWorkOrderService.findOrdersByState("UNREAD");
		model.addAttribute("pm", pm);
		return super.url_JSP("workOrderlist");
	}
	
	@RequestMapping("/searchWorkOrder")
	public String searchWorkOrder(String uid, String state, String type, Model model) {
		long id = ConvertUtil.toLong(uid, 0l); // 用户ID

		if (id > 0) {
			PagerModel pm = bqWorkOrderService.readOrderByUserId(id, "manager");
			model.addAttribute("pm", pm);
			model.addAttribute("id", id);
		} else if (!StringUtil.isEmpty(state)) {
			PagerModel pm = bqWorkOrderService.findOrdersByState(state);
			model.addAttribute("pm", pm);
			model.addAttribute("state", state);
		} else if (!StringUtil.isEmpty(type)) {
			PagerModel pm = bqWorkOrderService.findOrdersByType(type);
			model.addAttribute("pm", pm);
			model.addAttribute("type", type);
		} else {
			PagerModel pm = bqWorkOrderService.findOrdersByState("UNREAD");
			model.addAttribute("pm", pm);
			model.addAttribute("state", "UNREAD");
		}
		return super.url_JSP("workOrderlist");
	}
	
	/**
	 * 查看工单详情
	 */
	@RequestMapping("/workOrderDetail")
	public String orderDetail(Model model, String id) {
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(id, 0L));
		//更新工单状态为已读
		if ("UNREAD".equals(order.getF_to_order_state())) {
			order.setF_from_order_state("READ");
			order.setF_to_order_state("READ");
		}
		
		bqWorkOrderService.updateWorkOrder(order);
		model.addAttribute("order", order);
		
		return super.url_JSP("workOrderDetail");
	}
	
	/**
	 * 提交工单回复 
	 */
	@RequestMapping("/orderReply")
	public void orderReply(HttpServletRequest request, HttpServletResponse response, String content, String orderId, String file, Model model) {
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		
		BQSurecome manager = findCurrManager(request);
		if (manager == null) {
			checkVO.setCode("error");
			checkVO.setMsg("请重新登录");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(orderId, 0L));
		BQWorkOrderDetail detail = new BQWorkOrderDetail();
		detail.setF_order_id(order.getId());
		detail.setF_order_code(order.getF_order_code());
		detail.setF_direction("SYSTEM");
		detail.setF_user_name(manager.getUser_name());
		detail.setF_reply_content(content);
		detail.setF_file(file);
		detail.setF_create_time(bqWorkOrderDetailService.readTime());
		
		//更新工单状态
		order.setF_to_order_state("SEND"); //接收方已发送
		order.setF_from_order_state("UNREAD"); //发起方未读
		order.setF_update_time(bqWorkOrderService.readTime());
		
		checkVO.setCode("success");
		checkVO.setMsg("提交成功");
		if(StringUtil.isEmpty(content) && StringUtil.isEmpty(file)){
			checkVO.setCode("error");
			checkVO.setMsg("内容不能为空");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
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
		
		List<BQWorkOrderDetail> details = bqWorkOrderDetailService.readListByOrderId(ConvertUtil.toLong(orderId, 0L));
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(orderId, 0L));
		model.addAttribute("details", details);
		model.addAttribute("order", order);
		//更新工单状态为已读
		if ("UNREAD".equals(order.getF_to_order_state())) {
			order.setF_from_order_state("READ");
			order.setF_to_order_state("READ");
			bqWorkOrderService.updateWorkOrder(order);
		}
		
		return super.url_JSP("workOrderReply");
	}
	
	/**
	 * 关闭工单
	 */
	@RequestMapping("/closeOrder")
	public String closeOrder(HttpServletResponse response, Model model, String id) {
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(id, 0L));
		
		order.setF_from_order_state("CLOSE");
		order.setF_to_order_state("CLOSE");
		bqWorkOrderService.updateWorkOrder(order);

		return super.redirect("/managerWorkOrder/loadWorkOrder.do");
	}
	
	@RequestMapping("/downLoad")
	public void downLoad(HttpServletRequest request, HttpServletResponse response, String id) throws Exception{
		
		BQWorkOrder order = bqWorkOrderService.readById(ConvertUtil.toLong(id, 0L));
		
		String storePath = Contents.BASE_UPLOAD_PATH + order.getF_file();
		File file = new File(storePath);
		if (!file.exists()) {
			logger.info("资源文件不存在：" + order.getF_file());
			toWeb(response, "对不起，找不到资源文件！");
			return;
		}
		
		OutputStream out = null;
		InputStream is = null;
				
        try {
        	String fileName =  System.currentTimeMillis() + order.getF_file().substring(order.getF_file().lastIndexOf("."));
    		
        	fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
    		response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);// 设定输出文件头   
            response.setContentType("text/x-plain");// 定义输出类型 
            response.setCharacterEncoding("UTF-8");
            
            out = response.getOutputStream();
            is = new FileInputStream(file);
            
            byte[] cache = new byte[4 * 1024];
            for (int offset = is.read(cache); offset != -1; offset = is.read(cache)) {
            	out.write(cache, 0, offset);
            }
            
            out.flush();
            
        } catch (Exception e) {
            logger.error("下载资源文件异常，信息：", e);
            response.reset();
            toWeb(response, "对不起，程序异常！");
        } finally {
        	if (null != out) {
        		try {
        			out.close();
        		} catch (Exception e) {
        			logger.error("下载资源文件关闭输出流异常，信息：", e);
        		}
        	}
        	if (null != is) {
        		try {
        			is.close();
        		} catch (Exception e) {
        			logger.error("下载资源文件关闭输出流异常，信息：", e);
        		}
        	}
        }
		
		return;
	}
	
}
