package com.framework.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

/**
 * 图片处理类
 * @author minqi 2016-11-21
 *
 */
public class ImageUtils extends FileUtils {

	public static boolean checkImg(String path){

		return true;
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
