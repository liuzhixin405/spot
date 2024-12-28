package com.btc100.btc_q.action.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberArea;
import com.btc100.btc_q.entity.BQSubMemberInfo;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQMemberAreaService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQSubMemberInfoService;
import com.btc100.btc_q.service.BQSubMemberService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.ExcelExportUtil;
import com.btc100.btc_q.util.ExcelExportUtil.CellMap;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.UserAppKeyVo;

@Controller
@RequestMapping(value = "/managerSubUserCreate")
public class SubUsersCreateAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQSubMemberService bqSubMemberService;
	@Autowired
	private RegisterSerivce registerService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQMemberAreaService bqMemberAreaService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private BQSubMemberInfoService bqSubMemberInfoService;

	private static double process = 0;

	@RequestMapping(value = "/load")
	public String loadUsers(HttpServletRequest request, Model model) {
		String userid = request.getParameter("userid");

		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(userid)) {
			long userId = ConvertUtil.toLong(userid, 0l);
			BQMember motherUser = bqMemberService.readUserById(userId);
			if (motherUser == null) {
				return super.errorPage(model, "查不到该用户");
			}
			pm = bqSubMemberService.readSubUserAndAppkey(motherUser.getF_user_id());
		}

		model.addAttribute("create", request.getParameter("create"));
		model.addAttribute("pm", pm);
		model.addAttribute("userid", userid);
		return super.url_JSP("subUserCreat");
	}

	@RequestMapping(value = "/batchCreateUsers", method = RequestMethod.POST)
	public String batchCreateUsers(HttpServletRequest request, Model model) {
		long userId = ConvertUtil.toLong(request.getParameter("userId"), 0l);
		final BQMember motherUser = bqMemberService.readUserById(userId);
		if (motherUser == null) {
			return super.errorPage(model, "查不到该用户");
		}
		final String emailModel = request.getParameter("email");
		if (StringUtil.isEmpty(emailModel)) {
			return super.errorPage(model, "请输入登录名模板");
		}
		final int count = ConvertUtil.toInt(request.getParameter("createCount"), 0);
		if (count <= 0) {
			return super.errorPage(model, "请输入正确的创建数量");
		}
		if (!StringUtil.validateString(emailModel)) {
			return super.errorPage(model, "登录名模板包含非法字符");
		}
		final String allowIp = request.getParameter("allowIp");
		if (!StringUtil.isEmpty(allowIp)) {
			// 验证IP格式
			String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
					+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
					+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
			if (!allowIp.matches(regex)) {
				return super.errorPage(model, "IP格式不正确");
			}
			// API最多只能绑定10个IP
			String[] ipArray = allowIp.split(",");
			if (ipArray.length > 10) {
				return super.errorPage(model, "API最多只能绑定10个IP");
			}
		}

		if (process == 0 || process >= 100) {
			process = 0.0001;
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					try {

						int index = 0;
						for (int i = 0; i < count; i++) {
							index++;
							process = index * 100 / count;
							String email = emailModel + index;
							BQMember member = bqMemberService.findUserByEmail(email);
							if (member != null) {
								continue;
							}
							String password = RandomUtil.getRandomNormalString(8);
							BQMember subUser = new BQMember();
							subUser.setF_user_email(email);
							subUser.setF_real_email(email);
							subUser.setF_user_pwd(password);

							registerSub(motherUser, subUser, allowIp);
							
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			};
			Thread thread = new Thread(runnable);
			thread.start();
		}

		return super.redirect("/managerSubUserCreate/load.do?userid=" + motherUser.getF_user_id() + "&create=true");
	}

	@RequestMapping(value = "/getProcess")
	public void getProcess(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject data = new JSONObject();
		BigDecimal rate = new BigDecimal(process);
		rate = rate.setScale(2, RoundingMode.HALF_UP);
		data.put("rate", rate);
		toWeb(response, data);
	}

	private long registerSub(BQMember motherUser, BQMember subUser, String ip) {
		String password = subUser.getF_user_pwd();
		final String unencryptedPassword = password;
		String salt = RandomUtil.getRandomString(6);
		try {
			password = MD5Service.encryptString(password).toLowerCase();
			password = MD5Service.encryptString(password + salt).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		subUser.setF_user_pwd(password);
		subUser.setF_user_salt(salt);
		subUser.setF_user_email_status(1);
		subUser.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
		subUser.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		subUser.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		subUser.setF_user_salt(salt);
		subUser.setF_user_origin("");
		subUser.setF_first_login(1);
		subUser.setIs_sub_user(1);
		// 注册时间
		subUser.setF_user_time((int) (System.currentTimeMillis() / 1000));

		// 获取母账户地区
		BQMemberArea motherArea = bqMemberAreaService.findByUserId(motherUser.getF_user_id());
		BQMemberArea subArea = new BQMemberArea();
		subArea.setCountry_code(motherArea.getCountry_code());
		subArea.setCountry_name(motherArea.getCountry_name());
		subArea.setProvince_code(motherArea.getProvince_code());
		subArea.setProvince_name(motherArea.getProvince_name());
		subArea.setCity_code(motherArea.getCity_code());
		subArea.setCity_name(motherArea.getCity_name());
		registerService.registerSubUser(motherUser, subUser, "", subArea);

		long subUserId = subUser.getF_user_id();
		
		// 绑定appkey
		BQUserAppKey userAppKey = new BQUserAppKey();
		final String appKey = UUID.randomUUID().toString().replaceAll("-", "");
		final String appSecret = UUID.randomUUID().toString().replaceAll("-", "");
		userAppKey.setF_user_id(subUserId);
		userAppKey.setAllow_ip(ip);
		userAppKey.setApp_key(appKey);
		userAppKey.setApp_secret(appSecret);
		long overTime = 0;
		if (StringUtil.isEmpty(ip))
			overTime = System.currentTimeMillis() / 1000 + 90 * 24 * 3600;
		userAppKey.setOver_time(overTime);
		userAppKey.setState(1);
		userAppKey.setUpdate_time(System.currentTimeMillis() / 1000);
		bqUserAppKeyService.save(userAppKey);
		
		// 子账号相关信息
		BQSubMemberInfo info = new BQSubMemberInfo();
		info.setF_user_id(subUserId);
		info.setF_user_email(subUser.getF_user_email());
		info.setUnencrypted_password(unencryptedPassword);
		info.setApp_key(appKey);
		info.setApp_secret(appSecret);
		info.setAllow_ip(ip);
		info.setDateline(System.currentTimeMillis() / 1000);
		bqSubMemberInfoService.save(info);
		
		// 奖励BFC
		BQBftRewardSetting setting = bqBftRewardSettingService.get();
		doRegisterRewardBft(subUserId, setting);
		
		return subUserId;
	}

	private void doRegisterRewardBft(long userId, BQBftRewardSetting setting) {
		if (setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0 && setting.getOld_register_rewarded()) {
			BQBftRewardRecord record = new BQBftRewardRecord();
			record.setF_user_id(userId);
			record.setCurrency_type_id(setting.getCurrency_type_id());
			record.setReason(Contents.REWARD_REASON_REGISTER);
			record.setAmount(setting.getRegister_reward());
			record.setNote("用户注册赠送");

			bqBftRewardRecordService.bonus(record);
		}
	}

	@RequestMapping("/export")
	public void export(HttpServletResponse response, HttpServletRequest request) throws ParseException {
		String userid = request.getParameter("userid");

		if (StringUtil.isEmpty(userid)) {
			return;
		}
		long userId = ConvertUtil.toLong(userid, 0l);
		BQMember motherUser = bqMemberService.readUserById(userId);
		if (motherUser == null) {
			return;
		}

		List<Object[]> objs = bqSubMemberService.readAllSubUserAndAppkey(motherUser.getF_user_id());
		List<UserAppKeyVo> list = new ArrayList<UserAppKeyVo>();
		for (int i = 0; i < objs.size(); i++) {
			Object[] obj = objs.get(i);
			UserAppKeyVo vo = new UserAppKeyVo();
			vo.setId(i + 1);
			vo.setName((String) obj[1]);
			vo.setPassword((String) obj[2]);
			vo.setAppKey((String) obj[3]);
			vo.setAppSecret((String) obj[4]);
			vo.setIp((String) obj[5]);
			list.add(vo);
		}

		ServletOutputStream outputStream = null;
		List<CellMap> cellMapList = new ArrayList<CellMap>();
		cellMapList.add(new CellMap("序号", "id"));
		cellMapList.add(new CellMap("登录名", "name"));
		cellMapList.add(new CellMap("密码", "password"));
		cellMapList.add(new CellMap("访问公钥", "appKey"));
		cellMapList.add(new CellMap("访问私钥", "appSecret"));
		cellMapList.add(new CellMap("IP地址", "ip"));

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
					+ motherUser.getF_user_email() + "-sub-api.xlsx");
			outputStream = response.getOutputStream();
			ExcelExportUtil.exportSXSSFExcel("子账号api信息", cellMapList, list, rowAccessWindowSize, outputStream);
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

}
