package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合约资产排行
 */
@Controller
@RequestMapping("/transEachRanking")
public class TransEachRanking extends BaseAction{

    @Autowired
    private BQAccountContractService bqAccountContractService;
    @Autowired
    private BQMemberService bqMemberService;

    @RequestMapping(value = "/loadFormation",method = RequestMethod.GET)
    public String loadFormation(String partition, String code, Model mo) {
        DecimalFormat df = new DecimalFormat("0.00000000");
        PagerModel pm = null;
        if (null == partition || "".equals(partition)) partition = "USDT";
        if (null == code) code = "btc";

        List<BQTransaction> transactionList = super.findCurrTrans();
        mo.addAttribute("lstTrans", transactionList);

        pm = bqAccountContractService.readRecords(code);
        @SuppressWarnings("unchecked")
        List<BQAccountContract> bqAccountContracts = (List<BQAccountContract>) pm.getDatas();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (BQAccountContract contract1 : bqAccountContracts) {
            long user_id = contract1.getF_user_id();
            BQMember bqMember = bqMemberService.findUserById(user_id);
            if (bqMember == null) {
                continue;
            }
            String user_Email = bqMember.getF_user_email();
            BigDecimal contractAmount = contract1.getF_btc();
            BigDecimal contractLockAmount = contract1.getF_lockbtc();
            BigDecimal countAll = contractAmount.add(contractLockAmount);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user_id);
            map.put("userEmail", user_Email);
            map.put("conTractName", code.toUpperCase());
            map.put("contractAmount", df.format(contractAmount));
            map.put("contractLockAmount", df.format(contractLockAmount));
            map.put("countAll", df.format(countAll));
            mapList.add(map);
        }
        pm.setDatas(mapList);
        mo.addAttribute("code", code);
        mo.addAttribute("pm", pm);
        mo.addAttribute("partition", partition);
        return super.url_JSP("transEachRanking");
    }
}
