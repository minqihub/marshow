package com.framework.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.corba.se.spi.ior.MakeImmutable;

/**
 * 文件处理公共类
 * @author minqi 2016-11-21
 *
 */
public class FileUtils {
	
	/**
	 * 检查文件是否存在
	 * @param file
	 */
	public static void checkFile(File file){
		if(!file.exists()){
			throw new RuntimeException("MQFrame：文件不存在");
		}
	}
	
	/**
	 * 获得文件扩展名
	 * @return 例如.jpg
	 */
	public static String getFileExtension(){
		
		return "";
	}
	
	/**
	 * 创建文件
	 * @param path
	 * @return
	 * 2016-11-19
	 */
	public static boolean creatFile(String fileName, String path){
		if(creatDir(path)){
			File file = new File(path + "/" + fileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("MQFrame：文件创建失败");
			}
			if(file.exists()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 创建路径（文件夹或多级文件夹）
	 * @param path
	 * @return
	 * 2016-11-19
	 */
	public static boolean creatDir(String path){
		if(path.contains(".") || !path.contains(":")){
			return false;
		}else if(path.contains("\\")){
			path.replace("\\", "/");
		}
		File file = new File(path);
		file.mkdirs();
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 创建隐藏文件夹
	 * @param path
	 * @return
	 */
	public static boolean creatHiddenDir(String path){
		if(path.contains(".") || !path.contains(":")){
			return false;
		}else if(path.contains("\\")){
			path.replace("\\", "/");
		}
		File file = new File(path);
		file.mkdirs();
		//R ： 只读文件属性。A：存档文件属性。S：系统文件属性。H：隐藏文件属性。
        String attribute = " attrib +H " + file.getAbsolutePath();		//注意+和H中间不能空格
	    try {
			Runtime.getRuntime().exec(attribute);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	
	
	
	/**
	 * 复制文件
	 * @param file
	 * @param path
	 * @return
	 * 2016-11-19
	 */
	public static boolean copyFile(File oldFile, File newFile){
		checkFile(oldFile);
		try {
			if(!newFile.exists()){
				newFile.createNewFile();
			}
			FileInputStream input = new FileInputStream(oldFile);
			FileOutputStream output = new FileOutputStream(newFile);
			
			byte[] bytes = new byte[1024*1024];
			
			while(true){
				int len = input.read(bytes);
				if(len < 0){
					break;
				}
				output.write(bytes, 0, len);
			}
			output.close();
			input.close();
		} catch (IOException e) {
			return false;
//			throw new RuntimeException("MQFrame：文件复制失败");
		}
		return true;
	}
	
	/**
	 * 删除文件
	 * @param file
	 * @return
	 * 2016-11-19
	 */
	public static boolean deleteFile(File file){
		
		return file.delete();
	}
	
	
	
	/**
	 * 扫描指定路径的文件信息
	 * @param path
	 * 2017-03-02
	 */
	public static void scanDir(String path){   
        // get file list where the path has   
        File file = new File(path);   
        // get the folder list   
        File[] array = file.listFiles();   
          
        for(int i=0;i<array.length;i++){   
            if(array[i].isFile()){
            	String fileName = array[i].getName();
                System.out.println("11111_" + fileName);   	//文件名.扩展名
                if(fileName.endsWith(".txt")){
                	System.out.println("有一个txt文本！！");
                }
                
                
                
//                System.out.println("22222_" + array[i]);   				//绝对路径 + 文件名.扩展名  
//                System.out.println("33333_" + array[i].getPath());   	//同上
            }else if(array[i].isDirectory()){
            	//递归获取
            	scanDir(array[i].getPath());
            }   
        }   
    }  
	
	
	
	public static void main(String[] args) {
//		scanDir("D:/ngrok");
		
		System.out.println(creatHiddenDir("e://hello"));
		
	}
}
