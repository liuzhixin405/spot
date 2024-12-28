package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQCompositeIndexDetail;
import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQBenchmarkPriceService;
import com.btc100.btc_q.service.BQCompositeIndexDetailService;
import com.btc100.btc_q.service.BQCompositeIndexTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerCompositeIndex")
public class CompositeIndexManagerAction extends BaseAction {

	@Autowired
	private BQCompositeIndexTypeService bqCompositeIndexTypeService;
	@Autowired
	private BQCompositeIndexDetailService bqCompositeIndexService;
	@Autowired
	private BQBenchmarkPriceService bqBenchmarkPriceService;

	@RequestMapping(value = "/loadCompositeType")
	public String loadCompositeType(HttpServletRequest request, Model mo) {
		PagerModel pm = bqCompositeIndexTypeService.findAll();
		mo.addAttribute("pm", pm);

		return super.url_JSP("compositeIndexType");
	}

	@RequestMapping(value = "/compositeIndexTypeEdit")
	public String compositeIndexTypeEdit(HttpServletRequest request, Model mo) {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		BQCompositeIndexType type = bqCompositeIndexTypeService.getById(typeId);
		if (type == null) {
			return super.errorPage(mo, "typeId错误");
		}
		mo.addAttribute("type", type);
		return super.url_JSP("compositeIndexTypeEdit");
	}

	@RequestMapping("/updateCompositeType")
	public String updateManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		String typeCode = request.getParameter("typeCode");
		String typeName = request.getParameter("typeName");
		String benchmarkPrice = request.getParameter("benchmarkPrice");
		Boolean isUsing = (Convert.toInt(request.getParameter("isUsing"), 0) == 1);
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (typeCode == null || typeCode.isEmpty()) {
			return super.errorPage(mo, "缺少合约类型号");
		}

		if (typeName == null || typeName.isEmpty()) {
			return super.errorPage(mo, "缺少类型名称");
		}

		if (benchmarkPrice == null || benchmarkPrice.isEmpty()) {
			return super.errorPage(mo, "缺少基准开盘指数");
		}

		BigDecimal price = new BigDecimal(benchmarkPrice);
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			return super.errorPage(mo, "基准开盘指数不能小于等于0");
		}

		if (typeId <= 0 && bqCompositeIndexTypeService.getByCode(typeCode) != null) {
			return super.errorPage(mo, "已有合约类型号");
		}

		BQCompositeIndexType type = new BQCompositeIndexType();
		if (typeId > 0) {
			type.setType_id(typeId);
		}

		type.setTransaction_type_code(typeCode.toLowerCase());
		type.setType_name(typeName);
		type.setBenchmark_index(price);
		type.setIs_using(isUsing);

		bqCompositeIndexTypeService.update(type);

		return redirect("/managerCompositeIndex/loadCompositeType.do");
	}

	@RequestMapping(value = "/loadCompositeIndex")
	public String loadCompositeIndex(Model mo, String type) {
		int itype = ConvertUtil.toInt(type, 1);
		List<BQCompositeIndexDetail> list = bqCompositeIndexService.findByType(itype);
		BigDecimal totalRate = BigDecimal.ZERO;
		for (BQCompositeIndexDetail index : list) {
			totalRate = totalRate.add(index.getRate());
		}

		List<BQCompositeIndexType> types = bqCompositeIndexTypeService.findAllUsing();
		if (types != null && types.size() > 0) {
			mo.addAttribute("types", types);
		}
		mo.addAttribute("totalRate", totalRate);
		mo.addAttribute("list", list);
		mo.addAttribute("type", itype);
		return super.url_JSP("compositeIndex");
	}

	@RequestMapping(value = "/loadCompositeIndexEdit")
	public String loadCompositeIndexEdit(Model mo, String indexId, String indexType) {
		int id = ConvertUtil.toInt(indexId, 0);
		int itype = ConvertUtil.toInt(indexType, 1);

		BQCompositeIndexDetail index = bqCompositeIndexService.getById(id);
		if (index == null) {
			index = new BQCompositeIndexDetail();
			index.setIndex_type(itype);
		} else {
			itype = index.getIndex_type();
		}

		List<BQTransaction> allTrans = super.findCurrTrans();

		List<BQCompositeIndexType> types = bqCompositeIndexTypeService.findAllUsing();
		if (types != null && types.size() > 0) {
			mo.addAttribute("types", types);
		}
		mo.addAttribute("index", index);
		mo.addAttribute("transList", allTrans);
		return super.url_JSP("compositeIndexEdit");
	}

	@RequestMapping(value = "/addCompositeIndex", method = RequestMethod.POST)
	public String addCompositeIndex(HttpServletRequest request, Model mo) throws Exception {
		long id = ConvertUtil.toInt(request.getParameter("id"), 0);
		int indexType = ConvertUtil.toInt(request.getParameter("index_type"), 0);
		String code = request.getParameter("transaction_code");
		BigDecimal rate = new BigDecimal(request.getParameter("rate"));
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (indexType <= 0) {
			return super.errorPage(mo, "综合指数类型错误");
		}

		if (StringUtil.isEmpty(code)) {
			return super.errorPage(mo, "合约号错误");
		}

		if (rate.compareTo(BigDecimal.ZERO) <= 0) {
			return super.errorPage(mo, "权重不对,不能小于0");
		}

		BQCompositeIndexDetail index = new BQCompositeIndexDetail();
		BigDecimal totalRate = bqCompositeIndexService.getSumRate(indexType);
		if (id > 0) {
			index = bqCompositeIndexService.getById(id);
			if (index == null) {
				return super.errorPage(mo, "更新错误，id错误");
			}
			totalRate = totalRate.subtract(index.getRate());
		}
		totalRate = totalRate.add(rate);
		if (totalRate.compareTo(BigDecimal.ONE) > 0) {
			return super.errorPage(mo, "该综合指数类型总比重超过100%，请核对后添加");
		}

		if (id > 0) {
			if (!code.equals(index.getTransaction_code())) {
				BQCompositeIndexDetail compositeIndexDetail = bqCompositeIndexService.getByTypeAndCode(indexType, code);
				if (compositeIndexDetail != null) {
					return super.errorPage(mo, "已有该合约号的配比，请核对后修改");
				}
			}
		} else {
			BQCompositeIndexDetail compositeIndexDetail = bqCompositeIndexService.getByTypeAndCode(indexType, code);
			if (compositeIndexDetail != null) {
				return super.errorPage(mo, "已有该合约号的配比，请核对后添加");
			}
		}

		index.setIndex_type(indexType);
		index.setRate(rate);
		index.setTransaction_code(code);

		if (id > 0) {
			bqCompositeIndexService.update(index);
		} else {
			bqCompositeIndexService.save(index);
		}

		return redirect("/managerCompositeIndex/loadCompositeIndex.do?type=" + index.getIndex_type());
	}

	@RequestMapping("/deleteCompositeIndex")
	public String deleteCompositeIndex(Model mo, String id) {
		long indexId = ConvertUtil.toInt(id, 0);
		BQCompositeIndexDetail index = bqCompositeIndexService.getById(indexId);

		if (index == null)
			return super.errorPage(mo, "该记录不存在");

		bqCompositeIndexService.delete(index);
		return redirect("/managerCompositeIndex/loadCompositeIndex.do?type=" + index.getIndex_type());
	}

	@RequestMapping(value = "/loadBenchmarkPrice")
	public String loadBenchmarkPrice(HttpServletRequest request, Model mo) {
		String code = request.getParameter("code");
		PagerModel pm = bqBenchmarkPriceService.find(code);

		mo.addAttribute("code", code);
		mo.addAttribute("pm", pm);

		return super.url_JSP("benchmarkPrice");
	}

}
