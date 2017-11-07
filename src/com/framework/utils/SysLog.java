package com.framework.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 系统日志类
 * @author 2017-07-29 12:35:18
 *
 */
public class SysLog {
	
	protected Logger log = Logger.getLogger(SysLog.class);
	
	/**
	 * 控制台打印日志
	 * @param clazz
	 * @param info
	 */
	public static void log(Class clazz, String info) {
		System.out.println("["+DataUtils.getSysTime()+"  "+clazz+"]【"+info+"】");
	}
	
	/**
	 * 把输出结果同时保存到文本，用于大数据循环跟踪
	 * 注意：	1.此方法只适用windows下，开发调试使用
	 * 			2.默认保存在e盘（c盘可能系统设置了权限，无法读写）
	 * @param content
	 * @return 传入参数原样返回
	 * @throws IOException
	 */
	public static String printToTxt(String content){
    	String fileName = DataUtils.getSysTime("yyyy-MM-dd") + ".txt";
        File file = new File("d://" + fileName);
        
        if(!file.exists()){								//如果文件不存在,就动态创建文件
        	try {
				file.createNewFile();
			} catch (IOException e) {
				return content;
			}
        }
        
        FileWriter writer = null;
        String data = "【"+DataUtils.getSysTime()+"】" + content;
        try {
        	writer = new FileWriter(file, true);		//设置为:true，表示写入的时候追加数据
        	writer.write(data + "\r\n");
        } catch (IOException e) {
            
        } finally{
            if(writer != null){
                try {
                	writer.close();
				} catch (IOException e) {
					
				}
            }
        }
        return content;
    }
    
    
    public static void main(String[] args) throws IOException {
    	
    	for (int i = 0; i < 20; i++) {
			System.out.println(SysLog.printToTxt(UUID.randomUUID().toString()));
		}
    	
	}
    
    
	
}
