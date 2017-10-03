package com.framework.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.framework.utils.PropertiesReader;

/**
 * 图片处理类
 * @author minqi 2016-11-21
 *
 */
public class ImageUtils extends FileUtils {

	//配置文件读取对象
	public static PropertiesReader property = PropertiesReader.getInstance();
	
	//MySQL数据库参数
	private static String FileDir = property.getProperty("FileDir");
	
	
	
	
	
	/**
	 * 图片上传
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public static Map upload(HttpServletRequest request) throws IOException{
		
//		request.setCharacterEncoding("utf-8");  

		//1、创建一个DiskFileItemFactory工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //2、创建一个文件上传解析器  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        
        //解决上传文件名的中文乱码  
        upload.setHeaderEncoding("UTF-8");   
        factory.setSizeThreshold(1024 * 500);		//设置内存的临界值为500K  
        File linshi = new File("E:\\linshi");		//当超过500K的时候，存到一个临时文件夹中  
        factory.setRepository(linshi);  
        upload.setSizeMax(1024 * 1024 * 5);//设置上传的文件总的大小不能超过5M  
        
        try {  
            // 1. 得到 FileItem 的集合 items  
            List<FileItem> items = upload.parseRequest(request);  
  
            // 2. 遍历 items:  
            for (FileItem item : items) {  
                // 若是一个一般的表单域, 打印信息  
                if (item.isFormField()) {  
                    String name = item.getFieldName();  
                    String value = item.getString("utf-8");  
  
                    System.out.println(name + ": " + value);  
                      
                      
                }  
                // 若是文件域则把文件保存到 e:\\files 目录下.  
                else {  
                    String fileName = item.getName();  
                    long sizeInBytes = item.getSize();  
                    System.out.println(fileName);  
                    System.out.println(sizeInBytes);  
  
                    InputStream in = item.getInputStream();  
                    byte[] buffer = new byte[1024];  
                    int len = 0;  
  
                    fileName = "e:\\files\\" + fileName;//文件最终上传的位置  
                    System.out.println(fileName);  
                    OutputStream out = new FileOutputStream(fileName);  
  
                    while ((len = in.read(buffer)) != -1) {  
                        out.write(buffer, 0, len);  
                    }  
  
                    out.close();  
                    in.close();  
                }  
            }  
  
        } catch (FileUploadException e) {  
            e.printStackTrace();  
        }  
		
		
		return null;
	}
	
	
	/**
	 * 等比长宽缩小图片
	 * @param imgPath 图片路径
	 * @param scale 缩小倍数（1为原图；2为原图一半；4为原图四分之一）
	 * @return 新图的输入流
	 * @throws IOException
	 */
	public InputStream getNewImg(String imgPath, int scale) throws IOException{
		if(imgPath.contains("\\")){
			imgPath = imgPath.replace('\\', '/');
		}else if( scale < 0 ){
			scale = 0 - scale;
		}
		File file = new File(imgPath);
		Image oldImg = null;
		try {
			oldImg = ImageIO.read( file );
		} catch (IOException e) {
			throw new RuntimeException("MQFrame：未读取到图片");
		}
		int newH = oldImg.getHeight(null) / scale ;
		int newW = oldImg.getWidth(null) / scale ;
		//构建图片对象  
		Image newImg = oldImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage bi = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(newImg, 0, 0, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();  
        ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);  
        ImageIO.write(bi, "jpg", imOut);  
        InputStream in = new ByteArrayInputStream(bs.toByteArray());
		
		int index = file.getName().toString().indexOf(".");
		String filename = URLEncoder.encode(file.getName().substring(0,index),"UTF-8");
		long filesize = file.length();
//		byte[] aryZlib = JLTools.toByteArray(in);
		
		return in;
	}
	
	/**
	 * 指定长宽缩小图片
	 * @param height 指定新图高度
	 * @param width 指定新图宽度
	 */
	public static void getNewImg2(String imgPath, int height, int width){
		
		
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
