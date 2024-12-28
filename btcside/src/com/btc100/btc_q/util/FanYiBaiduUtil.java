package com.btc100.btc_q.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度翻译工具类
 */
public class FanYiBaiduUtil {

    private static final String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final String appId = "20210930000961100";
    private static final String secretKey = "kur4IFYn15z22MA64QKe";

    public static final String ZH = "zh";
    public static final String CHT = "cht"; //繁体
    public static final String EN = "en";
    public static final String JP = "jp";
    public static final String KOR = "kor"; //韩语


    public static String translate(String query, String from, String to) {
        String random10 = RandomUtil.getRandomString(10);
        String sign = "";
        try {
            sign = MD5Service.encryptString(appId + query + random10 + secretKey).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appId);
        params.put("salt", random10);
        params.put("sign", sign);

        String str = getUrlWithQueryString(params);
        String strResult = "";
        try {
            strResult = HttpToServer.sendGet(url, str);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            return msg;
        }

        if (!StringUtil.isEmpty(strResult)) {
            return strResult;
        }

        return "";
    }

    /**
     * 拼接参数，且百度翻译要求编码格式
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(Map<String, String> params) {
        if (params == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) {
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));  //编码

            i++;
        }

        return builder.toString();
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }






}
