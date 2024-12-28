package com.btc100.btc_q.util;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

public class OSSFileUtil {
	
	// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
	private final static String Endpoint = "https://oss-cn-hongkong.aliyuncs.com";
	// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
	private final static String AccessKeyId = "LTAI5tPUX23dNB9ogMzMvX17";
	private final static String AccessKeySecret = "SUm2gWG1zWtlARiRQ9RqgDq0YjpkyR";
	private final static String BucketName = "btc-k";
	private final static String DomainName = "https://btc-k.oss-cn-hongkong.aliyuncs.com/";
	
	public static String uploadFile(byte[] content, String path) {
		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(Endpoint, AccessKeyId ,AccessKeySecret);
		
		// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
		ossClient.putObject(BucketName, path, new ByteArrayInputStream(content));

		// 关闭OSSClient。
		ossClient.shutdown();
		
		return DomainName + path;
	}
	
	public static String uploadFile(String localFile, String path) {
		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(Endpoint, AccessKeyId ,AccessKeySecret);

		PutObjectRequest putObjectRequest = new PutObjectRequest(BucketName, path, new File(localFile));
		ossClient.putObject(putObjectRequest);
		
		// 关闭OSSClient。
		ossClient.shutdown();
		
		return DomainName + path;
	}
	
	public static boolean isFileExist(String path) {
		OSS ossClient = new OSSClientBuilder().build(Endpoint, AccessKeyId ,AccessKeySecret);
		
		boolean found = ossClient.doesObjectExist(BucketName, path);
		// 关闭OSSClient。
		ossClient.shutdown();
		
		return found;
	}
	
	public static String getFileUrl(String path) {
		return DomainName + path;
	}
}
