package com.btc100.btc_q.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.btc100.btc_q.common.Convert;

public class HttpToServer {
	
	public static final String METHOD_POST = "POST";
	
	public static final String METHOD_GET = "GET";
	
	public static final String METHOD_DELETE = "DELETE";
	
	private static final Logger logger = Logger.getLogger(HttpToServer.class);
	
	public static String sendGetAutoChange(String url, String param) {
		if(url.contains("https:")){
			return sendGet(url, param);
		}else{
			return sendGetForHttp(url, param);
		}
	}
	
	public static String sendGetForHttp(String url, String param) {
		return sendGetForHttp(url, param, 10000, 20000);
	}
	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGetForHttp(String url, String param, int connectTime, int readTime) {
		String result = "";
        BufferedReader in = null;
        try {
			String urlNameString = "";
			if (StringUtil.isEmpty(param))
				urlNameString = url;
			else
				urlNameString = url + "?" + param;

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.0.1471.813 Safari/537.36");
            connection.setConnectTimeout(connectTime);
            connection.setReadTimeout(readTime);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
//            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;

	}
	
	public static String sendGetError(String method, String url, String data, String param, Map<String, String> heads) throws Exception {
		StringBuffer sb_res = new StringBuffer();
		BufferedReader in = null;
		URLConnection connection = null;
		String urlNameString = "";
		
		try {
			if (StringUtil.isEmpty(param))
				urlNameString = url;
			else
				urlNameString = url + "?" + param;
			
			URL realUrl = new URL(urlNameString);
			
			trustAllHosts();
			
			HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
			https.setRequestMethod(method);
			if (realUrl.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				connection = https;
			} else {
				// 打开和URL之间的连接
				connection = realUrl.openConnection();
			}

			// 设置通用的请求属性
			
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.0.1471.813 Safari/537.36");
			connection.setReadTimeout(20*1000);
			connection.setConnectTimeout(5000);
			if (!StringUtil.isEmpty(data)) {
				connection.setRequestProperty("Content-Length", "" + data.length());
				connection.setRequestProperty("Content-Type", "application/json");
			}
			
			if (heads != null) {
				for (String key : heads.keySet()) {
					connection.setRequestProperty(key, heads.get(key));
				}
			}
			
			// 建立实际的连接
			connection.connect();
			
			OutputStreamWriter out = null;
			if (!StringUtil.isEmpty(data)) {
				out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
				out.write(data);
				out.flush();
			}
			
			// 获取所有响应头字段
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb_res.append(line);
			}
		} catch (Exception e) {
			throw e;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("关闭GET请求输入流出现异常！", e2);
			}
		}
		return sb_res.toString();
	}
	
	public static String sendGetErrorForBT(String method, String url, String data, String param, Map<String, String> heads) throws Exception {
		StringBuffer sb_res = new StringBuffer();
		BufferedReader in = null;
		URLConnection connection = null;
		String urlNameString = "";
		
		try {
			if (StringUtil.isEmpty(param))
				urlNameString = url;
			else
				urlNameString = url + "?" + param;
			
			URL realUrl = new URL(urlNameString);
			
			trustAllHosts();
			
			HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
			https.setRequestMethod(method);
			if (realUrl.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				connection = https;
			} else {
				// 打开和URL之间的连接
				connection = realUrl.openConnection();
			}
			
			// 设置通用的请求属性
			
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.0.1471.813 Safari/537.36");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setReadTimeout(200*1000);
			connection.setConnectTimeout(5000);
			if (!StringUtil.isEmpty(data)) {
				connection.setRequestProperty("Content-Length", "" + data.length());
			}
			
			if (heads != null) {
				for (String key : heads.keySet()) {
					connection.setRequestProperty(key, heads.get(key));
				}
			}
			
			// 建立实际的连接
			connection.connect();
			
			OutputStreamWriter out = null;
			if (!StringUtil.isEmpty(data)) {
				out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
				out.write(data);
				out.flush();
			}
			
			// 获取所有响应头字段
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb_res.append(line);
			}
		} catch (Exception e) {
			throw e;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("关闭GET请求输入流出现异常！", e2);
			}
		}
		return sb_res.toString();
	}
	
	public static String sendGetError(String method, String url, String data, String param) throws Exception {
		return sendGetError(method, url, data, param, null);
	}

	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		StringBuffer sb_res = new StringBuffer();
		BufferedReader in = null;
		URLConnection connection = null;
		String urlNameString = "";
		
		try {
			if (StringUtil.isEmpty(param))
				urlNameString = url;
			else
				urlNameString = url + "?" + param;
			
			URL realUrl = new URL(urlNameString);
			
			trustAllHosts();
			
			HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
			if (realUrl.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				connection = https;
			} else {
				// 打开和URL之间的连接
				connection = realUrl.openConnection();
			}

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setReadTimeout(20*1000);
			connection.setConnectTimeout(5000);
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
//			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
//				result += line;
				sb_res.append(line);
			}
		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("发送GET请求出现异常：" + e.getMessage());
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("关闭GET请求输入流出现异常！", e2);
			}
		}
		return sb_res.toString();
	}
	
	
	public static String sendGetByLength(String url, String param,long time) {
		StringBuffer sb_res = new StringBuffer();
		BufferedReader in = null;
		URLConnection connection = null;
		try {
			String urlNameString = "";
			if (StringUtil.isEmpty(param))
				urlNameString = url;
			else
				urlNameString = url + "?" + param;
			
			URL realUrl = new URL(urlNameString);
			
			trustAllHosts();
			
			HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
			if (realUrl.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				connection = https;
			} else {
				// 打开和URL之间的连接
				connection = realUrl.openConnection();
			}

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setConnectTimeout(10000);
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
//			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
			// 定义 BufferedReader输入流来读取URL的响应
			
			InputStream isr=connection.getInputStream();
			byte[] data=new byte[10240];
//			List list = new ArrayList();
//			List<Byte> list=new ArrayList<Byte>();
			StringBuilder sbBuilder=new StringBuilder();
//			int ioff=0;
			while(isr.read(data, 0, 10240)>=0){
				//list.addAll(data);
//				String str = new String(data).trim();
				String s=new String(data).trim();
				if(s.length()==0){
					try{
					Thread.sleep(10);
					}catch (Exception e) {}
					continue;
				}
				sbBuilder.append(s);

				String str=sbBuilder.toString();
				int index = str.lastIndexOf("\"date\":\"");
				
				if(str.length()>index+18){
					String stime = str.substring(index+8,index+18);
					long itime = Convert.toLong(stime,0l);
					if(itime-time<0){
						sbBuilder=new StringBuilder();
					}else{
						String line="";
						in = new BufferedReader(new InputStreamReader(isr));
						while ((line = in.readLine()) != null) {
							sbBuilder.append(line);
						}
						
						break;
					}
				}
			}
			
			String str=sbBuilder.toString();
			int Index=str.indexOf("{");
			sb_res.append("[");
			sb_res.append(str.substring(Index,str.length()));
			
			
//			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//			String line;
			
//			in.read(cbuf, off, len)
			
//			while ((line = in.readLine()) != null) {
//				result += line;
//				sb_res.append(line);
//			}
		} catch (Exception e) {
			// System.out.println("发送GET请求出现异常！" + e);
			logger.error("发送GET请求出现异常！", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("关闭GET请求输入流出现异常！", e2);
			}
		}
		return sb_res.toString();
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setConnectTimeout(10000);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送 POST 请求出现异常", e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("关闭POST请求输出流、输入流出现异常！", ex);
			}
		}
		return result;
	}
	
	public static String httpGet(String httpUrl) {
		BufferedReader input = null;
		StringBuilder sb = null;
		URL url = null;
		HttpURLConnection con = null;
		try {
			url = new URL(httpUrl);
			try {
				// trust all hosts
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
				if (url.getProtocol().toLowerCase().equals("https")) {
					https.setHostnameVerifier(DO_NOT_VERIFY);
					con = https;
				} else {
					con = (HttpURLConnection)url.openConnection();
				}
				https.setConnectTimeout(10000);
				input = new BufferedReader(new InputStreamReader(con.getInputStream()));
				sb = new StringBuilder();
				String s;
				while ((s = input.readLine()) != null) {
					sb.append(s).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} finally {
			// close buffered
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// disconnecting releases the resources held by a connection so they may be closed or reused
			if (con != null) {
				con.disconnect();
			}
		}

		return sb == null ? null : sb.toString();
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
													public boolean verify(String hostname, SSLSession session) {
														return true;
													}
												};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		//final String TAG = "trustAllHosts";
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				// Auto-generated method stub
				
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				// Auto-generated method stub
			}

			
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// 发送 GET 请求
		String s = HttpToServer.sendGet("https://www.okcoin.cn/api/ticker.do", "");
		System.out.println(s);
		//s = HttpToServer.httpGet("https://www.btc100.com/api/trades?symbol=btc");
		//System.out.println(s);
		
		s = HttpToServer.sendGet("https://www.btc100.com/api/trades", "symbol=btc");
		System.out.println(s);
		// 发送 POST 请求
//		String sr = HttpToServer.sendPost("http://localhost:6144/Home/RequestPostString", "key=123&v=456");
//		System.out.println(sr);
	}
}