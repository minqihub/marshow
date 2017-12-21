package com.framework.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 文件服务器传输工具类
 * @author minqi 2017-07-31 14:19:48
 *
 */
@Controller
@RequestMapping("/trust/ftpUtils")
public class FTPUtils {
	
	//用户私有文件存储路径
	private static final String PrivateDir = "D:/apache-tomcat-7.0.81/webapps";
	//用户公用文件存储路径
	private static final String PublicDir = "D:/apache-tomcat-7.0.81/webapps";
	//文件系统分隔符
	private static final String SEPARATOR = System.getProperty("file.separator");

	
	/**
	 * 用户上传的共有文件
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/publicUpload.do")
	public Map publicUpload(HttpServletRequest request, HttpServletResponse response) throws IOException{

		
		//以当天日期为分割创建文件夹
		String nowDate = DataUtils.getSysTime("yyyy-MM-dd");
		File dir = new File(this.PublicDir + this.SEPARATOR + "fs" + this.SEPARATOR + nowDate);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		//1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");   
        factory.setSizeThreshold(1024 * 500);		//设置内存的临界值为500K  
        
        File tempDir = new File( this.PublicDir + this.SEPARATOR + "fs" );		//当超过500K的时候，存到一个临时文件夹中  
        factory.setRepository(tempDir);  
        upload.setSizeMax(1024 * 1024 * 5);			//设置上传的文件总的大小不能超过5M  
        
        
        Map returnMap = new HashMap();
		returnMap.put("code", 0);
		returnMap.put("msg", "图片上传成功");
		List src = new ArrayList();
		
        try {  
            // 1. 得到 FileItem 的集合 items  
            List<FileItem> items = upload.parseRequest(request);  
  
            // 2. 遍历 items:  
            for (FileItem item : items) {
            	String fileName = item.getName();  		//原图片名+扩展名
           	 	long sizeInBytes = item.getSize();  	//原图片大小
           	 	int hashCodeName = (fileName + sizeInBytes).hashCode();		//利用文件名与文件大小重命名，以免文件被覆盖
           	 	
           	 	InputStream in = item.getInputStream();  
           	 	byte[] buffer = new byte[1024];  
           	 	int len = 0;  

           	 	//文件服务器存储位置
           	 	String fileLocation = dir + this.SEPARATOR + hashCodeName;
           	 	System.out.println("文件服务器存储位置："+ fileLocation);
           	 	
           	 	OutputStream out = new FileOutputStream(fileLocation);
  
           	 	while ((len = in.read(buffer)) != -1) {
           	 		out.write(buffer, 0, len);
           	 	}  
	  
           	 	out.close();
           	 	in.close();
           	 	
        		String port = request.getServerPort() + "";
        		if(!port.equals("80")){
        			port = ":" + port;
        		}else{
        			port = "";
        		}
        		
        		//文件服务器访问地址
        		String fileUrl = request.getScheme() + "://" + request.getServerName() + port + "/fs/" + nowDate+ "/" + hashCodeName;
        		System.out.println("文件服务器访问地址："+ fileUrl);

        		src.add(fileUrl);
        		returnMap.put("src", src);
            }  
  
        } catch (FileUploadException e) {  
    		returnMap.put("code", -1);
    		returnMap.put("msg", "图片上传失败");
        }  
		
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	/**
	 * 用户上传私有文件
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/privateUpload.do")
	public Map privateUpload(HttpServletRequest request, HttpServletResponse response){
		
		
		
		return null;
	}
	
	
	
	/**
	 * 下载多媒体文件，用来下载不是直接存在服务器上的文件
	 * 例如http://..../downloadMedia.do?json={"sourceUrl":"http://pic1.win4000.com/wallpaper/e/526c9f87129d9.jpg","sourceName":"123.png"}
	 * @param json {"sourceUrl":"文件的互联网地址","sourceName":"指定要下载的文件名"}，一定要urlEncode
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/downloadMedia.do")
	public void downloadMedia(String json, HttpServletRequest request, HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		try {
			//处理GET请求中文乱码
			json = new String(json.getBytes("ISO8859-1"), "UTF-8");
			Map data = Json.toMap(json);
			
			String sourceUrl = data.get("sourceUrl").toString();
			String sourceName = DataUtils.isNull(data.get("sourceName")) ? sourceUrl : data.get("sourceName").toString();
			
			//处理浏览器兼容；设置下载文件名称
			String browserName = request.getHeader("User-Agent");
			boolean isMSIE = browserName.contains("MSIE");
			if (isMSIE) {
				response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(sourceName, "UTF8"));
			} else {
				response.addHeader("Content-Disposition", "attachment;fileName=" + new String(sourceName.getBytes("gb2312"), "ISO8859-1"));
			}
			
			//请求资源
			sourceUrl = sourceUrl.replace(" ", "%20");		//url地址如果存在空格，会导致报错！  解决方法为：用+或者%20代替url参数中的空格
			URL url = new URL(sourceUrl);
			URLConnection conn = url.openConnection();
			
			//定义输出类型
			response.setContentType("application/msexcel;charset=utf-8");
			//设置下载的文件大小
			response.setContentLength(conn.getContentLength());
			
			inputStream = conn.getInputStream();
			outputStream = response.getOutputStream();
			IOUtils.copy(inputStream, outputStream);
			
	 	} catch (Exception e) {
	    	System.out.println("请求下载资源出错：" + e);
		} finally { 
			IOUtils.closeQuietly(inputStream); 
	    	IOUtils.closeQuietly(outputStream); 
	  	} 
	}
	
	
}
