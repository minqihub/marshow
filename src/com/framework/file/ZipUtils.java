package com.framework.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;


public class ZipUtils {  
	  
	private static final int buffer = 1024 * 2;  
	  
	/** 
	 * 解压Zip文件到当前文件夹
	 * @param path 源文件目录 
	 */  
	public static void unZip(String path){  
	     int count = -1;  
	     String savepath = "";  
	  
         File file = null;  
         InputStream is = null;  
         FileOutputStream fos = null;  
         BufferedOutputStream bos = null;  
	  
         savepath = path.substring(0, path.lastIndexOf(".")) + File.separator;					//保存解压文件目录
         System.out.println(savepath);
         new File(savepath).mkdir(); 															//创建保存目录  
         ZipFile zipFile = null;  
         try{
		     zipFile = new ZipFile(path,"gbk"); 												//解决中文乱码问题  
		     Enumeration<?> entries = zipFile.getEntries();  
		  
             while(entries.hasMoreElements()){
                 byte buf[] = new byte[buffer];  
  
                 ZipEntry entry = (ZipEntry)entries.nextElement();  
  
                 String filename = entry.getName();
             	 if(filename.contains("/")){
             		filename = filename.substring(filename.indexOf("/") + 1, filename.length());
            	 }
                 System.out.println(filename);
                 
                 boolean ismkdir = false;  
                 if(filename.lastIndexOf("/") != -1){ 											//检查此文件是否带有文件夹  
                    ismkdir = true;  
                 }  
                 filename = savepath + filename;  
  
                 if(entry.isDirectory()){ 														//如果是文件夹先创建  
		            file = new File(filename);  
		            file.mkdirs();  
		             continue;  
		         }  
                 file = new File(filename);  
                 if(!file.exists()){ 															//如果是目录先创建  
		            if(ismkdir){  
		            	new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); 	//目录先创建  
		            }  
                 }  
                 file.createNewFile(); //创建文件  
  
                 is = zipFile.getInputStream(entry);  
                 fos = new FileOutputStream(file);  
                 bos = new BufferedOutputStream(fos, buffer);  
  
                 while((count = is.read(buf)) > -1){  
                     bos.write(buf, 0, count);  
                 }  
                 bos.flush();  
                 bos.close();  
                 fos.close();
                 is.close();  
             }  
             zipFile.close();  
         }catch(IOException ioe){  
             ioe.printStackTrace();  
         }finally{  
            try{  
	            if(bos != null){  
	                bos.close();  
	            }  
	            if(fos != null){  
	                fos.close();  
	            }  
	            if(is != null){  
	                is.close();  
	            }  
	            if(zipFile != null){  
	                zipFile.close();  
	            }  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
        }  
	}
	
	
    /** 
    * 根据原始rar路径，解压到指定文件夹下.      
    * @param path 原始rar路径 
    * @param savaPath 解压到的文件夹      
    */
    public static void unRar(String path){
//        File dstDiretory = new File(dstDirectoryPath);
//        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
//            dstDiretory.mkdirs();
//        }
    	String savepath = path.substring(0, path.lastIndexOf(".")) + File.separator;
        Archive a = null;
        try {
            a = new Archive(new File(path));
            if (a != null) {
                a.getMainHeader().print(); 										// 打印文件信息.
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                	String fileName = fh.getFileNameW().isEmpty()?fh.getFileNameString():fh.getFileNameW();		//解决中文乱码问题
                	if(fileName.contains("\\")){
                		fileName = fileName.substring(fileName.indexOf("\\") + 1, fileName.length());
                	}
                	System.out.println(fileName);
                    if (fh.isDirectory()) { 									// 文件夹 
//                        File fol = new File(dstDirectoryPath + File.separator + fileName);
                    	File fol = new File(savepath + File.separator + fileName);
//                        fol.mkdirs();
                    } else { 													// 文件
//                        File out = new File(dstDirectoryPath + File.separator + fileName.trim());
                    	File out = new File(savepath + File.separator + fileName.trim());
//                        System.out.println(out.getAbsolutePath());
                        try {													// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压. 
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {			// 相对路径可能多级，可能需要创建父目录. 
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void getFiles(String path){
        if(path.toLowerCase().endsWith(".rar")){
            unRar(path);
        }else if(path.toLowerCase().endsWith(".zip")){
            unZip(path);
        }else{
        	throw new RuntimeException("MQFrame：仅支持rar和zip格式的压缩文件");
        }
    }
    
    /**
     * 删除解压后的文件
     * @param path
     */
    public static void deleteFiles(String path){
    	if(path.contains(".")){
        	path = path.substring(0, path.lastIndexOf("."));
    	}
    	File file = new File(path);
    	file.delete();
    	System.out.println(path);
    }
    
	public static void main(String[] args) {
//		getFiles("e:/测试.zip");
		deleteFiles("e:/测试.zip");
		
	}
	
}  