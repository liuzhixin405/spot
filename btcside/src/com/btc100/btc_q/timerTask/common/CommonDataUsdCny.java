package com.btc100.btc_q.timerTask.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;

/**
 * 实时汇率
 * 
 * @author Administrator
 * 
 */
public class CommonDataUsdCny implements Serializable {
	
	private static final long serialVersionUID = -7873273049121759169L;

	/**
	 * 获取sina或者yahoo的实时汇率
	 * @param lTime
	 * @return
	 */
	public BigDecimal findUsdRate(long lTime) {
		String huobiUsd = findHuobiUsd();
		BigDecimal sinarate = new BigDecimal(ConvertUtil.toDouble(huobiUsd, 0.0));
		return sinarate.setScale(4, RoundingMode.HALF_UP);
	}

	/**
	 * 获取新浪财经的汇率
	 * @param time
	 * @return
	 */
	@SuppressWarnings("unused")
	private String findSinaUsd(long time){
		String strResult = HttpToServer.sendGetForHttp(UrlConfig.getValue("strApiSina"), "rn=" + time + "&list=USDCNY");
		String[] strs = strResult.split(",");
		return strs[1];
	}
	
	private String findHuobiUsd() {
		String strResult = HttpToServer.sendGet(UrlConfig.getValue("strApiHuobi"), "");
		if (!StringUtil.isEmpty(strResult)) {
			JSONObject obj = JSONObject.parseObject(strResult);
			if (obj.getBoolean("success") && obj.getInteger("code") == 200) {
				JSONObject data = obj.getJSONObject("data");
				JSONArray detail = data.getJSONArray("detail");
				String price = "0";
				for (int i = 0; i < detail.size(); i++) {
					JSONObject coin = detail.getJSONObject(i);
					if ("USDT".equalsIgnoreCase(coin.getString("coinName"))) {
						price = coin.getString("sell");
						break;
					}
				}
				
				return price;
			}
		}
		
		return "";
	}
	
	/**
	 * 获取雅虎财经的汇率
	 * @return
	 */
	@SuppressWarnings("unused")
	private String findYahooUsd(){
		Document doc = getResultInfo(UrlConfig.getValue("strApiYahoo"));
		String strResult = doc.asXML();
		if (StringUtil.isEmpty(strResult)) {
			return "";
		}
		Element rootElt = doc.getRootElement();
		for (Iterator iterator = rootElt.elementIterator("resources"); iterator.hasNext();) {
			Element type = (Element) iterator.next();
			Iterator iters = type.elementIterator("resource"); // 获取子节点head下的子节点script
			while (iters.hasNext()) {
				Element itemEle = (Element) iters.next();
				String field = itemEle.elementTextTrim("field"); // 拿到head下的子节点script下的字节点username的值
				if (field.equals("USD/CNY")) {
					List<Element> lsts = itemEle.elements();
					return (lsts.get(1)).getText();
				}
			}
		}
		return "";
	}
	
	/**
     * 根据url获得服务器上返回的xml
     * @param url
     * @return
     */
	public static Document getResultInfo(String url){
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        Document doc = null;
        try{
            HttpResponse response = httpClient.execute(get);
            // 先从响应头得到实体
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                // 得到实体输入流
                InputStream inSm = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(inSm, "UTF-8"));
                String xmlString = "";
                for (String temp = br.readLine(); temp != null; xmlString += temp, temp = br.readLine());
                // 去除字符串中的换行符，制表符，回车符。
                InputStream stream2 = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));

                SAXReader saxReader = new SAXReader(); 
                saxReader.setEncoding("UTF-8");
                doc = (Document) saxReader.read(new InputSource(stream2));
//                System.out.println(doc.asXML());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return doc;
    }

}
