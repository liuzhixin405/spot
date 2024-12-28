package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/managerSpotRiskControlSetting")
public class SpotRiskControlSetting extends BaseAction{

    @Autowired
    private BQCurrencyTypeService bqCurrencyTypeService;
    @Autowired
    private BQSurecomeService bqSurecomeService;

    @RequestMapping(value = "/load")
    public String getSpotRiskControlSetting(HttpServletRequest request, Model model){
       List<BQCurrencyType> bqCurrencyTypes = bqCurrencyTypeService.findAll();
        model.addAttribute("bqCurrencyTypes",bqCurrencyTypes);
        int msgState = ConvertUtil.toInt(request.getParameter("msg"), 0);
        if (msgState == 1){
            model.addAttribute("msg","权限修改失败");
        }else if (msgState == 2){
            model.addAttribute("msg","权限修改成功");
        }
        model.addAttribute("name", request.getParameter("name"));
        PagerModel pm = bqSurecomeService.findAllModel();
        model.addAttribute("pm",pm);
        return super.url_JSP("spotRiskControlSetting");
    }

    @RequestMapping(value = "/addOrUpadateCoins")
    public String addOrUpadateCoins(HttpServletRequest request, Model model, String bqCurrencyTypes){
        BQSurecome owner = (BQSurecome)request.getSession().getAttribute("owner");
        if (owner == null) {
            return super.url_ROOT("login");
        }
        String name = request.getParameter("userName");
        BQSurecome bqSurecome = bqSurecomeService.getByName(name);
        if (null == bqSurecome) {
            model.addAttribute("msg",1);
            return "redirect:/managerSpotRiskControlSetting/load.do";
        }
        bqSurecomeService.updateCoinsByName(name, bqCurrencyTypes);
        model.addAttribute("msg",2);
        model.addAttribute("name", name);

        if (owner.getUser_id() == bqSurecome.getUser_id()) {
            owner = bqSurecomeService.getByName(owner.getUser_name());
            request.getSession().setAttribute("owner", owner);
        }
        return "redirect:/managerSpotRiskControlSetting/load.do";
    }

}
