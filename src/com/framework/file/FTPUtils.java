package com.framework.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;

/**
 * 文件服务器传输工具类
 * @author minqi 2017-07-31 14:19:48
 *
 */
@Controller
@RequestMapping("/ftpUtils")
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
	
	
}
