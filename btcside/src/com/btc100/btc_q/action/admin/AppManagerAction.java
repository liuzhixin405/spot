package com.btc100.btc_q.action.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQApp;
import com.btc100.btc_q.service.BQAppService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping(value = "/managerApp")
public class AppManagerAction extends BaseAction {

	@Autowired
	private BQAppService bqAppService;
	
	@RequestMapping(value = "/appManager")
	public String getApp(Model mo, String platform) {
		List<BQApp> list = bqAppService.readAll();
		mo.addAttribute("list", list);
		BQApp app = bqAppService.readByPlatform(platform);
		if (app == null) {
			app = new BQApp();
		}
		mo.addAttribute("app", app);
		
		return super.url_JSP("appManager");
	}
	
	@RequestMapping(value = "/updateApp", method = RequestMethod.POST)
	public String updateApp(String name, String platform, String version, String download_url, String note, Model mo) {
		
		BQApp app = bqAppService.readByPlatform(platform);
		if (app == null) {
			app = new BQApp();
		}
		
		if (StringUtil.isEmpty(download_url)) {
			return super.errorPage(mo, "请先上传安装包");
		}
		
		if (StringUtil.isEmpty(name) || StringUtil.isEmpty(version)) {
			return super.errorPage(mo, "名称或版本号不能为空");
		}
		
		if ("Android".equals(platform)) {
			if (!download_url.contains("apk")) {
				return super.errorPage(mo, "Android版本更新需上传apk格式文件");
			}
		} else {
			if (!download_url.contains("plist")) {
				return super.errorPage(mo, "IOS版本更新需上传ipa格式文件");
			}
		}
		app.setName(name);
		app.setPlatform(platform);
		app.setVersion(version);
		app.setNote(note);
		app.setDownload_url(download_url);
		
		bqAppService.update(app);
		
		return super.redirect("/managerApp/appManager.do");
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(@RequestParam(value = "files", required = false) MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {

		JSONObject json = new JSONObject();
		json.put("code", "success");
		json.put("msg", "");
		// 获取文件全名
		String uploadFileName = file.getOriginalFilename();
		// 获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
		String ext = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1, uploadFileName.length());
		// 对扩展名进行小写转换
		ext = ext.toLowerCase();
		
		String name = uploadFileName.substring(0, uploadFileName.length() - ext.length() - 1).toLowerCase();

		// 文件限制
		// 定义一个数组，用于保存可上传的文件类型
		List<String> fileTypes = new ArrayList<String>();
		fileTypes.add("apk");
		fileTypes.add("ipa");

		if (!fileTypes.contains(ext) || uploadFileName.contains("%00")) { // 如果扩展名属于允许上传的类型，则创建文件
			json.put("code", "error");
			json.put("msg", "文件格式不符");
			toWeb(response, json);
		}

		long timeStamp = System.currentTimeMillis() / 1000;
		String fileName = name + "_" + timeStamp + "." + ext;
		String targetFilePath = "/file-upload/app/android/";
		String baseStorePath = Contents.BASE_UPLOAD_PATH;
		if ("ipa".equals(ext)) {
			targetFilePath = "/file-upload/app/ios/";
		}
		String targetPath = baseStorePath + targetFilePath + fileName;
		File targetFile = new File(targetPath);
		File targetParentFile = targetFile.getParentFile();
		if (!targetParentFile.exists()) {
			targetParentFile.mkdirs();
		}

		File wFile = new File(targetParentFile, fileName);
		try {
			file.transferTo(wFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			json.put("code", "error");
			json.put("msg", "上传文件失败");
			toWeb(response, json);
		}
		
		String webRoot = super.getRequestRoot(request);
		if (webRoot.endsWith("/")) {
			webRoot = webRoot.substring(0, webRoot.length() - 1);
		}
		
		String downloadUrl = webRoot + targetFilePath + fileName;
		if ("ipa".equals(ext)) {
			downloadUrl = createPlist(targetFilePath, name + "_" + timeStamp, request);
			if (StringUtil.isEmpty(downloadUrl)) {
				json.put("code", "error");
				json.put("msg", "上传ipa文件失败");
				toWeb(response, json);
			}
		}
		json.put("data", downloadUrl);
		toWeb(response, json);
	}
	
	public String createPlist(String targetFilePath, String time, HttpServletRequest request) {
		String title = "BFX";
		String bundleId = "com.taiyiyun.bfx";
		String versionCode = "1.0.0";
		
		String ipaUrl = super.getRequestRoot(request) + targetFilePath + time + ".ipa";
		String targetPath = Contents.BASE_UPLOAD_PATH + targetFilePath + time + ".plist";
		File file = new File(targetPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

			}
		}
		String plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
				+ "<plist version=\"1.0\">\n"
				+ "\t<dict>\n"
				+ "\t\t<key>items</key>\n"
				+ "\t\t<array>\n"
				+ "\t\t\t<dict>\n"
				+ "\t\t\t\t<key>assets</key>\n"
				+ "\t\t\t\t<array>\n"
				+ "\t\t\t\t\t<dict>\n"
				+ "\t\t\t\t\t\t<key>kind</key>\n"
				+ "\t\t\t\t\t\t<string>software-package</string>\n"
				+ "\t\t\t\t\t\t<key>url</key>\n"
				// 之前所上传的ipa文件路径
				+ "\t\t\t\t\t\t<string>" + "" + ipaUrl + "</string>\n"
				+ "\t\t\t\t\t</dict>\n"
				+ "\t\t\t\t</array>\n"
				+ "\t\t\t\t<key>metadata</key>\n"
				+ "\t\t\t\t<dict>\n"
				+ "\t\t\t\t\t<key>bundle-identifier</key>\n"
				// bundleId:com.taiyiyun.bfx
				+ "\t\t\t\t\t<string>" + bundleId + "</string>\n"
				+ "\t\t\t\t\t<key>bundle-version</key>\n"
				// 版本号
				+ "\t\t\t\t\t<string>" + versionCode + "</string>\n"
				+ "\t\t\t\t\t<key>kind</key>\n"
				+ "\t\t\t\t\t<string>software</string>\n"
				+ "\t\t\t\t\t<key>title</key>\n"
				// title
				+ "\t\t\t\t\t<string>" + title + "</string>\n"
				+ "\t\t\t\t</dict>\n"
				+ "\t\t\t</dict>\n"
				+ "\t\t</array>\n"
				+ "\t</dict>\n"
				+ "</plist>";
		try {
			FileOutputStream output = new FileOutputStream(file);
			OutputStreamWriter writer;
			writer = new OutputStreamWriter(output, "UTF-8");
			writer.write(plist);
			writer.close();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		String webRoot = super.getRequestRoot(request);
		if (webRoot.endsWith("/")) {
			webRoot = webRoot.substring(0, webRoot.length() - 1);
		}
		
		return webRoot + targetFilePath + time + ".plist";
	}
	
}
