package com.btc100.btc_q.action.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.util.CommonUtil;
import com.btc100.btc_q.util.ImageUtil;
import com.btc100.btc_q.util.StringUtil;

/**
 * 图片上传API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/fileUploadApi")
public class FileUploadApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(FileUploadApiAction.class);
	
	/**
	 * 上传图片，返回图片路径
	 * 
	 */
	@RequestMapping("/upload")
	public String upload(HttpServletRequest request, HttpServletResponse response, Model model, MultipartFile file) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		try {
			
			if (!ImageUtil.isValidFile("jpg", file.getBytes()) && !ImageUtil.isValidFile("png", file.getBytes())){
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件类型不支持（jpg或png，最大2M）");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			String fileN = file.getOriginalFilename();
			
			String ext = ".jpg";
			if (fileN.contains("png"))
				ext = ".png";
			String fileName = CommonUtil.getUUID() + ext;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String storeFolderPath = "/file-upload/custom/" + dateFormat.format(new Date()) + "/";
			String storePath = Contents.BASE_UPLOAD_PATH + storeFolderPath;
			String tempPath = Contents.TEMP_PATH + storeFolderPath;
			
			File tmpFolder = new File(tempPath);
			if (!tmpFolder.exists()) {
				tmpFolder.mkdirs();
			}
			
			File storeFolder = new File(storePath);
			if (!storeFolder.exists()) {
				storeFolder.mkdirs();
			}
			
			File tmpFile = new File(tmpFolder, fileName);
			file.transferTo(tmpFile);
			
			try {
				fileName = null;
				fileName = ImageUtil.addWatermark(tmpFile, storePath, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			tmpFile.delete();
			
			if (StringUtil.isEmpty(fileName)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件类型不支持（jpg或png，最大2M）");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			userJson.put("data", storeFolderPath + fileName);

//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String storeFolderPath = "file-upload/custom/" + dateFormat.format(new Date()) + "/";
//			
//			if (!ImageUtil.isValidFile("jpg", file.getBytes()) && !ImageUtil.isValidFile("png", file.getBytes())) {
//				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件上传异常");
//				model.addAttribute("ticker", tradesStr);
//				return apiData(response);
//			}
//
//			String url = OSSFileUtil.uploadFile(file.getBytes(), storeFolderPath + fileName);
//			userJson.put("data", url);
			
		} catch (Exception e) {
			logger.error("文件上传异常，详情：", e);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件上传异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 上传图片（base64），返回图片路径
	 * 
	 */
	@RequestMapping("/uploadBase64")
	public String uploadBase64(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		try {
			String base64Data = request.getParameter("file");
			String imgType = base64Data.substring(0, base64Data.indexOf(','));
			String ext = ".jpg";
			if (imgType.contains("png"))
				ext = ".png";
			base64Data = base64Data.substring(base64Data.indexOf(',') + 1);
			Base64 decoder = new Base64();
			String fileName = CommonUtil.getUUID() + ext;
			
			byte[] f = decoder.decode(base64Data);
			for (int i = 0; i < f.length; ++i) {
				if (f[i] < 0) {
					f[i] += 256;
				}
			}
			
			if (!ImageUtil.isValidFile("jpg", f) && !ImageUtil.isValidFile("png", f)){
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件类型不支持（jpg或png，最大2M）");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} 
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String storeFolderPath = "/file-upload/custom/" + dateFormat.format(new Date()) + "/";
			String storePath = Contents.BASE_UPLOAD_PATH + storeFolderPath;
			String tempPath = Contents.TEMP_PATH + storeFolderPath;
			
			File tmpFolder = new File(tempPath);
			if (!tmpFolder.exists()) {
				tmpFolder.mkdirs();
			}
			
			File storeFolder = new File(storePath);
			if (!storeFolder.exists()) {
				storeFolder.mkdirs();
			}
			
			File tmpFile = new File(tmpFolder, fileName);
			OutputStream outputStream = new FileOutputStream(tmpFile);
			outputStream.write(f);
			outputStream.flush();
			outputStream.close();			
			
			try {
				fileName = null;
				fileName = ImageUtil.addWatermark(tmpFile, storePath, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			tmpFile.delete();
			
			if (StringUtil.isEmpty(fileName)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件类型不支持（jpg或png，最大2M）");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			userJson.put("data", storeFolderPath + fileName);
			
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String storeFolderPath = "file-upload/custom/" + dateFormat.format(new Date()) + "/";
//			
//			byte[] f = decoder.decode(base64Data);
//			for (int i = 0; i < f.length; ++i) {
//				if (f[i] < 0) {
//					f[i] += 256;
//				}
//			}
//			
//			if (!ImageUtil.isValidFile("jpg", f) && !ImageUtil.isValidFile("png", f)) {
//				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件上传异常");
//				model.addAttribute("ticker", tradesStr);
//				return apiData(response);
//			}
//
//			String url = OSSFileUtil.uploadFile(f, storeFolderPath + fileName);
//			userJson.put("data", url);
		} catch (Exception e) {
			logger.error("文件上传异常，详情：", e);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "文件上传异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
}
