package com.btc100.btc_q.action.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerUser")
public class UsersAction extends BaseAction {

	@Autowired
	private VIEWUsermanagerService viewUsermanagerService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	BQCommonFailedloginService bqCommonFailedloginService;
	@Autowired
	private BQMemberModifyInformationRecordService bqMemberModifyInformationRecordService;

	@RequestMapping(value = "/loadUsers")
	public String loadUsers(HttpServletRequest request, Model mo) {
		String state = request.getParameter("state");
		String userid = request.getParameter("userid");
		String usertel = request.getParameter("usertel");
		String useremail = request.getParameter("useremail");
		String countryName = request.getParameter("countryName");
		String inviteId = request.getParameter("inviteId");
		String userLevelId = request.getParameter("userLevelId");
		String ss0 = request.getParameter("pager.offset");

		int istate = ConvertUtil.toInt(state, 0);
		long uid = ConvertUtil.toLong(userid, 0l);
		long inviteuId = ConvertUtil.toLong(inviteId, 0l);
		int iuserLevelId = ConvertUtil.toInt(userLevelId, -1);

		countryName = countryName == null ? "" : countryName;
		if (ss0 != null) {
			// 处理汉字
			try {
				countryName = URLEncoder.encode(countryName, "ISO-8859-1");
				countryName = URLDecoder.decode(countryName, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			try {
				countryName = new String(countryName.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		PagerModel pm = new PagerModel();

		if (istate == 1) {
			pm = viewUsermanagerService.findModel(uid, usertel, useremail, countryName, inviteuId, iuserLevelId, 1);
		} else if (istate == 2) {
			pm = viewUsermanagerService.findModel(uid, usertel, useremail, countryName, inviteuId, iuserLevelId, 2);
		} else if (istate == 3) {
			pm = viewUsermanagerService.findModel(uid, usertel, useremail, countryName, inviteuId, iuserLevelId, 3);
		} else {
			pm = viewUsermanagerService.findModel();
		}

		mo.addAttribute("pm", pm);
		mo.addAttribute("userid", userid);
		mo.addAttribute("usertel", usertel);
		mo.addAttribute("useremail", useremail);
		mo.addAttribute("countryName", countryName);
		mo.addAttribute("inviteId", inviteId);
		mo.addAttribute("userLevelId", userLevelId);
		mo.addAttribute("state", state);
		return super.url_JSP("userslist");
	}

	@RequestMapping(value = "/selectUsers")
	public String selectUsers(HttpServletRequest request, Model mo) {
		String state = request.getParameter("state");
		String userid = request.getParameter("userid");
		String usertel = request.getParameter("usertel");
		String useremail = request.getParameter("useremail");
		String countryName = request.getParameter("countryName");
		String inviteId = request.getParameter("inviteId");
		String userLevelId = request.getParameter("userLevelId");
		String ss0 = request.getParameter("pager.offset");

		int istate = ConvertUtil.toInt(state, 0);
		long uid = ConvertUtil.toLong(userid, 0l);
		long inviteuId = ConvertUtil.toLong(inviteId, 0l);
		int iuserLevelId = ConvertUtil.toInt(userLevelId, -1);

		countryName = countryName == null ? "" : countryName;

		if (ss0 != null) {
			// 处理汉字
			try {
				countryName = URLEncoder.encode(countryName, "ISO-8859-1");
				countryName = URLDecoder.decode(countryName, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		PagerModel pm = viewUsermanagerService.findModel(uid, usertel, useremail, countryName, inviteuId, iuserLevelId, istate);
		mo.addAttribute("pm", pm);
		mo.addAttribute("userid", userid);
		mo.addAttribute("usertel", usertel);
		mo.addAttribute("useremail", useremail);
		mo.addAttribute("countryName", countryName);
		mo.addAttribute("inviteId", inviteId);
		mo.addAttribute("userLevelId", userLevelId);
		mo.addAttribute("state", state);

		return super.url_JSP("userslist");
	}

	@RequestMapping(value = "/freezeUser")
	public String freezeUser(Integer userid, Integer istate, Model mo) {
		bqMemberService.updatefreeze(userid, istate);
		return super.redirect("/managerUser/selectUsers.do?userid=" + userid);
	}

	@RequestMapping(value = "/managerUser")
	public String managerUser(HttpServletRequest request, long userid, Model mo) {
		VIEWUsermanager user = viewUsermanagerService.findUser(userid);
		BQMemberSafeSettings safeSettings = bqMemberSafeSettingsService.getById(userid);
		mo.addAttribute("id", user.getF_user_id());
		mo.addAttribute("email", user.getF_user_email());
		mo.addAttribute("tel", safeSettings.getF_user_tel());
		mo.addAttribute("area", user.getCountry_name());
		int msg = Convert.toInt(request.getParameter("msg"), -1);
		if (msg == 0) {
			mo.addAttribute("msg", "密码错误");
		} else if (msg == 1) {
			mo.addAttribute("msg", "修改成功");
		} else if (msg == 2) {
			mo.addAttribute("msg", "手机或邮箱重复");
		} else if (msg == 3) {
			mo.addAttribute("msg", "手机和邮箱有空");
		}

		return super.url_JSP("userdetails");
	}

	@RequestMapping(value = "/userChange")
	public String userChange(Integer id, String email, String tel, String paypwd, HttpServletRequest request, Model mo) throws Exception {
		mo.addAttribute("msg", 0);
		if (super.checkYZPwd(request, paypwd)) {
			String clearlogin = request.getParameter("clearlogin");
			if (!StringUtil.isEmpty(clearlogin)) {
				bqCommonFailedloginService.clearLoginTime(clearlogin);
			}

			BQMember user = bqMemberService.readUserById(id);
			BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(id);
			String oldemail = user.getF_real_email();
			String oldTel = bqms.getF_user_tel();
			if (tel == null) tel = "";
			if (email == null) email = "";
			
			if (StringUtil.isEmpty(email) && StringUtil.isEmpty(tel)) {
				mo.addAttribute("msg", 3);
				mo.addAttribute("userid", id);
				return super.redirect("/managerUser/managerUser.do");
			}
			
			if (!StringUtil.isEmpty(email) && !email.equals(user.getF_real_email())) {
				BQMember member = bqMemberService.readByRealEmail(email);
				if (member != null) {
					mo.addAttribute("msg", 2);
					mo.addAttribute("userid", id);
					return super.redirect("/managerUser/managerUser.do");
				}
			}
			
			if (!StringUtil.isEmpty(tel) && !tel.equals(bqms.getF_user_tel())) {
				BQMemberSafeSettings settings = bqMemberSafeSettingsService.readByTel(tel);
				if (settings != null) {
					mo.addAttribute("msg", 2);
					mo.addAttribute("userid", id);
					return super.redirect("/managerUser/managerUser.do");
				}
			}
			
			boolean isEmailUser = user.getF_user_email().equals(user.getF_real_email());
			user.setF_real_email(email);
			user.setF_user_email_status(StringUtil.isEmpty(email) ? 0 : 1);
			if (isEmailUser) {
				user.setF_user_email(StringUtil.isEmpty(email) ? tel : email);
			} else {
				user.setF_user_email(StringUtil.isEmpty(tel) ? email : tel);
			}
			
			bqms.setF_user_tel(tel);
			int safeType = (StringUtil.isEmpty(bqms.getF_user_tel()) ? 0 : 1) + (StringUtil.isEmpty(bqms.getF_user_safegoogle()) ? 0 : 2);
			bqms.setF_user_safetype(safeType);
			
			bqMemberSafeSettingsService.updateTel(bqms);
			bqMemberService.update(user);

			BQSurecome manager = (BQSurecome) request.getSession().getAttribute("owner");
			if (!oldemail.equals(email)) {
				BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
				informationRecord.setF_user_id(user.getF_user_id());
				informationRecord.setF_old_information(oldemail);
				informationRecord.setF_new_information(email);
				informationRecord.setF_information_type(2);
				informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
				informationRecord.setF_modify_ip(manager.getUser_lastip());
				informationRecord.setF_modify_server(1);
				informationRecord.setF_modify_user(manager.getUser_name());
				informationRecord.setF_operation_type(3);
				bqMemberModifyInformationRecordService.save(informationRecord);
			}
			if (!oldTel.equals(tel)) {
				BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
				informationRecord.setF_user_id(bqms.getF_user_id());
				informationRecord.setF_old_information(oldTel);
				informationRecord.setF_new_information(tel);
				informationRecord.setF_information_type(1);
				informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
				informationRecord.setF_modify_ip(manager.getUser_lastip());
				informationRecord.setF_modify_server(1);
				informationRecord.setF_modify_user(manager.getUser_name());
				informationRecord.setF_operation_type(3);
				bqMemberModifyInformationRecordService.save(informationRecord);
			}

			mo.addAttribute("msg", 1);
		}
		mo.addAttribute("userid", id);

		return super.redirect("/managerUser/managerUser.do");
	}

	private BQMember selectUser(String userId, String userEmail) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.readUserById(uid);
		if (user == null) {
			user = bqMemberService.readUserByEmail(userEmail);
		}
		return user;
	}

	@RequestMapping("/loadGoogle")
	public String loadGoogle(HttpServletRequest request, Model model) {
		String userId = request.getParameter("userId");
		String userEmail = request.getParameter("email");

		BQMember user = this.selectUser(userId, userEmail);
		if (user == null) {
			model.addAttribute("pm", bqMemberSafeSettingsService.readUserSafe(0));
		} else {
			model.addAttribute("pm", bqMemberSafeSettingsService.readUserSafe(user.getF_user_id()));
			model.addAttribute("userId", user.getF_user_id());
			model.addAttribute("email", user.getF_user_email());
		}
		model.addAttribute("pageUrl", "loadGoogle");
		return super.url_JSP("userGoogleManage");
	}

	@RequestMapping("/removeGoogle")
	public String removeGoogle(HttpServletRequest request, Model model) throws Exception {
		String userId = request.getParameter("userId");
		String userEmail = request.getParameter("email");
		String surepwd = request.getParameter("surepwd");

		if (!super.checkYZPwd(request, surepwd)) {
			return super.errorPage(model, "安全密码不正确");
		}

		BQMember user = this.selectUser(userId, userEmail);
		if (user == null) {
			model.addAttribute("pm", bqMemberSafeSettingsService.readUserSafe(0));
		} else {
			BQMemberSafeSettings safeSettings = bqMemberSafeSettingsService.readById(user.getF_user_id());
			String safegoogle = safeSettings.getF_user_safegoogle();
			if (safeSettings == null || "".equals(safeSettings.getF_user_safegoogle())
					|| (safeSettings.getF_user_safetype() != 2 && safeSettings.getF_user_safetype() != 3)) {
				model.addAttribute("pm", new PagerModel());
				model.addAttribute("errormsg", "该用户不存在或者没有绑定谷歌");
			} else {
				bqMemberSafeSettingsService.removeGoogle(user.getF_user_id());
				model.addAttribute("errormsg", "解除谷歌绑定成功");

				BQSurecome manager = (BQSurecome) request.getSession().getAttribute("owner");
				BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
				informationRecord.setF_user_id(user.getF_user_id());
				informationRecord.setF_old_information(safegoogle);
				informationRecord.setF_new_information("");
				informationRecord.setF_information_type(3);
				informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
				informationRecord.setF_modify_ip(manager.getUser_lastip());
				informationRecord.setF_modify_server(1);
				informationRecord.setF_modify_user(manager.getUser_name());
				informationRecord.setF_operation_type(3);
				bqMemberModifyInformationRecordService.save(informationRecord);
			}
			model.addAttribute("pm", bqMemberSafeSettingsService.readUserSafe(user.getF_user_id()));
			model.addAttribute("userId", user.getF_user_id());
			model.addAttribute("email", user.getF_user_email());
		}
		model.addAttribute("pageUrl", "loadGoogle");
		return super.url_JSP("userGoogleManage");
	}

	@RequestMapping("/loadModifyRecord")
	public String loadModifyRecord(HttpServletRequest request, Model model){
		long uId = ConvertUtil.toLong(request.getParameter("userId"), 0l);
		int type = ConvertUtil.toInt(request.getParameter("type"), 0 );
		String modifyUser = request.getParameter("modifyUser");
		String name = request.getParameter("name");
		int opertionType = ConvertUtil.toInt(request.getParameter("opertionType"), 0);
		PagerModel pm = bqMemberModifyInformationRecordService.readPagerModel(uId, type, modifyUser, name, opertionType);
		if (uId > 0) {
			model.addAttribute("uid", uId);
		}
		if (opertionType > 0) {
			model.addAttribute("opertionType", opertionType);
		}
		model.addAttribute("type", type);
		model.addAttribute("modifyUser", StringUtil.isEmpty(modifyUser) ? "" : modifyUser);
		model.addAttribute("name", StringUtil.isEmpty(name) ? "" : name);
		model.addAttribute("pm", pm);

		return super.url_JSP("safetyVerificationModifyRecord");

	}

}
