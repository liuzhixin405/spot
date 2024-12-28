package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.UserAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 币币风控
 */
@Controller
@RequestMapping(value = "/managerSpotRiskControl")
public class ManagerSpotRiskControl extends BaseAction{

    @Autowired
    private BQMemberService bqMemberService;
    @Autowired
    private BQCurrencyTypeService bqCurrencyTypeService;
    @Autowired
    private BQAccountService bqAccountService;
    @Autowired
    private SpotPendingDelegationService spotPendingDelegationService;
    @Autowired
    private SpotMemberTradeDetailService memberTradeDetailService;

    /**
     *钱包账户统计
     */
    @RequestMapping("/statisticsData")
    public String statisticsData(HttpServletRequest request, String orderBy, String userName, String userId, String coinId, Model model) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        int currencyId = ConvertUtil.toInt(coinId, Contents.TRADE_CURRENCY_TYPE_ID);
        long uId = ConvertUtil.toLong(userId, 0L);
        BQMember user = new BQMember();
        if (uId > 0) {
            user = bqMemberService.readUserById(uId);
        }
        if (!StringUtil.isEmpty(userName)) {
            user = bqMemberService.readByRealEmail(userName);
        }
        if (user == null) {
            return super.errorPage(model, "查不到该用户");
        }

        List<BQCurrencyType> coins = new ArrayList<>();
        String[] results;
        String coin = manager.getUser_coins();
        boolean isContainCoin = false;
        if (null != coin && !"".equals(coin)){
            results= coin.split(",");
            for (String result:results) {
                BQCurrencyType bq = bqCurrencyTypeService.readByTypeName(result);
                coins.add(bq);
                if (bq.getType_id() == currencyId){
                    isContainCoin = true;
                }
            }
        }else {
            PagerModel pagerModel = new PagerModel();
            model.addAttribute("allCoin", new ArrayList<BQCurrencyType>());
            model.addAttribute("bqAccount", new BQAccount());
            model.addAttribute("pm", pagerModel);
            model.addAttribute("orderBy", "All");
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);
            model.addAttribute("coinId", currencyId);
            return super.url_JSP("spotManagerAccountStatistics");
        }

        if (!isContainCoin) currencyId=coins.get(0).getType_id();
        BQAccount bqAccount = bqAccountService.readAccountStatistics(currencyId);
//        BQCurrencyType currencyType = null;
//        for (BQCurrencyType type : coins) {
//            if (type.getType_id() == currencyId) {
//                currencyType = type;
//                break;
//            }
//        }

        PagerModel pm = bqAccountService.readStatistics(orderBy, user.getF_user_id(), currencyId);
        model.addAttribute("allCoin", coins);
        model.addAttribute("bqAccount", bqAccount);
        model.addAttribute("pm", pm);
        model.addAttribute("orderBy", StringUtil.isEmpty(orderBy) ? "ALL" : orderBy);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("coinId", currencyId);

        if (pm.getTotal() > 0) {
            List<UserAccountVo> newList = new ArrayList<>();
            for (Object obj : pm.getDatas()) {
                BQAccount account = (BQAccount) obj;
                UserAccountVo accvo = new UserAccountVo();
                accvo.setUserId(account.getF_user_id());
                accvo.setCoinId(account.getCurrency_type_id());
                accvo.setUserEmail(account.getF_user_email());
                accvo.setAvailable(account.getBtc());
                accvo.setFrozen(account.getBtc_locked());
                accvo.setWalletAddress("");
                accvo.setBfxToUnlock(BigDecimal.ZERO);
                newList.add(accvo);
            }
            pm.setDatas(newList);
        }
        return super.url_JSP("spotManagerAccountStatistics");
    }

    /**
     * 币币挂单买入
     */
    @RequestMapping("/loadBuy")
    public String loadBuyPending(HttpServletRequest request, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        if (coinPairs != null && coinPairs.size() > 0) {

            BQSpotMarketCoinPair coinPair = coinPairs.get(0);
            PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, 0L, null, coinPair.getCoin_pair_name());// (currTran.getTransaction_code());

            mo.addAttribute("currtran", coinPair.getCoin_pair_name());
            mo.addAttribute("trans", coinPairs);
            mo.addAttribute("pm", pm);

            mo.addAttribute("dk", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_BUY)); // 买总量
            mo.addAttribute("kp", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_SELL)); // 卖总量

        }else {
            mo.addAttribute("pm",new PagerModel());
        }

        mo.addAttribute("page", "买入");
        mo.addAttribute("url", "selectBuy");
        mo.addAttribute("mapping","managerSpotRiskControl");
        return super.url_JSP("spotPendingList");
    }

    @RequestMapping("/selectBuy")
    public String selectBuyPending(String coinPairName, String userid, String useremail, HttpServletRequest request, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        BQSpotMarketCoinPair coinPair = null;
        for (BQSpotMarketCoinPair pair: coinPairs) {
            if (pair.getCoin_pair_name().equals(coinPairName)) {
                coinPair = pair;
                break;
            }
        }
        if (coinPair != null) {
            long iuserID = ConvertUtil.toLong(userid, 0L);
            if (iuserID == 0) {
                BQMember bqMember = super.findUsersByEmail(useremail);
                if (bqMember != null)
                    iuserID = bqMember.getF_user_id();
            }

            PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, iuserID, useremail, coinPair.getCoin_pair_name());
            mo.addAttribute("currtran", coinPair.getCoin_pair_name());
            mo.addAttribute("pm", pm);
            mo.addAttribute("userid", userid);
            mo.addAttribute("useremail", useremail);

            mo.addAttribute("dk", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_BUY)); // 买总量
            mo.addAttribute("kp", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_SELL)); // 卖总量

        } else {
            PagerModel pm = new PagerModel();
            pm.setTotal(0);
            mo.addAttribute("currtran", coinPairName);
            mo.addAttribute("pm", pm);
            mo.addAttribute("userid", userid);
            mo.addAttribute("useremail", useremail);
        }

        mo.addAttribute("trans", coinPairs);
        mo.addAttribute("page", "买入");
        mo.addAttribute("url", "selectBuy");
        mo.addAttribute("mapping","managerSpotRiskControl");

        return super.url_JSP("spotPendingList");
    }

    /**
     * 币币挂单卖出
     */
    @RequestMapping("/loadSell")
    public String loadSellPending(HttpServletRequest request, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        if (coinPairs != null && coinPairs.size() > 0) {

            BQSpotMarketCoinPair coinPair = coinPairs.get(0);
            PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, 0L, null, coinPair.getCoin_pair_name());

            mo.addAttribute("currtran", coinPair.getCoin_pair_name());
            mo.addAttribute("trans", coinPairs);
            mo.addAttribute("pm", pm);

            mo.addAttribute("dk", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_BUY)); // 买总量
            mo.addAttribute("kp", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_SELL)); // 卖总量

        }else {
            mo.addAttribute("pm",new PagerModel());
        }

        mo.addAttribute("page", "卖出");
        mo.addAttribute("url", "selectSell");
        mo.addAttribute("mapping","managerSpotRiskControl");

        return super.url_JSP("spotPendingList");
    }

    @RequestMapping("/selectSell")
    public String selectSellPending(String coinPairName, String userid, String useremail, HttpServletRequest request, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        BQSpotMarketCoinPair coinPair = null;
        for (BQSpotMarketCoinPair pair: coinPairs) {
            if (pair.getCoin_pair_name().equals(coinPairName)) {
                coinPair = pair;
                break;
            }
        }
        if (coinPair != null) {
            long iuserID = ConvertUtil.toLong(userid, 0L);
            if (iuserID == 0) {
                BQMember bqMember = super.findUsersByEmail(useremail);
                if (bqMember != null)
                    iuserID = bqMember.getF_user_id();
            }

            PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, iuserID, useremail, coinPair.getCoin_pair_name());
            mo.addAttribute("currtran", coinPair.getCoin_pair_name());
            mo.addAttribute("pm", pm);
            mo.addAttribute("userid", userid);
            mo.addAttribute("useremail", useremail);

            mo.addAttribute("dk", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_BUY)); // 买总量
            mo.addAttribute("kp", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_SELL)); // 卖总量

        } else {
            PagerModel pm = new PagerModel();
            pm.setTotal(0);
            mo.addAttribute("currtran", coinPairName);
            mo.addAttribute("pm", pm);
            mo.addAttribute("userid", userid);
            mo.addAttribute("useremail", useremail);
        }

        mo.addAttribute("trans", coinPairs);
        mo.addAttribute("page", "卖出");
        mo.addAttribute("url", "selectSell");
        mo.addAttribute("mapping","managerSpotRiskControl");

        return super.url_JSP("spotPendingList");
    }

    /**
     * 币币交易信息
     */
    @RequestMapping("/loadTrades")
    public String loadTrades(HttpServletRequest request, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        if (coinPairs.size() > 0) {
            String coinPairName = coinPairs.get(0).getCoin_pair_name();
            mo.addAttribute("coinPairName", coinPairName);
            mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName));
            mo.addAttribute("db", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
            mo.addAttribute("kb", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量

        } else {
            PagerModel pm = new PagerModel();
            pm.setDatas(null);
            pm.setTotal(0);
            mo.addAttribute("pm", pm);
        }
        mo.addAttribute("lstTrans", coinPairs);
        mo.addAttribute("current", 1);
        mo.addAttribute("url","managerSpotRiskControl");
        return super.url_JSP("spotTradeLists");
    }

    @RequestMapping("/selectTrades")
    public String selectTrades(HttpServletRequest request, String coinPairName, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        BQSpotMarketCoinPair coinPair = null;
        for (BQSpotMarketCoinPair pair: coinPairs) {
            if (pair.getCoin_pair_name().equals(coinPairName)) {
                coinPair = pair;
                break;
            }
        }
        if (coinPair == null) {
            return super.redirect("/managerSpotRiskControl/loadTrades.do");
        }
        mo.addAttribute("lstTrans", coinPairs);
        int icurrent=0;
        for (int i = 0; i < coinPairs.size(); i++) {
            String hy = coinPairs.get(i).getCoin_pair_name();
            if (coinPairName.equals(hy)) {
                icurrent=i+1;
            }
        }
        mo.addAttribute("current", icurrent);
        mo.addAttribute("coinPairName", coinPairName);
        mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName));
        mo.addAttribute("db", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
        mo.addAttribute("kb", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量
        mo.addAttribute("url","managerSpotRiskControl");

        return super.url_JSP("spotTradeLists");
    }

    @RequestMapping("/userTrades")
    public String userTrades(HttpServletRequest request, String coinPairName, String buyuser, String selluser, Model mo) {
        BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
        if (manager == null) {
            return super.url_ROOT("login");
        }
        List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPairManager(manager);
        Integer buyuserid = ConvertUtil.toInt(buyuser, 0);
        Integer selluserid = ConvertUtil.toInt(selluser, 0);
        if (buyuserid > 0)
            mo.addAttribute("buyuser", buyuserid);
        if (selluserid > 0)
            mo.addAttribute("selluser", selluserid);

        BQSpotMarketCoinPair coinPair = null;
        for (BQSpotMarketCoinPair pair: coinPairs) {
            if (pair.getCoin_pair_name().equals(coinPairName)) {
                coinPair = pair;
                break;
            }
        }
        if (coinPair == null) {
            return super.redirect("/managerSpotRiskControl/loadTrades.do");
        }
        mo.addAttribute("lstTrans", coinPairs);
        int icurrent=0;
        for (int i = 0; i < coinPairs.size(); i++) {
            String hy = coinPairs.get(i).getCoin_pair_name();
            if (coinPairName.equals(hy)) {
                icurrent=i+1;
            }
        }

        mo.addAttribute("current", icurrent);
        mo.addAttribute("coinPairName", coinPairName);
        mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName, buyuserid, selluserid));
        double itotal = memberTradeDetailService.findTotal(coinPairName, buyuserid, selluserid);
        mo.addAttribute("total", itotal);
        mo.addAttribute("db", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
        mo.addAttribute("kb", spotPendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量
        mo.addAttribute("url","managerSpotRiskControl");

        return super.url_JSP("spotTradeLists");
    }
}
