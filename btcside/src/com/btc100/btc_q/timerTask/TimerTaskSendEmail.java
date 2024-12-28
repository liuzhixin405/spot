package com.btc100.btc_q.timerTask;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.SendMail;

public class TimerTaskSendEmail {

	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberService bqMemberService;
	
	public void run() {
		List<BQAccount> lstBqAccounts = bqAccountService.findMoneyUser(Contents.TRADE_CURRENCY_TYPE_ID);
		for (BQAccount account : lstBqAccounts) {
			long uid = account.getF_user_id();
			if(bqMemberSafeSettingsService.checkUserSafe(uid)){
				BQMember user = bqMemberService.findUserById(uid);
				if (user == null) continue;
				SendMail themail = new SendMail();
				StringBuilder sb = new StringBuilder();
				sb.append("<div style=\"width:800px; margin:60px auto;overflow:hidden; font-size:12px; -webkit-border-radius:5px; -moz-border-radius:5px;  -ms-border-radius:5px; -o-border-radius:5px;  border-radius:5px;  -webkit-box-shadow:0 0 15px #eee; -moz-box-shadow:0 0 15px #eee; box-shadow:0 0 15px #eee;color:#6a6a6a;border:1px solid #dbdbdb;\">");
				sb.append("<h1 style=\"position:relative;width:100%;font-size:18px; font-weight:normal;height:50px;line-height:50px;background:#00376a;color:#7dc0ff;margin:0;\">");
				sb.append("<span style=\"padding-left:20px;\">尊敬的XKD用户：</span>");
				sb.append("</h1>");
				sb.append("<div style=\"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:10px 35px;\">");
				sb.append("<p style=\"line-height:1.8em; margin:15px 0;\">您好！</p>");
				sb.append("<p>");
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们已注意到您已向本平台充值，非常感谢您对我们的信任，我们一定努力做到最好！");
				sb.append("<br/>");
				sb.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;但我们发现您的账户目前没有任何安全防范措施，因此希望您能尽快提高您的账号的安全级别。");
				sb.append("<br/>");
				sb.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们建议您至少选择谷歌验证和手机验证两种验证方式中的一种验证，并希望您最好能够同时验证。 ");
				sb.append("<br/>");
				sb.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;再次致谢！");
				sb.append("</p>");
				sb.append("<p style=\"line-height:1.8em;margin:15px 0;text-align:right; \">- XKD团队</p>");
				sb.append("</div>");
				sb.append("</div>");
				themail.send(user.getF_real_email(), sb.toString());
			}
		}
	}
	
}
