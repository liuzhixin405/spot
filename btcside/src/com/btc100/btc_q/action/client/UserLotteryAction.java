package com.btc100.btc_q.action.client;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationRequest;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.UserLotteryVO;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 抽奖
 */
@Controller
@RequestMapping("/userLottery")
public class UserLotteryAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(UserLotteryAction.class);

    @Autowired
    private BQMemberDrawService bqMemberDrawService;
    @Autowired
    private BQLuckObtainRecordService bqLuckObtainRecordService;
    @Autowired
    private BQLuckDrawRecordService bqLuckDrawRecordService;
    @Autowired
    private BQLuckDrawSettingService bqLuckDrawSettingService;
    @Autowired
    private BQAreaProvinceService bqAreaProvinceService;
    @Autowired
    private BQAreaCityService bqAreaCityService;
    @Autowired
    private BQAreaCountryService bqAreaCountryService;
    @Autowired
    private BQAreaAreaService bqAreaAreaService;
    @Autowired
    private SpyMemcachedClient spyMemcachedClient;

    private final static String AccessKeyId = "";
    private final static String AccessKeySecret = "";


    @RequestMapping(value = "/toDrawInfo")
    public String toFindUserRecord(HttpServletRequest request, Model model){
        Contents.Local.LOCALE.set(getLocale(request));
        String error = "";
        try {
            request.setCharacterEncoding("UTF-8");
            error = request.getParameter("error");
            if ("error".equals(error)) {
                error = "暂未获得实物奖励";
            }
            if (StringUtil.isEmpty(error)) {
                error = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        model.addAttribute("error", error);

        List<BQLuckObtainRecord> obtainRecords = spyMemcachedClient.get(BTCQMemecacheKeys.AWARD_WINNING_LIST);
        if (null == obtainRecords) {
            obtainRecords = bqLuckObtainRecordService.readLast20Draw();        //获奖记录
            spyMemcachedClient.set(BTCQMemecacheKeys.AWARD_WINNING_LIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, obtainRecords);
        }
        List<UserLotteryVO> obtainRecordVOS = new ArrayList<>();
        for (BQLuckObtainRecord obtainRecord : obtainRecords) {
            obtainRecordVOS.add(new UserLotteryVO(obtainRecord.getUser_name(), obtainRecord.getNotes()));
        }
        model.addAttribute("obtainRecords", obtainRecordVOS);
        List<BQLuckDrawSetting> bqLuckDrawSetting = spyMemcachedClient.get(BTCQMemecacheKeys.PRIZE_SETTING);
        if (null == bqLuckDrawSetting) {
            bqLuckDrawSetting = bqLuckDrawSettingService.readAllSettigs();        //奖项设置
            spyMemcachedClient.set(BTCQMemecacheKeys.PRIZE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqLuckDrawSetting);
        }
        List<UserLotteryVO> drawSettingVOS = new ArrayList<>();
        for (BQLuckDrawSetting luckDrawSetting : bqLuckDrawSetting) {
            drawSettingVOS.add(new UserLotteryVO(luckDrawSetting.getPrize(), luckDrawSetting.getNotes(), luckDrawSetting.getPhoto_name(), 0));
        }

        PagerModel pmDrawRecords = new PagerModel();

        BQMember user = super.findCurrUser(request, true);
        if(user == null) {
            model.addAttribute("count", 0);
            model.addAttribute("usdtFee", 0);
            model.addAttribute("usdrFee", 0);
            model.addAttribute("pm", pmDrawRecords);
            model.addAttribute("drawSettings", drawSettingVOS);
            return "jsp/user_bigTurntableLottery";
        }

        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById((int) user.getF_user_id());
        if (bqMemberDraw == null) {
            model.addAttribute("count", 0);
            model.addAttribute("usdtFee", 0);
            model.addAttribute("usdrFee", 0);
        } else {
            int count = bqMemberDraw.getF_draw_count() - bqMemberDraw.getF_already_draw_count();
            model.addAttribute("count", count < 0 ? 0 : count);
            model.addAttribute("usdtFee", bqMemberDraw.getF_produce_fee_usdt());
            model.addAttribute("usdrFee", bqMemberDraw.getF_produce_fee_usdr());
        }

        pmDrawRecords = bqLuckDrawRecordService.readModelRecordById(user.getF_user_id());
        @SuppressWarnings("unchecked")
		List<BQLuckDrawRecord> drawRecordsl = (List<BQLuckDrawRecord>) pmDrawRecords.getDatas();
        List<UserLotteryVO> lotteryVOS = new ArrayList<>();
        for (BQLuckDrawRecord drawRecord : drawRecordsl) {
            lotteryVOS.add(new UserLotteryVO(drawRecord.getDraw_time(), drawRecord.getPrize_name(), drawRecord.getPrize()));
        }
        pmDrawRecords.setDatas(lotteryVOS);
        model.addAttribute("pm", pmDrawRecords);

        Map<String, Integer> countPrize = bqLuckDrawRecordService.readCountGroupPrizeByUid(user.getF_user_id());
        for (String integer : countPrize.keySet()) {
            for (UserLotteryVO drawSettingVO : drawSettingVOS) {
                if (integer.equals(drawSettingVO.getPrize())) {
                    drawSettingVO.setCount(countPrize.get(integer));
                }
            }
        }
        model.addAttribute("drawSettings", drawSettingVOS);

        List<BQLuckDrawRecord> drawRecords = bqLuckDrawRecordService.readDrawRecordById(user.getF_user_id());       //用户抽奖记录
        List<UserLotteryVO> drawRecordVOS = new ArrayList<>();
        for (BQLuckDrawRecord drawRecord : drawRecords) {
            drawRecordVOS.add(new UserLotteryVO(drawRecord.getDraw_time(), drawRecord.getPrize_name(), drawRecord.getPrize()));
        }
        model.addAttribute("drawRecords", drawRecordVOS);

        return "jsp/user_bigTurntableLottery";
    }

    @RequestMapping(value = "/getDrawInfo")
    public void findUserRecord(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        Contents.Local.LOCALE.set(getLocale(request));
        JSONObject userJson = new JSONObject();
        userJson.put("code", 0);
        userJson.put("msg", "success");
        Gson gson = new Gson();
        BQMember user = super.findCurrUser(request, true);

        List<BQLuckObtainRecord> obtainRecords = spyMemcachedClient.get(BTCQMemecacheKeys.AWARD_WINNING_LIST);
        if (null == obtainRecords) {
            obtainRecords = bqLuckObtainRecordService.readLast20Draw();        //获奖记录
            spyMemcachedClient.set(BTCQMemecacheKeys.AWARD_WINNING_LIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, obtainRecords);
        }
        List<UserLotteryVO> obtainRecordVOS = new ArrayList<>();
        for (BQLuckObtainRecord obtainRecord : obtainRecords) {
            obtainRecordVOS.add(new UserLotteryVO(obtainRecord.getUser_name(), obtainRecord.getNotes()));
        }
        userJson.put("obtainRecords", gson.toJson(obtainRecordVOS));

        List<BQLuckDrawSetting> bqLuckDrawSetting = spyMemcachedClient.get(BTCQMemecacheKeys.PRIZE_SETTING);
        if (null == bqLuckDrawSetting) {
            bqLuckDrawSetting = bqLuckDrawSettingService.readAllSettigs();        //奖项设置
            spyMemcachedClient.set(BTCQMemecacheKeys.PRIZE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqLuckDrawSetting);
        }
        List<UserLotteryVO> drawSettingVOS = new ArrayList<>();
        for (BQLuckDrawSetting luckDrawSetting : bqLuckDrawSetting) {
            drawSettingVOS.add(new UserLotteryVO(luckDrawSetting.getPrize(), luckDrawSetting.getNotes(), luckDrawSetting.getPhoto_name(), 0));
        }


        PagerModel pmDrawRecords =new PagerModel();

        if (user == null) {
            userJson.put("count", 0);
            userJson.put("usdtFee", 0);
            userJson.put("usdrFee", 0);
            userJson.put("pm", pmDrawRecords);
            userJson.put("drawSettings", gson.toJson(drawSettingVOS));
            toWeb(response, userJson);
            return;
        }

        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById((int) user.getF_user_id());
        if (bqMemberDraw == null) {
            userJson.put("count", 0);
            userJson.put("usdtFee", 0);
            userJson.put("usdrFee", 0);
        } else {
            int count = bqMemberDraw.getF_draw_count() - bqMemberDraw.getF_already_draw_count();
            userJson.put("count", count < 0 ? 0 : count);
            userJson.put("usdtFee", bqMemberDraw.getF_produce_fee_usdt());
            userJson.put("usdrFee", bqMemberDraw.getF_produce_fee_usdr());
        }

        pmDrawRecords = bqLuckDrawRecordService.readModelRecordById(user.getF_user_id());
        @SuppressWarnings("unchecked")
		List<BQLuckDrawRecord> drawRecordsl = (List<BQLuckDrawRecord>) pmDrawRecords.getDatas();
        List<UserLotteryVO> lotteryVOS = new ArrayList<>();
        for (BQLuckDrawRecord drawRecord : drawRecordsl) {
            lotteryVOS.add(new UserLotteryVO(drawRecord.getDraw_time(), drawRecord.getPrize_name(), drawRecord.getPrize()));
        }
        pmDrawRecords.setDatas(lotteryVOS);
        userJson.put("pm", gson.toJson(pmDrawRecords));

        Map<String, Integer> countPrize = bqLuckDrawRecordService.readCountGroupPrizeByUid(user.getF_user_id());
        for (String integer : countPrize.keySet()) {
            for (UserLotteryVO drawSettingVO : drawSettingVOS) {
                if (integer.equals(drawSettingVO.getPrize())) {
                    drawSettingVO.setCount(countPrize.get(integer));
                }
            }
        }
        userJson.put("drawSettings", gson.toJson(drawSettingVOS));

        List<BQLuckDrawRecord> drawRecords = bqLuckDrawRecordService.readDrawRecordById(user.getF_user_id());
        List<UserLotteryVO> drawRecordVOS = new ArrayList<>();
        for (BQLuckDrawRecord drawRecord : drawRecords) {
            drawRecordVOS.add(new UserLotteryVO(drawRecord.getDraw_time(), drawRecord.getPrize_name(), drawRecord.getPrize()));
        }
        userJson.put("drawRecords", gson.toJson(drawRecordVOS));
        toWeb(response, userJson);
    }


    //去到收货地址
    @RequestMapping(value = "/toAddAdress")
    public String toAddAdress(HttpServletRequest request, Model model) {
        Locale locale = Utils.getLocale(request);
        request.setAttribute("locale", locale);

        BQMember user = super.findCurrUser(request, true);
        if (user == null) {
            return "redirect:/login/login.do";
        }

        if (null == bqLuckObtainRecordService.readMatterPrizeById(user.getF_user_id())) {
            model.addAttribute("error", "error");
            return "redirect:/userLottery/toDrawInfo.do";
        }

        String countryID = "";
        String country = "";
        int proID = 0;
        String pro = "";
        int cityID = 0;
        String city = "";
        int areaID = 0;
        String area = "";
        String sip = super.getIpAddr(request);

        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            DescribeIpv4LocationRequest ipLocation = new DescribeIpv4LocationRequest();
            ipLocation.setIp(sip);
            try {
                DescribeIpv4LocationResponse ipLocalResponse = client.getAcsResponse(ipLocation);
                countryID = ipLocalResponse.getCountryCode();
                country = ipLocalResponse.getCountry();
                if (countryID.equals("IANA") || "未分配或者内网IP".equals(country) || country.contains("本机")) {
                    countryID = "CN";
                    // country = "中国";
                    proID = 0;
                    pro = "";
                    cityID = 0;
                    city = "";
                    areaID = 0;
                    area = "";
                } else {
                    pro = ipLocalResponse.getProvince();
                    city = ipLocalResponse.getCity();
                    area = ipLocalResponse.getCounty();
                    BQAreaProvince province = bqAreaProvinceService.readProviceByName(pro);
                    if (province != null) {
                        proID = ConvertUtil.toInt(province.getCode(), 0);
                    }

                    BQAreaCity ct = bqAreaCityService.readCityByName(city);
                    if (ct != null) {
                        cityID = ConvertUtil.toInt(ct.getCode(), 0);
                    }

                    BQAreaArea ae = bqAreaAreaService.readAreaByName(area);
                    if (ae != null) {
                        areaID = ConvertUtil.toInt(ae.getCode(), 0);
                    }
                }

            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                System.out.println("ErrCode:" + e.getErrCode());
                System.out.println("ErrMsg:" + e.getErrMsg());
                System.out.println("RequestId:" + e.getRequestId());
            }

        } catch (Exception e) {
            logger.error("收货地址通过IP判断所在地出错" + e.toString(), e);
        }

        List<BQAreaCountry> lis = bqAreaCountryService.readCountry();
        if ("en_US".equals(locale.toString())) {
            if ("CN".equals(countryID)) {
                country = "China";
                pro = StringUtil.getPinyinString(pro);
                city = StringUtil.getPinyinString(city);
                area = StringUtil.getPinyinString(area);
            } else if (!"xx".equals(countryID)) {
                for (BQAreaCountry bqAreaCountry : lis) {
                    if (bqAreaCountry.getCode().equals(countryID)) {
                        country = bqAreaCountry.getEn_name();
                        pro = "";
                        city = "";
                        area = "";
                    }
                }
            }
        }

        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById((int) user.getF_user_id());
        if(null != bqMemberDraw && !StringUtil.isEmpty(bqMemberDraw.getAddress())) {
            String addressee = bqMemberDraw.getAddress().substring(0, bqMemberDraw.getAddress().indexOf(","));
            model.addAttribute("addressee", addressee);
            String phoneAndAddress = bqMemberDraw.getAddress().substring(bqMemberDraw.getAddress().indexOf(",") + 1);
            String phone = phoneAndAddress.substring(0, phoneAndAddress.indexOf(","));
            model.addAttribute("phone", phone);
            String address = phoneAndAddress.substring(phoneAndAddress.indexOf(",") + 1);
            country = address.substring(address.indexOf("G:") + 2, address.indexOf("P:"));
            pro = address.substring(address.indexOf("P:") + 2, address.indexOf("S:"));
            city = address.substring(address.indexOf("S:") + 2, address.indexOf("Q:"));
            area = address.substring(address.indexOf("Q:") + 2, address.indexOf("D:"));
            String addressl = address.substring(address.indexOf("D:") + 2);

            countryID = bqAreaCountryService.readCountyByName(country).getCode();
            proID = Integer.parseInt(bqAreaProvinceService.readProviceByName(pro).getCode());
            cityID = Integer.parseInt(bqAreaCityService.readCityByName(city).getCode());
            BQAreaArea bqAreaArea = bqAreaAreaService.readAreaByCityCodeAndAreaName(area, String.valueOf(cityID));
            if (bqAreaArea != null) {
                areaID = Integer.parseInt(bqAreaArea.getCode());
            }
            model.addAttribute("address", addressl);
        }

        model.addAttribute("listCountry", lis);
        model.addAttribute("countryCode", countryID);
        model.addAttribute("currCountryName", country);
        model.addAttribute("proviceCode", proID);
        model.addAttribute("currProvinceName", pro);
        model.addAttribute("cityCode", cityID);
        model.addAttribute("currCityName", city);
        model.addAttribute("areaCode", areaID);
        model.addAttribute("currAreaName", area);

        return "jsp/user_addReceivingAddress";
    }

    @RequestMapping(value = "/addArdess", method = RequestMethod.POST)
    public void addArdess(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        Locale locale = Utils.getLocale(request);

        request.setAttribute("locale", locale);
        Gson gson = new Gson();
//        BQMember user = super.findCurrUser(request, true);
        AjaxCheckVO checkVO = new AjaxCheckVO();

        checkVO.setCode("error");
        checkVO.setMsg(SpringContext.getText("msg.pg.userLottery.end", Contents.Local.LOCALE.get()));
        toWeb(response, gson.toJson(checkVO));
        return;

//        if (user == null) {
//            checkVO.setCode("error");
//            checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//
//        if (null == bqLuckObtainRecordService.readMatterPrizeById(user.getF_user_id())) {
//            checkVO.setCode("error");
//            checkVO.setMsg("暂未获得实物奖励");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//
//        //此处做地址判断
//        String phone = request.getParameter("phone");
//        String address = request.getParameter("address");
//        String addressee = request.getParameter("addressee");
//        if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(address) || StringUtil.isEmpty(addressee)) {
//            checkVO.setCode("error");
//            checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//        Matcher m = p.matcher(phone);
//        if (!m.matches()) {
//            checkVO.setCode("error");
//            checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//
//        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById(user.getF_user_id());
//        if (null == bqMemberDraw) {
//            checkVO.setCode("error");
//            checkVO.setMsg("用户不存在，请稍后再试");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//        String country = request.getParameter("countryCode");           //国
//        if (!country.equals("CN")) {
//            checkVO.setCode("error");
//            checkVO.setMsg("当前国家地址不支持,请换个国家");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//        String countryName = "";
//        BQAreaCountry countryl = bqAreaCountryService.readCountyByCode(country);//
//        if (countryl != null)
//            countryName = countryl.getZh_name();
//
//        String provice = request.getParameter("proviceCode");           //省
//        BQAreaProvince provicel = bqAreaProvinceService.readProviceByCode(provice);
//        String proName = "";
//        if (provicel == null) {
//            checkVO.setCode("error");
//            checkVO.setMsg("请正确选择省份");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        } else {
//            proName = provicel.getName();
//        }
//        String city = request.getParameter("cityCode");                 //城
//        BQAreaCity cityl = bqAreaCityService.readCityByCode(city);
//        String cityName = "";
//        if (cityl == null) {
//            checkVO.setCode("error");
//            checkVO.setMsg("请正确选择城市");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        } else {
//            cityName = cityl.getName();
//        }
//
//        String area = request.getParameter("areaCode");                 //区
//        BQAreaArea areal = bqAreaAreaService.readAreaByCode(area);
//        String areaName= "";
//        if (areal != null) areaName = areal.getName();
//
//        String expressParam = addressee +","+phone +","+ "G:" +countryName + "P:" + proName + "S:" + cityName + "Q:" + areaName + "D:" + address;
//        bqMemberDraw.setAddress(expressParam);
//        bqMemberDrawService.update(bqMemberDraw);
//
//        JSONObject userJson = new JSONObject();
//        userJson.put("code", 0);
//        userJson.put("msg", SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
//        toWeb(response, userJson);
    }

    //用户抽奖
    @RequestMapping(value = "/userLuckDraw", method = RequestMethod.POST)
    public void userLuckDraw(HttpServletRequest request, HttpServletResponse response) throws JSONException{
        Contents.Local.LOCALE.set(getLocale(request));
        Gson gson = new Gson();
//        BQMember user = super.findCurrUser(request, true);
        AjaxCheckVO checkVO = new AjaxCheckVO();

        checkVO.setCode("error");
        checkVO.setMsg(SpringContext.getText("msg.pg.userLottery.end", Contents.Local.LOCALE.get()));
        toWeb(response, gson.toJson(checkVO));
        return;

//        if (user == null) {
//            checkVO.setCode("error");
//            checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById(user.getF_user_id());
//        if (bqMemberDraw == null) {
//            checkVO.setCode("error");
//            checkVO.setMsg("可抽取次数不足");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//
//        int count = bqMemberDraw.getF_draw_count() - bqMemberDraw.getF_already_draw_count();
//        if (count <= 0) {
//            checkVO.setCode("error");
//            checkVO.setMsg("可抽取次数不足");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//
//        BQLuckDrawSetting bqLuckDrawSetting = new BQLuckDrawSetting();
//        BQAppointLuck bqAppointLuck = bqAppointLuckService.getByUid(user.getF_user_id());
//        if (null != bqAppointLuck && bqAppointLuck.getNumber() == 0) {
//            bqLuckDrawSetting =  bqLuckDrawSettingService.readByPrize(bqAppointLuck.getPrize());
//        } else {
//            bqLuckDrawSetting = getLotteryIndex(bqLuckDrawSettingService.readAllSettigs());
//        }
//
//        try {
//            bqLuckDrawSettingService.updateCountByAward(bqLuckDrawSetting, bqMemberDraw, bqAppointLuck, user.getF_user_email());
//        } catch (Exception e) {
//            e.printStackTrace();
//            checkVO.setCode("error");
//            checkVO.setMsg("服务器错误，请稍后重试");
//            toWeb(response, gson.toJson(checkVO));
//            return;
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("zh_CN", bqLuckDrawSetting.getName_zh_CN());
//        map.put("en_US", bqLuckDrawSetting.getName_en_US());
//        map.put("notes", bqLuckDrawSetting.getNotes());
//        map.put("prize", bqLuckDrawSetting.getPrize());
//        map.put("isMatter", StringUtil.isEmpty(bqLuckDrawSetting.getPrize_matter()) ? 0 : 1 );
//
//        JSONObject userJson = new JSONObject();
//        userJson.put("code", 0);
//        userJson.put("msg", "success");
//        userJson.put("date", map);
//        toWeb(response, userJson);
    }


    public BQLuckDrawSetting getLotteryIndex(List<BQLuckDrawSetting> bqLuckDrawSettings) {
        List<BQLuckDrawSetting> drawSettings = new ArrayList<>();
        //计算总概率
        double sumProbability = 0d;
        for (BQLuckDrawSetting award : bqLuckDrawSettings) {
            if (award.getProbability() <= 0) {
                continue;
            }
            drawSettings.add(award);
            sumProbability += award.getProbability();
        }
        //奖品总数
        int size = drawSettings.size();
        //计算每个奖品的概率区间
        //例如奖品A概率区间0-0.1  奖品B概率区间 0.1-0.5 奖品C概率区间0.5-1
        //每个奖品的中奖率越大，所占的概率区间就越大
        List<Double> sortAwardProbabilityList = new ArrayList<Double>(size);
        Double tempSumProbability = 0d;
        for (BQLuckDrawSetting award : drawSettings) {
            tempSumProbability += award.getProbability();
            sortAwardProbabilityList.add(tempSumProbability / sumProbability);
        }

        //产生0-1之间的随机数
        //随机数在哪个概率区间内，则是哪个奖品
        double randomDouble = Math.random();
        //加入到概率区间中，排序后，返回的下标则是bqDrawSettings中中奖的下标
        sortAwardProbabilityList.add(randomDouble);
        Collections.sort(sortAwardProbabilityList);
        return drawSettings.get(sortAwardProbabilityList.indexOf(randomDouble));
    }

}
