package com.btc100.btc_q.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.vo.SendResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SendtelcodeUtil {
    /** 国内短信接口地址 */
    private final static String URL = "http://106.15.1.248:9090/sms/batch/v1";
    private final static String APPKEY = "OTEcKR";
    private final static String APPSECRET = "CQs9j7";
    private final static String APPCODE = "1000";


    /** 国际短信接口地址 */
    private final static String OUT_URL = "http://47.243.5.189:9090/sms/batch/v1";

    private final static String OUT_APPKEY = "bJlYx0";
    private final static String OUT_APPSECRET = "RabwOM";
    private final static String OUT_APPCODE = "1000";

    static String smsBody = "【Btcside】您的验证码为：%s，该验证码10分钟内有效，请勿泄露于他人。如非本人操作，请及时修改密码。";
    static String out_smsBody = "[Btcside]您的验证码为：%s，该验证码10分钟内有效，请勿泄露于他人。如非本人操作，请及时修改密码。";



    public static String sendTelCode(String zone, String telNum, int random) throws IOException {
        CloseableHttpClient httpClient = null;
        HttpPost httppost = null;
        CloseableHttpResponse httpresponse = null;
        try{
            httpClient = HttpClients.createDefault();


            String sign = "";
            Map<String,String> map = new HashMap<>();
            String timestamp = String.valueOf(System.currentTimeMillis());
            map.put("timestamp", timestamp);

            if ("86".equals(zone) || StringUtil.isEmpty(zone)) {
                httppost = new HttpPost(URL);
                map.put("appkey", APPKEY);
                map.put("appcode", APPCODE);
                sign = md5(APPKEY + APPSECRET + timestamp);
                map.put("msg", String.format(smsBody, random));
                map.put("phone", telNum);
            } else {
                httppost = new HttpPost(OUT_URL);
                map.put("appkey", OUT_APPKEY);
                map.put("appcode", OUT_APPCODE);
                sign = md5(OUT_APPKEY + OUT_APPSECRET + timestamp);
                map.put("msg", String.format(out_smsBody, random));
                map.put("phone", zone + telNum);
            }
            map.put("sign", sign);
            map.put("uid", createNonce());

            String json = JSONObject.toJSONString(map);
            StringEntity formEntity = new StringEntity(json, "utf-8");
            httppost.setEntity(formEntity);
            httpresponse = httpClient.execute(httppost);
            HttpEntity httpEntity = httpresponse.getEntity();
            String response = EntityUtils.toString(httpEntity, "utf-8");
            SendResponseVO responseVO = JSONObject.parseObject(response, SendResponseVO.class);
            if (responseVO != null) {
                if ("00000".equals(responseVO.getCode())) {
                    return "";
                }
                if ("F0016".equals(responseVO.getCode())) {
                    return "手机号码无效";
                }
                if ("F0100".equals(responseVO.getCode())) {
                    return "未知错误";
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(httppost!=null){
                httppost.abort();
            }
            if(httpClient!=null){
                httpClient.close();
            }
            if (httpresponse != null) {
                httpresponse.close();
            }
        }
        return "发送失败";
    }



    private static String md5(String param) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] md5Byte = md5.digest(param.getBytes(StandardCharsets.UTF_8));
        return byteToHex(md5Byte);
    }


    private static String byteToHex(byte[] md5Byte) {
        String result = "";
        StringBuilder sb = new StringBuilder();
        for(byte each : md5Byte){
            int value = each&0xff;
            String hex = Integer.toHexString(value);
            if(value<16){
                sb.append("0");
            }
            sb.append(hex);
        }
        result = sb.toString();
        return result;
    }

    public static int byte4ToInteger(byte[] b, int offset) {
        return (0xff & b[offset]) << 24 | (0xff & b[offset+1]) << 16 |
                (0xff & b[offset+2]) << 8 | (0xff & b[offset+3]);
    }


    /**
     * 发送手机验证码
     *
     * @param telNum
     *            手机号
     * @param random
     *            6位随机码
     * @return
     */
    public static String sendTelCode(String telNum, int random) throws IOException {
        return sendTelCode("86", telNum, random);
    }


    /**
     * 生成随机码。用于防重复提交
     */
    public static String createNonce() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
    }


    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

//        String result = sendTelCode("", "110120119", 112299);
//        System.out.println(result);

//        sendRecharge("86", "13534053406", 300 + "", "usdr");
    }


}
