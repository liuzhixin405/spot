package com.btc100.btc_q.action.admin;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.btc100.btc_q.entity.BQNotradetime;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/noTradeTime")
public class BQNtradeTimeAction extends BaseAction {

	@Autowired
	BQNotradetimeService bqNotradetimeService;

	@RequestMapping("/noTradePage")
	public String noTradeTimePage(HttpServletRequest request, Model mo) throws ParseException {
		String time = request.getParameter("time");
		long time_client = StringUtil.isEmpty(time) ? 0l : this.modifyTime(time); // 0 shijian
		int type = ConvertUtil.toInt(request.getParameter("type"), 0); // fuzhi 0 zhengzhi
		
		PagerModel pm = bqNotradetimeService.findAllpage(time_client, type, 0);
		mo.addAttribute("pm", pm);
		mo.addAttribute("time", time);
		mo.addAttribute("type", type);
		return super.url_JSP("noTradeTime");
	}

	@RequestMapping("/add")
	public void noTradeTimeAdd(HttpServletRequest request, HttpServletResponse response, Model mo) throws ParseException {
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			toWeb(response, "未选择时间！");
		}
		int type = ConvertUtil.toInt(request.getParameter("type"), 0);
		BQNotradetime anewTime = new BQNotradetime();
		anewTime.setH_time(this.modifyTime(time));
		anewTime.setH_type(type);
		bqNotradetimeService.add(anewTime);
		mo.addAttribute("time", time);
		mo.addAttribute("type", type);
		toWeb(response, "success");
	}

	@RequestMapping("/delete")
	public String noTradeTimeDelete(HttpServletRequest request, Model mo) {
		long id = ConvertUtil.toLong(request.getParameter("id"), 0l);
		if (id <= 0) {
			return super.errorPage(mo, "未获取到ID！");
		}

		bqNotradetimeService.deleteById(id);

		return super.redirect("/noTradeTime/noTradePage.do");
	}

	@RequestMapping("/update")
	public String noTradeTimeUpdate(HttpServletRequest request, Model mo) throws ParseException {
		long id = ConvertUtil.toLong(request.getParameter("id"), 0l);
		if (id <= 0) {
			return super.errorPage(mo, "未获取到ID！");
		}

		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			return super.errorPage(mo, "未选择时间！");
		}

		bqNotradetimeService.updateById(id, this.modifyTime(time));

		return super.redirect("/noTradeTime/noTradePage.do");
	}

	/**
	 * 将时间格式为yyyy-MM-dd HH:mm:ss的时间转换为yyyy-MM-dd 00:00:00的时间，再转换成时间戳
	 * 
	 * @param time
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	private long modifyTime(String time) throws ParseException {
		String yearMonthDay = time.trim().split(" ")[0] + " 00:00:00";
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return DateUtil.DateToUnix(formatDate.parse(yearMonthDay));
	}

	/************************ 以下是读取xls xlsx文件操作 **********************/
	
	@RequestMapping(value = "/reade", method = RequestMethod.POST)
	public String readExcel(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, Model mo) {
		
		// 获取文件全名
		String fileName = file.getOriginalFilename();
		// 获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		// 对扩展名进行小写转换
		ext = ext.toLowerCase();

		// 文件限制
		// 定义一个数组，用于保存可上传的文件类型
		List<String> fileTypes = new ArrayList<String>();
		fileTypes.add("xls");
		fileTypes.add("xlsx");

		if (!fileTypes.contains(ext) || fileName.contains("%00")) { // 如果扩展名属于允许上传的类型，则创建文件
			return super.errorPage(mo, "上传的文件不符合要求");
		}
		
		try {
			String[][] arrayStrs = null;
			if (ext.equalsIgnoreCase("xls"))
				arrayStrs = this.readExcelXls(file.getInputStream());
			else
				arrayStrs = this.readExcelXlsx(file.getInputStream());

			List<BQNotradetime> lstNotrades = new ArrayList<BQNotradetime>();
			if (arrayStrs != null && arrayStrs.length > 0) {
				for (int i = 0; i < arrayStrs.length; i++) {
					String time = arrayStrs[i][0];
					String type = arrayStrs[i][1];

					BQNotradetime anewTime = new BQNotradetime();
					anewTime.setH_time(this.modifyTimefile(time));
					int type_file = ConvertUtil.toInt(type, 0);
					anewTime.setH_type(type_file);
					lstNotrades.add(anewTime);
				}
				bqNotradetimeService.addList(lstNotrades);
			}
		} catch (ParseException e) {
			return super.errorPage(mo, "上传的文件时间格式不符合要求");
		} catch (IOException e) {
			return super.errorPage(mo, "上传的文件不符合要求");
		} catch (Exception e) {
			return super.errorPage(mo, "上传的文件不符合要求");
		}
		
		return super.redirect("/noTradeTime/noTradePage.do");
	}
	
	/**
	 * 将时间格式为yyyyMMdd的时间转换为yyyyMMdd 00:00:00的时间，再转换成时间戳
	 * 
	 * @param time
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	private long modifyTimefile(String time) throws ParseException {
		String yearMonthDay = time + " 00:00:00";
		DateFormat formatDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return DateUtil.DateToUnix(formatDate.parse(yearMonthDay));
	}
	
	/**
	 * 将xls文件里的数据转换成字符数组
	 * 
	 * @param fileName
	 *            文件路径
	 * @return
	 * @throws IOException
	 */
	private String[][] readExcelXls(InputStream fileName) throws IOException {

		String[][] s = null;
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(fileName);
			HSSFSheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
//			System.out.println("该Excel文件中有" + rows + "行");
			s = new String[rows][];
			if (rows > 0) {
				// 获取总列数`
				int cells = sheet.getRow(0).getPhysicalNumberOfCells();
//				System.out.println("该Excel文件有" + cells + "列");
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					String[] cellsvalue = new String[cells + 1];
					for (short c = 0; c < cells; c++) {
						String value = "";
						@SuppressWarnings("deprecation")
						HSSFCell cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									value = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
								} else {
									NumberFormat nf = NumberFormat.getInstance();
									nf.setGroupingUsed(false);// true时的格式：1,234,567,890
									double acno = cell.getNumericCellValue();
									value = nf.format(acno);
								}
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = "";
							default:
								break;
							}
						}
						cellsvalue[c] = value;
					}
					s[r] = cellsvalue;
				}
			}
		} catch (Exception ex) {
		}
		return s;
	}

	private String[][] readExcelXlsx(InputStream fileName) throws IOException {
		
		String[][] s = null;
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(fileName);
			XSSFSheet sheet = xwb.getSheetAt(0);
			
			int rows = sheet.getPhysicalNumberOfRows();
//			System.out.println("该Excel文件中有" + rows + "行");
			s = new String[rows][];

			if (rows > 0) {
				// 获取总列数`
				int cells = sheet.getRow(0).getPhysicalNumberOfCells();
//				System.out.println("该Excel文件有" + cells + "列");
				for (int r = 0; r < rows; r++) {
					XSSFRow row = sheet.getRow(r);
					String[] cellsvalue = new String[cells + 1];
					for (short c = 0; c < cells; c++) {
						String value = "";
						XSSFCell cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									value = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
								} else {
									NumberFormat nf = NumberFormat.getInstance();
									nf.setGroupingUsed(false);// true时的格式：1,234,567,890
									double acno = cell.getNumericCellValue();
									value = nf.format(acno);
								}
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = "";
							default:
								break;
							}
						}
						cellsvalue[c] = value;
					}
					s[r] = cellsvalue;
				}
			}
		} catch (Exception ex) {
		}
		return s;
	}
	
}
