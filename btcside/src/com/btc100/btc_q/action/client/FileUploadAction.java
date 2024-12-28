package com.btc100.btc_q.action.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.util.CommonUtil;
import com.btc100.btc_q.util.ImageUtil;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/file")
public class FileUploadAction extends BaseAction {

	private static final Logger logger = Logger.getLogger(FileUploadAction.class);
	
	private final static String JPG = "ffd8ff";
    
	private final static String PNG = "89504e47";
	
	private final static Map<String, String> fileTypes = new HashMap<String, String>();
    
    static {
        fileTypes.put("jpg", JPG);
        fileTypes.put("png", PNG);
    }

	@RequestMapping("/uploadWorkOrder")
	public void uploadImg(HttpServletRequest request, HttpServletResponse response, String tag) {
		Contents.Local.LOCALE.set(getLocale(request));
		if (!StringUtil.isEmpty(tag) && "user".equals(tag)) {
			BQMember user = super.findCurrUser(request, false);
			if (user == null) {
				return;
			}
		}

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
				toWeb(response, "error");
				return;
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
				toWeb(response, "error");
				return;
			}
			
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String storeFolderPath = "file-upload/custom/workOrder/" + dateFormat.format(new Date()) + "/";
//			
//			byte[] f = decoder.decode(base64Data);
//			for (int i = 0; i < f.length; ++i) {
//				if (f[i] < 0) {
//					f[i] += 256;
//				}
//			}
//			
//			if (!ImageUtil.isValidFile("jpg", f) && !ImageUtil.isValidFile("png", f)) {
//				toWeb(response, "error");
//				return;
//			}
//			
//			String url = OSSFileUtil.uploadFile(f,  storeFolderPath + fileName);
//			toWeb(response, url); 

			toWeb(response, storeFolderPath + fileName);
			
		} catch (Exception e) {
			logger.error("文件上传异常，详情：", e);
			toWeb(response, "error");
			return;
		}
	}
	
	/**
     * 获取文件头
     * @param filepath
     * @return
     * @throws IOException 
     */
    public static String getFileHeader(File file) throws IOException {
         
        FileInputStream input = new FileInputStream(file);
         
        byte[] buffer = new byte[4];
        input.read(buffer, 0, buffer.length);
        input.close();
         
        return new String(Hex.encodeHex(buffer));
    }
     
    /**
     * 验证文件头类型是否合法
     * @param fileType
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isValidFile(String fileType, File file) throws IOException {
        String fileHeader = getFileHeader(file);
        String fileTypeHeader = fileTypes.get(fileType);
         
        if (fileHeader == null || fileTypeHeader == null) {
            return false;
        }
         
        if (fileHeader.toLowerCase().startsWith(fileTypeHeader)) {
            return true;
        }
        return false;
    }

}
