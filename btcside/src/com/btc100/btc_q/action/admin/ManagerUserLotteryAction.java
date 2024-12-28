package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 用户大转盘抽奖
 */
@Controller
@RequestMapping(value = "/managerUserLottery")
public class ManagerUserLotteryAction extends BaseAction{

    @Autowired
    private BQMemberDrawService bqMemberDrawService;
    @Autowired
    private BQLuckObtainRecordService bqLuckObtainRecordService;
    @Autowired
    private BQLuckDrawRecordService bqLuckDrawRecordService;
    @Autowired
    private BQAppointLuckService bqAppointLuckService;
    @Autowired
    private BQLuckDrawSettingService bqLuckDrawSettingService;
    @Autowired
    private SpyMemcachedClient spyMemcachedClient;

    @RequestMapping(value = "/loadDraw")
    public String loadDraw(HttpServletRequest request, Model model) throws JSONException {
       String error = request.getParameter("error");
       if (!StringUtil.isEmpty(error)) {
           if (error.equals("error1")) {
               model.addAttribute("msg", "请输入具体的参数");
           } else if (error.equals("error2")) {
               model.addAttribute("msg", "该抽奖设置不存在");
           }
       }

       String success = request.getParameter("success");
       if (!StringUtil.isEmpty(success)) {
           model.addAttribute("msg", "修改成功");
       }
       PagerModel pm = bqLuckDrawSettingService.readModel();
       model.addAttribute("pm", pm);
       String prize = request.getParameter("prize");
       BQLuckDrawSetting bqLuckDrawSetting = new BQLuckDrawSetting();
       if (!StringUtil.isEmpty(prize)) bqLuckDrawSetting = bqLuckDrawSettingService.readByPrize(prize);
       model.addAttribute("bqLuckDrawSetting", bqLuckDrawSetting);

       return super.url_JSP("managerLotterySetting");
    }

    @RequestMapping(value = "/doSettingDraw", method = RequestMethod.POST)
    public String settingDrawName(HttpServletRequest request, Model model){
        int id = Convert.toInt(request.getParameter("id"), 0);
        String prize = request.getParameter("prize");
        String nameZh = request.getParameter("name_zh_CN");
        String nameUs = request.getParameter("name_en_US");
        double probability = Convert.toDouble(request.getParameter("probability"), 0);
        String prizeMatter = request.getParameter("prize_matter");
        String prizeUsdt = request.getParameter("prize_usdt");
        String notes = request.getParameter("notes");
        String photo_name = request.getParameter("photo_name");
        BigDecimal countUsdt = BigDecimal.ZERO;

        if (!StringUtil.isEmpty(prizeUsdt)) {
            countUsdt = new BigDecimal(prizeUsdt);
        }
        if (StringUtil.isEmpty(prizeMatter)) {
            prizeMatter = "";
        }
        if (StringUtil.isEmpty(notes)) {
            notes = "";
        }
        if (StringUtil.isEmpty(photo_name)) {
            photo_name = "";
        }

        if (StringUtil.isEmpty(nameZh) || StringUtil.isEmpty(nameUs) || id == 0 || StringUtil.isEmpty(prize)) {
            model.addAttribute("error", "error1");
            return super.redirect("/managerUserLottery/loadDraw.do");
        }
        BQLuckDrawSetting bqLuckDrawSetting = bqLuckDrawSettingService.readByPrize(prize);
        if (null == bqLuckDrawSetting){
            model.addAttribute("error", "error2");
            return super.redirect("/managerUserLottery/loadDraw.do");
        }

        bqLuckDrawSetting.setProbability(probability);
        bqLuckDrawSetting.setName_zh_CN(nameZh);
        bqLuckDrawSetting.setName_en_US(nameUs);
        bqLuckDrawSetting.setNotes(notes);
        bqLuckDrawSetting.setPhoto_name(photo_name);
        bqLuckDrawSetting.setPrize_matter(prizeMatter);
        bqLuckDrawSetting.setPrize_usdt(countUsdt);
        bqLuckDrawSetting.setPrize(bqLuckDrawSetting.getPrize());
        bqLuckDrawSettingService.update(bqLuckDrawSetting);
        model.addAttribute("success", "success");
        model.addAttribute("prize", bqLuckDrawSetting.getPrize());

        List<BQLuckDrawSetting> bqLuckDrawSettings = bqLuckDrawSettingService.readAllSettigs();
        spyMemcachedClient.set(BTCQMemecacheKeys.PRIZE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqLuckDrawSettings);

        return super.redirect("/managerUserLottery/loadDraw.do");
    }

    @RequestMapping(value = "/loadDrawRecord")
    public String loadDrawRecord(HttpServletRequest request, Model model, HttpServletResponse response){
        long uid = Convert.toInt(request.getParameter("uid"), -1);
        String award = request.getParameter("award");//奖项

        BigDecimal count = bqLuckObtainRecordService.readCountCoin(0);
        BigDecimal userCount = bqLuckObtainRecordService.readCountCoin(uid);
        DecimalFormat df = new DecimalFormat("0.00000000");
        String cou = df.format(count);
        String uCou = df.format(userCount);
        model.addAttribute("count", cou);
        model.addAttribute("userCount", uCou);
        PagerModel pm = bqLuckDrawRecordService.readModelByIdAndPrize(uid, award);
        model.addAttribute("pm", pm);
        if (uid >= 0) model.addAttribute("uid", uid);
        model.addAttribute("award", award);

        List<BQLuckDrawSetting> bqLuckDrawSettings = bqLuckDrawSettingService.readAllSettigs();
        model.addAttribute("settings", bqLuckDrawSettings);
        return super.url_JSP("luckDrawRecord");
    }

    @RequestMapping(value = "/loadUserDraw")
    public String loadUserDraw(HttpServletRequest request, Model model){
        long uid = Convert.toInt(request.getParameter("uid"), 0);
        PagerModel pm = bqMemberDrawService.readModel(uid);
        @SuppressWarnings("unchecked")
		List<BQMemberDraw> bqMemberDraws = (List<BQMemberDraw>) pm.getDatas();
        for (BQMemberDraw bqMemberDraw : bqMemberDraws) {
            String address = "";
            if (null != bqMemberDraw.getAddress()) {
                address = bqMemberDraw.getAddress().replace("G:", "");
                address = address.replace("P:", "");
                address = address.replace("S:", "");
                address = address.replace("Q:", "");
                address = address.replace("D:", "");
                bqMemberDraw.setAddress(address);
            }
        }
        model.addAttribute("pm", pm);
        model.addAttribute("uid", uid);
        return super.url_JSP("userStatisticsList");
    }

    @RequestMapping(value = "/loadAppointRecord")
    public String loadAppointRecord(HttpServletRequest request, Model model) {
        int userId = Convert.toInt(request.getParameter("uid"), 0);
        PagerModel pm = bqAppointLuckService.readByUid(userId);
        if (userId > 0) model.addAttribute("uid", userId);
        model.addAttribute("pm", pm);

        String msg = request.getParameter("msg");
        if (!StringUtil.isEmpty(msg)) {
            if (msg.equals("success")) {
                model.addAttribute("msg", "删除成功");
            } else {
                model.addAttribute("msg", "删除失败");
            }
        }

        return super.url_JSP("appointList");
    }

    @RequestMapping(value = "/deleteAppointRecord")
    public String deleteAppointRecord(HttpServletRequest request, Model model) {
        int id = Convert.toInt(request.getParameter("id"), 0);
        if (id == 0) {
            model.addAttribute("msg", "error");
        }
        if (bqAppointLuckService.deleteById(id) > 0) {
            model.addAttribute("msg", "success");
        } else {
            model.addAttribute("msg", "error");
        }

        return super.redirect("/managerUserLottery/loadAppointRecord.do");
    }

    @RequestMapping(value = "/loadOnlyUser")
    public String loadOnlyUser(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        if (!StringUtil.isEmpty(error)) model.addAttribute("msg", "指定失败");
        String success = request.getParameter("success");
        if (!StringUtil.isEmpty(success)) model.addAttribute("msg", "指定成功");

        long uid = Convert.toInt(request.getParameter("uid"), 0);
        model.addAttribute("uid", uid);
        List<BQLuckDrawSetting> bqLuckDrawSettings = bqLuckDrawSettingService.readAllSettigs();
        model.addAttribute("settings", bqLuckDrawSettings);
        return  super.url_JSP("appointUserPrizeSet");
    }

    @RequestMapping(value = "/updateUserAward")
    public String updateUserAward(BQAppointLuck bqAppointLuck, Model model){
        try {
            bqAppointLuckService.save(bqAppointLuck);
            model.addAttribute("success","success");
        } catch (Exception e) {
            model.addAttribute("error","error");
        }
        model.addAttribute("uid", bqAppointLuck.getUser_id());
        return super.redirect("/managerUserLottery/loadOnlyUser.do");
    }

}
