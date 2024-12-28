package com.btc100.btc_q.action.api;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationRequest;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
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
import java.util.*;

/**
 * 抽奖
 */
@Controller
@RequestMapping("/drawApi")
public class DrawApiAction extends BaseApiAction{

    private static final Logger logger = Logger.getLogger(ExchangeApiAction.class);

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


    @RequestMapping(value = "/getDrawInfo")
    public String findUserRecord(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
        Contents.Local.LOCALE.set(getLocale(request));
        Gson gson = new Gson();
        JSONObject userJson = new JSONObject();
        userJson.put("code", 0);
        userJson.put("msg", "");

        Map<String, Object> recordMap = new HashMap<>();
        List<BQLuckObtainRecord> obtainRecords = spyMemcachedClient.get(BTCQMemecacheKeys.AWARD_WINNING_LIST);
        if (null == obtainRecords) {
            obtainRecords = bqLuckObtainRecordService.readLast20Draw();        //获奖记录
            spyMemcachedClient.set(BTCQMemecacheKeys.AWARD_WINNING_LIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, obtainRecords);
        }
        List<UserLotteryVO> obtainRecordVOS = new ArrayList<>();
        for (BQLuckObtainRecord obtainRecord : obtainRecords) {
            obtainRecordVOS.add(new UserLotteryVO(obtainRecord.getUser_name(), obtainRecord.getNotes()));
        }
        recordMap.put("obtainRecords", obtainRecordVOS);

        List<BQLuckDrawSetting> bqLuckDrawSetting = spyMemcachedClient.get(BTCQMemecacheKeys.PRIZE_SETTING);
        if (null == bqLuckDrawSetting) {
            bqLuckDrawSetting = bqLuckDrawSettingService.readAllSettigs();        //奖项设置
            spyMemcachedClient.set(BTCQMemecacheKeys.PRIZE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqLuckDrawSetting);
        }
        List<UserLotteryVO> drawSettingVOS = new ArrayList<>();
        for (BQLuckDrawSetting luckDrawSetting : bqLuckDrawSetting) {
            drawSettingVOS.add(new UserLotteryVO(luckDrawSetting.getPrize(), luckDrawSetting.getNotes(), luckDrawSetting.getPhoto_name(), 0));
        }

        Map<String,Object> map = new HashMap<String, Object>();
        String checkToken = checkToken(request, map);
        if (!StringUtil.isEmpty(checkToken)) {
            recordMap.put("count", 0);
            recordMap.put("usdtFee", 0);
            recordMap.put("usdrFee", 0);
            recordMap.put("drawSettings", drawSettingVOS);
            userJson.put("data", new JSONObject(gson.toJson(recordMap)));
            model.addAttribute("ticker", userJson);
            return apiData(response);
        }

        BQMember user = (BQMember) map.get("user");
        String token = (String) map.get("token");
        userJson.put("token", token);

        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById((int) user.getF_user_id());
        if (bqMemberDraw == null) {
            recordMap.put("count", 0);
            recordMap.put("usdtFee", 0);
            recordMap.put("usdrFee", 0);
        } else {
            int count = bqMemberDraw.getF_draw_count() - bqMemberDraw.getF_already_draw_count();
            recordMap.put("count", count < 0 ? 0 : count);
            recordMap.put("usdtFee", bqMemberDraw.getF_produce_fee_usdt());
            recordMap.put("usdrFee", bqMemberDraw.getF_produce_fee_usdr());
        }

        Map<String, Integer> countPrize = bqLuckDrawRecordService.readCountGroupPrizeByUid(user.getF_user_id());
        for (String integer : countPrize.keySet()) {
            for (UserLotteryVO drawSettingVO : drawSettingVOS) {
                if (integer.equals(drawSettingVO.getPrize())) {
                    drawSettingVO.setCount(countPrize.get(integer));
                }
            }
        }
        recordMap.put("drawSettings", drawSettingVOS);

        List<BQLuckDrawRecord> drawRecords = bqLuckDrawRecordService.readDrawRecordById(user.getF_user_id());
        List<UserLotteryVO> drawRecordVOS = new ArrayList<>();
        for (BQLuckDrawRecord drawRecord : drawRecords) {
            drawRecordVOS.add(new UserLotteryVO(drawRecord.getDraw_time(), drawRecord.getPrize_name(), drawRecord.getPrize()));
        }
        recordMap.put("drawRecords", drawRecordVOS);

        userJson.put("data", new JSONObject(gson.toJson(recordMap)));
        model.addAttribute("ticker", userJson);
        return apiData(response);
    }

    @RequestMapping(value = "/addAdress")
    public String addAdress(HttpServletRequest request, Model model, HttpServletResponse response) throws JSONException {
        Locale locale = Utils.getLocale(request);
        request.setAttribute("locale", locale);

        Gson gson = new Gson();
        JSONObject userJson = new JSONObject();
        Map<String, Object> paramMap = new HashMap<>();
        userJson.put("code", 0);
        userJson.put("msg", "");

        Map<String,Object> map = new HashMap<String, Object>();
        String checkToken = checkToken(request, map);
        if (!StringUtil.isEmpty(checkToken)) {
            String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
            model.addAttribute("ticker", tradesStr);
            return apiData(response);
        }

        BQMember user = (BQMember) map.get("user");
        String token = (String) map.get("token");
        userJson.put("token", token);

        if (null == bqLuckObtainRecordService.readMatterPrizeById(user.getF_user_id())) {
            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未获得实物奖励");
            model.addAttribute("ticker", tradesStr);
            return apiData(response);
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
            paramMap.put("addressee", addressee);
            String phoneAndAddress = bqMemberDraw.getAddress().substring(bqMemberDraw.getAddress().indexOf(",") + 1);
            String phone = phoneAndAddress.substring(0, phoneAndAddress.indexOf(","));
            paramMap.put("phone", phone);
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
            paramMap.put("address", addressl);
        }

        paramMap.put("listCountry", lis);
        paramMap.put("countryCode", countryID);
        paramMap.put("currCountryName", country);
        paramMap.put("proviceCode", proID);
        paramMap.put("currProvinceName", pro);
        paramMap.put("cityCode", cityID);
        paramMap.put("currCityName", city);
        paramMap.put("areaCode", areaID);
        paramMap.put("currAreaName", area);
        userJson.put("data", new JSONObject(gson.toJson(paramMap)));
        model.addAttribute("ticker", userJson);
        return apiData(response);
    }

    @RequestMapping(value = "/doAddArdess", method = RequestMethod.POST)
    public String doAddArdess(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
        Locale locale = Utils.getLocale(request);
        request.setAttribute("locale", locale);
//        Map<String,Object> map = new HashMap<String, Object>();
//        String checkToken = checkToken(request, map);

        String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, SpringContext.getText("msg.pg.userLottery.end", Contents.Local.LOCALE.get()));
        model.addAttribute("ticker", tradesStr);
        return apiData(response);

//        if (!StringUtil.isEmpty(checkToken)) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//
//        BQMember user = (BQMember) map.get("user");
//        String token = (String) map.get("token");
//        JSONObject userJson = new JSONObject();
//        userJson.put("code", 0);
//        userJson.put("msg", "");
//        userJson.put("token", token);
//
//        if (null == bqLuckObtainRecordService.readMatterPrizeById(user.getF_user_id())) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未获得实物奖励");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//
//        String phone = request.getParameter("phone");
//        String address = request.getParameter("address");
//        String addressee = request.getParameter("addressee");
//        if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(address) || StringUtil.isEmpty(addressee)) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，请重新提交");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//        Matcher m = p.matcher(phone);
//        if (!m.matches()) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，请重新提交");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//
//        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById(user.getF_user_id());
//        if (null == bqMemberDraw) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_OVERTIME, "用户不存在，请稍后再试");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//        String country = request.getParameter("countryCode");           //国
//        if (!country.equals("CN")) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "当前地址不支持，请换个国家");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
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
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "请正确选择省份");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        } else {
//            proName = provicel.getName();
//        }
//        String city = request.getParameter("cityCode");                 //城
//        BQAreaCity cityl = bqAreaCityService.readCityByCode(city);
//        String cityName = "";
//        if (cityl == null) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "请正确选择城市");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
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
//        model.addAttribute("ticker", userJson);
//        return apiData(response);
    }

    @RequestMapping(value = "/userLuckDraw" , method = RequestMethod.POST)
    public String userLuckDraw(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException{
        Locale locale = Utils.getLocale(request);
        request.setAttribute("locale", locale);
//        Map<String,Object> map = new HashMap<String, Object>();
//        String checkToken = checkToken(request, map);

        String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, SpringContext.getText("msg.pg.userLottery.end", Contents.Local.LOCALE.get()));
        model.addAttribute("ticker", tradesStr);
        return apiData(response);

//        if (!StringUtil.isEmpty(checkToken)) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//
//        BQMember user = (BQMember) map.get("user");
//        String token = (String) map.get("token");
//        Gson gson = new Gson();
//        JSONObject userJson = new JSONObject();
//        userJson.put("code", 0);
//        userJson.put("msg", "");
//        userJson.put("token", token);
//
//        BQMemberDraw bqMemberDraw = bqMemberDrawService.readById(user.getF_user_id());
//        if (bqMemberDraw == null) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "可抽取次数不足");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//
//        int count = bqMemberDraw.getF_draw_count() - bqMemberDraw.getF_already_draw_count();
//        if (count <= 0) {
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "可抽取次数不足");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
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
//            String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "服务器错误，请稍后重试，或联系客服");
//            model.addAttribute("ticker", tradesStr);
//            return apiData(response);
//        }
//        Map<String, Object> mapl = new HashMap<>();
//        mapl.put("name", bqLuckDrawSetting.getName_zh_CN());
//        mapl.put("notes", bqLuckDrawSetting.getNotes());
//        mapl.put("prize", bqLuckDrawSetting.getPrize());
//        mapl.put("isMatter", StringUtil.isEmpty(bqLuckDrawSetting.getPrize_matter()) ? 0 : 1);
//
//        userJson.put("data", new JSONObject(gson.toJson(mapl)));
//        model.addAttribute("ticker", userJson);
//        return apiData(response);
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
