package com.btc100.btc_q.ueditor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.util.CommonUtil;
import com.btc100.btc_q.util.ImageUtil;
import com.btc100.btc_q.util.OSSFileUtil;
import com.btc100.btc_q.util.StringUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;

/**
 * UEditor文件上传辅助类
 * 
 */
public class Uploader {

	// 文件大小常量, 单位kb
	private static final int MAX_SIZE = 500 * 1024;
	// 输出文件地址
	private String url = "";
	// 上传文件名
	private String fileName = "";
	// 状态
	private String state = "";
	// 文件类型
	private String type = "";
	// 原始文件名
	private String originalName = "";
	// 文件大小
	private String size = "";
	
	private String savePath = "";

	private HttpServletRequest request = null;
	private String title = "";

	// 文件允许格式
	private String[] allowFiles = {".png", ".jpg", ".jpeg" };
	// 文件大小限制，单位Byte
	private long maxSize = 0;

	private HashMap<String, String> errorInfo = new HashMap<String, String>();
	private Map<String, String> params = null;
	// 上传的文件数据
	private InputStream inputStream = null;

	public static final String ENCODEING = System.getProperties().getProperty(
			"file.encoding");

	public Uploader(HttpServletRequest request) {
		this.request = request;
		this.params = new HashMap<String, String>();

		this.setMaxSize(Uploader.MAX_SIZE);

		HashMap<String, String> tmp = this.errorInfo;
		tmp.put("SUCCESS", "SUCCESS"); // 默认成功
		// 未包含文件上传域
		tmp.put("NOFILE",
				"\\u672a\\u5305\\u542b\\u6587\\u4ef6\\u4e0a\\u4f20\\u57df");
		// 不允许的文件格式
		tmp.put("TYPE",
				"\\u4e0d\\u5141\\u8bb8\\u7684\\u6587\\u4ef6\\u683c\\u5f0f");
		// 文件大小超出限制
		tmp.put("SIZE",
				"\\u6587\\u4ef6\\u5927\\u5c0f\\u8d85\\u51fa\\u9650\\u5236");
		// 请求类型错误
		tmp.put("ENTYPE", "\\u8bf7\\u6c42\\u7c7b\\u578b\\u9519\\u8bef");
		// 上传请求异常
		tmp.put("REQUEST", "\\u4e0a\\u4f20\\u8bf7\\u6c42\\u5f02\\u5e38");
		// 未找到上传文件
		tmp.put("FILE", "\\u672a\\u627e\\u5230\\u4e0a\\u4f20\\u6587\\u4ef6");
		// IO异常
		tmp.put("IO", "IO\\u5f02\\u5e38");
		// 目录创建失败
		tmp.put("DIR", "\\u76ee\\u5f55\\u521b\\u5efa\\u5931\\u8d25");
		// 未知错误
		tmp.put("UNKNOWN", "\\u672a\\u77e5\\u9519\\u8bef");

		this.parseParams();

	}

	public void upload() throws Exception {
		
		Object sName = request.getSession().getAttribute("owner");
		BQSurecome manager = (BQSurecome)sName;
		if (sName == null||manager==null) {
			return;
		}
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
		if (!isMultipart) {
			this.state = this.errorInfo.get("NOFILE");
			return;
		}

		if (this.inputStream == null) {
			this.state = this.errorInfo.get("FILE");
			return;
		}
		
		// 存储title
		this.title = this.getParameter("pictitle");

		try {
			if (StringUtil.isEmpty(this.originalName)) {
				this.originalName = "tttt.jpg";
			}
			
			if (!this.checkFileType(this.originalName)) {
				this.state = this.errorInfo.get("TYPE");
				return;
			}

			this.type = this.getFileExt(this.originalName);
			this.fileName = CommonUtil.getUUID() + this.type;
			
			String tmpFolder = "/tmp/btcq/file-upload/ueditor/";
			File folder = new File(tmpFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			String tempFilePath = tmpFolder + this.fileName;
			FileOutputStream fos = new FileOutputStream(tempFilePath);
			BufferedInputStream bis = new BufferedInputStream(this.inputStream);
			byte[] buff = new byte[128];
			int count = -1;
			while ((count = bis.read(buff)) != -1) {
				fos.write(buff, 0, count);
			}
			bis.close();
			fos.close();
			
			File file = new File(tempFilePath);
			if (!ImageUtil.isValidFile("jpg", file) && !ImageUtil.isValidFile("png", file)) {
				file.delete();
				this.state = this.errorInfo.get("TYPE");
				return;
			}
			
			SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
			savePath = "file-upload/ueditor/" + formater.format(new Date()) + "/";
			String path = savePath + this.fileName;
			
			this.url = OSSFileUtil.uploadFile(tempFilePath, path);
			file.delete();
			
			this.state = this.errorInfo.get("SUCCESS");
			
//			SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
//			String savePath = this.savePath + "/" + formater.format(new Date());
//			if (!this.checkFileType(this.originalName)) {
//				this.state = this.errorInfo.get("TYPE");
//				return;
//			}
//
//			this.fileName = CommonUtil.getUUID() + ".jpg";
//			this.type = this.getFileExt(this.fileName);
//			this.url = savePath + "/" + this.fileName;
//			
//			String tmpFolder = "/tmp/btcq/file-upload/ueditor/";
//			File folder = new File(tmpFolder);
//			if (!folder.exists()) {
//				folder.mkdirs();
//			}
//			
//			FileOutputStream fos = new FileOutputStream(tmpFolder + this.fileName);
//			BufferedInputStream bis = new BufferedInputStream(this.inputStream);
//			byte[] buff = new byte[128];
//			int count = -1;
//			while ((count = bis.read(buff)) != -1) {
//				fos.write(buff, 0, count);
//			}
//			bis.close();
//			fos.close();
//			
//			String iconBasePath = request.getSession().getServletContext().getRealPath("/");
//			File file = new File(tmpFolder + this.fileName);
//			if (!ImageUtil.isValidFile("jpg", file) && !ImageUtil.isValidFile("png", file)) {
//				file.delete();
//				this.state = this.errorInfo.get("TYPE");
//				return;
//			}
//			
//			String saveForder = Contents.BASE_UPLOAD_PATH + "/file-upload/" + savePath;
//			folder = new File(saveForder);
//			if (!folder.exists()) {
//				folder.mkdirs();
//			}
//			fileName = ImageUtil.addWatermark(file, saveForder, iconBasePath);
//			file.delete();
//			
//			if (StringUtil.isEmpty(fileName)) {
//				this.state = this.errorInfo.get("TYPE");
//				return;
//			}
//
//			this.url = savePath + "/" + fileName.replace("\\", "");
//			this.state = this.errorInfo.get("SUCCESS");
			
		} catch (Exception e) {
			e.printStackTrace();
			this.state = this.errorInfo.get("IO");
		}

	}
	
	/**
	 * 接受并保存以base64格式上传的文件
	 * 
	 * @param fieldName
	 */
	public void uploadBase64(String fieldName) {
		Object sName = request.getSession().getAttribute("owner");
		BQSurecome manager = (BQSurecome)sName;
		if (sName == null||manager==null) {
			return;
		}
		
		String base64Data = this.request.getParameter(fieldName);
		
		Base64 decoder = new Base64();
		try {
			byte[] b = decoder.decode(base64Data);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			
			if (!ImageUtil.isValidFile("jpg", b) && !ImageUtil.isValidFile("png", b)) {
				this.state = this.errorInfo.get("TYPE");
				return;
			}
			
			SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
			savePath = "file-upload/ueditor/" + formater.format(new Date()) + "/";
			this.fileName = CommonUtil.getUUID() + ".jpg";
			this.url = OSSFileUtil.uploadFile(b,  savePath + fileName);
			this.state = this.errorInfo.get("SUCCESS");
		} catch (Exception e) {
			this.state = this.errorInfo.get("IO");
		}
	}

	public String getParameter(String name) {
		return this.params.get(name);
	}

	/**
	 * 文件类型判断
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileType(String fileName) {
		Iterator<String> type = Arrays.asList(this.allowFiles).iterator();
		while (type.hasNext()) {
			String ext = type.next();
			if (fileName.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	private String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	private void parseParams() {
		DiskFileItemFactory dff = new DiskFileItemFactory();
		try {
			ServletFileUpload sfu = new ServletFileUpload(dff);
			sfu.setSizeMax(this.maxSize);
			sfu.setHeaderEncoding(Uploader.ENCODEING);

			FileItemIterator fii = sfu.getItemIterator(this.request);

			while (fii.hasNext()) {
				FileItemStream item = fii.next();
				// 普通参数存储
				if (item.isFormField()) {

					this.params.put(item.getFieldName(),
							this.getParameterValue(item.openStream()));

				} else {

					// 只保留一个
					if (this.inputStream == null) {
						this.inputStream = item.openStream();
						this.originalName = item.getName();
						return;
					}

				}

			}

		} catch (Exception e) {
			this.state = this.errorInfo.get("UNKNOWN");
		}

	}

	/**
	 * 从输入流中获取字符串数据
	 * 
	 * @param in
	 *            给定的输入流， 结果字符串将从该流中读取
	 * @return 从流中读取到的字符串
	 */
	private String getParameterValue(InputStream in) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String result = "";
		String tmpString = null;

		try {

			while ((tmpString = reader.readLine()) != null) {
				result += tmpString;
			}

		} catch (Exception e) {
			// do nothing
		}

		return result;

	}

	@SuppressWarnings("unused")
	private byte[] getFileOutputStream(InputStream in) {

		try {
			return IOUtils.toByteArray(in);
		} catch (IOException e) {
			return null;
		}

	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void setAllowFiles(String[] allowFiles) {
		this.allowFiles = allowFiles;
	}

	public void setMaxSize(long size) {
		this.maxSize = size * 1024;
	}

	public String getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getState() {
		return this.state;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	public String getOriginalName() {
		return this.originalName;
	}
}