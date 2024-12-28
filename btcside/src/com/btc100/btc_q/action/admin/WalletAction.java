package com.btc100.btc_q.action.admin;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberWalletService;
import com.btc100.btc_q.service.BQWalletQueueBtcService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerWallet")
public class WalletAction extends BaseAction {
	@Autowired
	BQWalletQueueBtcService bqWalletQueueBtcService;
	@Autowired
	BQMemberWalletService bqMemberWalletService;

	@RequestMapping("/loadRecords")
	public String load(String curr, HttpServletRequest request, Model mo) {
		// bqWalletQueueBtcService.deleteById(1);
		int record = ConvertUtil.toInt(curr, 1);
		PagerModel pm = null;
		switch (record) {
		case 2:
			pm = bqWalletQueueBtcService.findAll(Contents.TRADE_CURRENCY_TYPE_ID);
			break;
		default:
			pm = bqMemberWalletService.readAll(Contents.TRADE_CURRENCY_TYPE_ID);
			break;
		}

		mo.addAttribute("pm", pm);
		mo.addAttribute("curr", record);

		return super.url_JSP("rechargeaddresslist");
	}

	@RequestMapping("/updateRecord")
	public String update(int curr, HttpServletRequest request, Model mo) throws ServiceException {

		mo.addAttribute("curr", curr);

		return super.redirect("/managerRecharge/loadRecords.do");// ("rechargelist");
	}

	/*
	@RequestMapping("/addRecord")
	public String add(@RequestParam(value = "file", required = false) MultipartFile file, String curr, HttpServletRequest request,
			HttpServletResponse response, Model mo) throws ServiceException, UnsupportedEncodingException, IOException, WriterException, JSONException {
		if (file == null || file.isEmpty() || file.getName() == null) {
			return super.errorPage(mo, "请选择地址文件!");
		}

		String fileName = file.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String fileType = fileName.substring(index);
		if (fileType == null || !(fileType.equalsIgnoreCase(".txt") || fileType.equalsIgnoreCase(".csv"))) {
			return super.errorPage(mo, "请选择txt或者csv文件!");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.equalsIgnoreCase("text/plain")) {
			return super.errorPage(mo, "请选择有效的txt或者csv文件!");
		}

		String sname = "/file-upload/walletaddress/btc";
		String realpath = Contents.BASE_UPLOAD_PATH + sname;
		String dbName = sname;

		File pfile = new File(realpath, fileName);
		if (!pfile.exists()) {
			pfile.mkdirs();
		}

		String sfilecontentString = new String(file.getBytes(), "utf-8");

		String[] strs = sfilecontentString.split("\r\n");
		int width = 132;
		int height = 132;
		String format = "jpg";

		List<BQWalletQueueBtc> list = new ArrayList<BQWalletQueueBtc>();

		int icount = 0, isuccess = 0;

		for (String str : strs) {
			// 插入比特幣數據庫中
			// String sFilePath = realpath + "/t_" + str + ".jpg";
			String sDBPath = dbName + "/t_" + str + ".jpg";
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			// 内容所使用编码
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			// 生成表
			File pFile = new File(realpath, "/t_" + str + ".jpg");
			BitMatrix ma = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(ma, format, pFile);

			BQWalletQueueBtc pBtc = new BQWalletQueueBtc();
			pBtc.setWallet_address(str);
			pBtc.setWallet_pic(sDBPath);
			list.add(pBtc);
		}

		Map<String, Object> map = bqWalletQueueBtcService.saveList(list);
		icount += ConvertUtil.toInt(map.get("total"), 0);
		isuccess += ConvertUtil.toInt(map.get("success"), 0);
		list.clear();

		// 保存
		// try {
		// file.transferTo(targetFile);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// mo.addAttribute("fileUrl", request.getContextPath() + "/upload/" +
		// fileName);

		mo.addAttribute("curr", curr);
		JSONObject jo = new JSONObject();
		jo.put("total", icount);
		jo.put("success", isuccess);
		super.toWeb(response, jo.toString());

		return super.redirect("/managerWallet/loadRecords.do");// ("rechargelist");
	}

	// public String importBTCWalletAddress() throws Exception {
	//
	// FileInputStream fs = new FileInputStream(uploadFile);
	// Workbook workbook = new HSSFWorkbook(fs);
	// // 讀取Excel Sheet頁
	// Sheet sheet = workbook.getSheetAt(0);
	// int rowNum = sheet.getLastRowNum();// 行數
	// // 不是空表的情況下,空表返回錯誤
	// if (rowNum <= 0)
	// return Action.ERROR;
	//
	// // 列數
	// int cellNum = sheet.getRow(0).getLastCellNum();
	//
	// String syspath =
	// ServletActionContext.getServletContext().getRealPath("/");
	// String btcpath = "/FileUpload/addressQR/btc/";
	// File path = new File(syspath + btcpath);
	// if (!path.exists()) {
	// path.mkdirs();
	// }
	//
	// // 讀取每一行
	// for (int i = 0; i < rowNum; i++) {
	// Row row = sheet.getRow(i);// 當前行
	// if (cellNum >= 2) {
	// Cell cell = row.getCell(1);// 第二列是地址字段
	// cell.setCellType(Cell.CELL_TYPE_STRING);
	// if (cell != null) {
	// String ssString = cell.getStringCellValue();
	// // 插入比特幣數據庫中
	// String sFilePath = btcpath + "t_" + ssString + ".jpg";
	// mimportAddressService.hinsertBTC(ssString, sFilePath);
	// // 生成表
	// String sQRPath = syspath + sFilePath;
	// File pFile = new File(sQRPath);
	//
	// int width = 132;
	// int height = 132;
	// // 二维码的图片格式
	// String format = "jpg";
	// Hashtable hints = new Hashtable();
	// // 内容所使用编码
	// hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
	// hints.put(EncodeHintType.MARGIN, 1);
	//					
	// BitMatrix bitMatrix = new MultiFormatWriter().encode(ssString,
	// BarcodeFormat.QR_CODE, width,
	// height, hints);
	// // 生成二维码
	// MatrixToImageWriter.writeToFile(bitMatrix, format, pFile);
	// }
	// }
	// }
	//
	// return "bsuccess";
	// }
	 * 
	 */

	@RequestMapping("/selectRecords")
	public String selectUser(String uid, String curr, String uemail, String address, HttpServletRequest request, Model mo) {

		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(uid)) {
			long userid = Convert.toLong(uid, 0);
			pm = bqMemberWalletService.readPageById(userid, Contents.TRADE_CURRENCY_TYPE_ID);
		} else if (!StringUtil.isEmpty(uemail)) {
			long userid = 0;
			BQMember bqMember = super.findUsersByEmail(uemail);
			if (bqMember != null)
				userid = bqMember.getF_user_id();
			pm = bqMemberWalletService.readPageById(userid, Contents.TRADE_CURRENCY_TYPE_ID);
		} else if (!StringUtil.isEmpty(address)) {
			pm = bqMemberWalletService.readByAddress(address, Contents.TRADE_CURRENCY_TYPE_ID);
		}else{
			pm = bqMemberWalletService.readAll(Contents.TRADE_CURRENCY_TYPE_ID);
		}

		mo.addAttribute("pm", pm);
		mo.addAttribute("curr", curr);

		mo.addAttribute("uid", uid);
		mo.addAttribute("uemail", uemail);
		mo.addAttribute("address", address);
		return super.url_JSP("rechargeaddresslist");
	}

}
