package com.btc100.btc_q.action.client;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.btc100.base.BaseAction;


@Controller
@RequestMapping("/survey")
public class SurveyAction extends BaseAction {

	@RequestMapping("survey")
	public String Survey(HttpServletRequest request){
		return "jsp/survey";
	}

	
	@RequestMapping("surveydo")
	public String surveydo(HttpServletRequest request,Model model) {
		return "";
//		String num = request.getParameter("numarray");
//		String[] numarray=num.split(",");
//		int num1=0;
//		for(int i=0;i<numarray.length;i++){
//			String sub=numarray[i].substring(0, 4);//前四位
//			String last=numarray[i].substring(numarray[i].length()-1,numarray[i].length());//最后一位
//			if(sub.equals("num1")){
//				if(last.equals("1")){
//					model.addAttribute("numarr1", "不要把鸡蛋放在一个篮子");
//					model.addAttribute("num1", "");
//					num1=num1+18;
//				}
//				if(last.equals("2")){
//					model.addAttribute("numarr1", "不要把鸡蛋放在一个篮子");
//					model.addAttribute("num1", "");
//					num1=num1+19;
//				}
//				if(last.equals("3")){
//					model.addAttribute("num1", "合理的配置资产");
//					model.addAttribute("numarr1", "");
//					num1=num1+20;
//				}
//			}
//			if(sub.equals("num3")){
//				if(last.equals("1")){
//					num1=num1+18;
//				}
//				if(last.equals("2")){
//					num1=num1+19;
//				}
//				if(last.equals("3")){
//					num1=num1+20;
//				}
//			}
//			if(sub.equals("num4")){
//				if(last.equals("1")){
//					model.addAttribute("numarr2", "不要受到一些所谓的消息影响到您的交易决策");
//					model.addAttribute("num4", "");
//					num1=num1+18;
//				}
//				if(last.equals("2")){
//					model.addAttribute("numarr2", "不要受到一些所谓的消息影响到您的交易决策");
//					model.addAttribute("num4", "");
//					num1=num1+19;
//				}
//				if(last.equals("3")){
//					model.addAttribute("num4", "严格按照自己的交易习惯进行操作");
//					model.addAttribute("numarr2", "");
//					num1=num1+20;
//				}
//			}
//			if(sub.equals("num5")){
//				if(last.equals("1")){
//					num1=num1+18;
//				}
//				if(last.equals("2")){
//					num1=num1+19;
//				}
//				if(last.equals("3")){
//					num1=num1+20;
//				}
//			}
//			if(sub.equals("num6")){
//				if(last.equals("1")){
//					model.addAttribute("numarr3", "多学习相关知识，培养自己的交易纪律");
//					model.addAttribute("num6", "");
//					num1=num1+18;
//				}
//				if(last.equals("2")){
//					model.addAttribute("numarr3", "多学习相关知识，培养自己的交易纪律");
//					model.addAttribute("num6", "");
//					num1=num1+19;
//				}
//				if(last.equals("3")){
//					model.addAttribute("numarr3", "");
//					model.addAttribute("num6", "操作严谨");
//					num1=num1+20;
//				}
//			}
//			if(sub.equals("num7")){
//				if(last.equals("1")){
//					model.addAttribute("num7", "稳健型");
//				}
//				if(last.equals("2")){
//					model.addAttribute("num7", "风险偏好型");
//				}
//				if(last.equals("3")){
//					model.addAttribute("num7", "风险厌恶型");
//				}
//			}
//		}
//		model.addAttribute("num", num1);
//		return "jsp/survey-success";
	}

}
