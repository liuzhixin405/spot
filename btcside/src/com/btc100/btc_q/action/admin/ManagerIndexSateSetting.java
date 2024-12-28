package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.entity.BQIndexState;
import com.btc100.btc_q.service.BQIndexStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/managerIndexSateSetting")
public class ManagerIndexSateSetting extends BaseAction{

    @Autowired
    private BQIndexStateService bqIndexStateService;

    @RequestMapping(value = "/load")
    public String load(Model model){
        BQIndexState iState = bqIndexStateService.read();
        model.addAttribute("iState",iState);
        return super.url_JSP("indexStateSetting");
    }

    @RequestMapping("/updateState")
    public String updateState(HttpServletRequest request, Model model){
        String indexName = request.getParameter("indexName");
        String firState = request.getParameter("state");
        int state= -1;
        if (null != firState){
            state = Integer.parseInt(firState);
        }
        if("".equals(indexName) || null == indexName) return super.url_JSP("indexStateSetting");
        if (0 != state && 1 != state) return super.url_JSP("indexStateSetting");

        bqIndexStateService.updateStateByName(indexName, state);
        return "redirect:/managerIndexSateSetting/load.do";
    }


}
