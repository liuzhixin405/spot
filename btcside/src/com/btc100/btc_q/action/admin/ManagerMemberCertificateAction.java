package com.btc100.btc_q.action.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQCertificateType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberCertificate;
import com.btc100.btc_q.service.BQCertificateTypeService;
import com.btc100.btc_q.service.BQMemberCertificateService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerMemberCertificate")
public class ManagerMemberCertificateAction extends BaseAction {
	
	@Autowired
	private BQCertificateTypeService bqCertificateTypeService;
	@Autowired
	private BQMemberCertificateService bqMemberCertificateService;
	@Autowired
	private BQMemberService bqMemberService;

	@RequestMapping("/load")
	public String load(String userId, String userEmail, Model model) {
		PagerModel pm = new PagerModel();
		if (StringUtil.isEmpty(userId) && StringUtil.isEmpty(userEmail)) {
			pm = bqMemberCertificateService.readPagerModel();
		} else {
			long uid = ConvertUtil.toLong(userId, 0l);
			BQMember user = bqMemberService.readUserById(uid);
			if (user == null) {
				user = bqMemberService.readUserByEmail(userEmail);
			}
			if (user == null) {
				return super.errorPage(model, "查不到该用户");
			}
			pm = bqMemberCertificateService.readPageByUserId(user.getF_user_id());
		}

		model.addAttribute("userId", userId);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("pm", pm);
		
		return super.url_JSP("memberCertificatelist");
	}
	
	@RequestMapping("/get")
	public String get(int id, Model model) {
		BQMemberCertificate memberCertificate = bqMemberCertificateService.readById(id);
		if (memberCertificate == null) {
			memberCertificate = new BQMemberCertificate();
		}
		List<BQCertificateType> types = bqCertificateTypeService.readAll();
		model.addAttribute("memberCertificate", memberCertificate);
		model.addAttribute("types", types);
		
		return super.url_JSP("memberCertificateedit");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BQMemberCertificate certificate, Model model) {
		BQMember user = bqMemberService.readUserById(certificate.getUser_id());
		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		if (StringUtil.isEmpty(certificate.getActivity_name()) || StringUtil.isEmpty(certificate.getCertificate_id())) {
			return super.errorPage(model, "参数错误");
		}
		
		BQCertificateType type = bqCertificateTypeService.readById(certificate.getCertificate_id());
		certificate.setCertificate_name(type.getCertificate_name());
		if (StringUtil.isEmpty(certificate.getId()) || certificate.getId() <= 0) {
			certificate.setIssue_time(System.currentTimeMillis() / 1000);
			bqMemberCertificateService.save(certificate);
		} else {
			bqMemberCertificateService.update(certificate);
		}
		
		return super.redirect("/managerMemberCertificate/load.do");
	}
	
	@RequestMapping("/delete")
	public String deleteLevel(String id) {
		long cerId = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cerId);
		bqMemberCertificateService.delete(certificate);
		return super.redirect("/managerMemberCertificate/load.do");
	}

}
