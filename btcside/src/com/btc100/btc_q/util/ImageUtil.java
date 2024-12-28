package com.btc100.btc_q.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.google.zxing.EncodeHintType;

public class ImageUtil {
	
	private static final Logger logger = Logger.getLogger(ImageUtil.class);
	
	private static final Map<EncodeHintType, Object> HINTS = new HashMap<EncodeHintType, Object>();
	static {
		HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
		HINTS.put(EncodeHintType.MARGIN, 0);
	}
	
	private final static String JPG = "ffd8ff";
    
	private final static String PNG = "89504e47";
	
	private final static Map<String, String> fileTypes = new HashMap<String, String>();
    
    static {
        fileTypes.put("jpg", JPG);
        fileTypes.put("png", PNG);
    }
	
	/** 水印图片支持的文件格式 */
	private static final String[] IMG_FORMATS = {".png", ".jpg"};
	
	public static boolean isSupportFormat(String fileFmt) {
		boolean isMatch = false;
		for (int i = 0; i < IMG_FORMATS.length; ++i) {
			if (IMG_FORMATS[i].equalsIgnoreCase(fileFmt)) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
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
    
    public static String getFileHeader(byte[] file) throws IOException {
        byte[] buffer = new byte[4];
        System.arraycopy(file, 0, buffer, 0, buffer.length);
        
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
    
    public static boolean isValidFile(String fileType, byte[] file) throws IOException {
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
	
	public static String addWatermark(File file, String storePath, String iconBasePath) {
		
		File folder = new File(storePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		String result = CommonUtil.getUUID() + ".jpg";
		String targetFileName = storePath + result;
		
		String type = "TEXT";
		String text = "BFX";
		Integer degree = 0;
		Integer left = 80;
		Integer top = 30;
		Float clarity = 0.3F;
		
		boolean success = false;
		if ("IMG".equals(type)) {
			success = ImageUtil.waterMarkImageByIcon(iconBasePath, file.getAbsolutePath(), targetFileName, degree, left, top, clarity);
		} else {
			success = ImageUtil.waterMarkByText(text, file.getAbsolutePath(), targetFileName, degree, left, top, clarity);
		}
		
		return success ? result : "";
	}
	
	/**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     * @param degree 水印图片旋转角度
     * @param width 宽度(与左相比)
     * @param height 高度(与顶相比)
     * @param clarity 透明度(小于1的数)越接近0越透明
     */
    public static boolean waterMarkImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree, Integer width, Integer height, float clarity) {
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));
            int imgWidth = srcImg.getWidth(null);
            int imgHeight = srcImg.getHeight(null);
            BufferedImage buffImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);
            // 得到Image对象。
            Image img = imgIcon.getImage();
            float alpha = clarity; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 表示水印图片的位置
            if (width < 0) {
            	width = imgWidth + width - img.getWidth(null);
            }
            if (height < 0) {
            	height = imgHeight + height - img.getHeight(null);
            }
            g.drawImage(img, width, height, 105, 105, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            
            return true;
        } catch (Exception e) {
        	logger.error("添加图片水印异常，源路径：" + srcImgPath + "，目标路径：" + targerPath, e);
            return false;
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
            	logger.error("添加图片水印关闭输出流异常，详情：", e);
            }
        }
    }
    
    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param logoText 水印文字
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     * @param degree 水印图片旋转角度
     * @param width 宽度(与左相比)
     * @param height 高度(与顶相比)
     * @param clarity 透明度(小于1的数)越接近0越透明
     */
    public static boolean waterMarkByText(String logoText, String srcImgPath, String targerPath, Integer degree, Integer width, Integer height, Float clarity) {
        // 主图片的路径
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));
            
            int w = srcImg.getWidth(null);
            int h = srcImg.getHeight(null);
            if (w == 0 || h == 0) {
            	return false;
            }
            
            BufferedImage buffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 设置颜色
            g.setColor(Color.red);
            // 设置 Font
            g.setFont(new Font("宋体", Font.BOLD, 30));
            float alpha = clarity;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y) .
            g.drawString(logoText, w - width, h - height);
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            
            return true;
        } catch (Exception e) {
        	logger.error("添加文字水印异常，源路径：" + srcImgPath + "，目标路径：" + targerPath, e);
            return false;
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
            	logger.error("添加文字水印关闭输出流异常，详情：", e);
            }
        }
    }
}
