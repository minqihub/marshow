package com.thirdParty.weChat.wxInterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.framework.utils.DataUtils;
import com.framework.utils.PropertiesReader;

/**
 * 微信接口的异常
 * @author minqi 2017-12-12 10:04:47
 *
 */
public class WXException extends Exception {
	
	private static final Properties properties = new Properties();		//静态常量，只有一个实例化Properties对象
    private static PropertiesReader reader = null;						//静态变量
	
	static {
		String fileName = WXException.class.getClassLoader().getResource("/").getPath().replace("classes", "v1.config");
//		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
		
		}
	}
    
    /**
     * 获取错误代码的中文
     * @param errcode
     * @return
     */
	public static String getExceptionMsg(String errcode){
		String errcode_zn = DataUtils.isNull(properties.getProperty(errcode)) ? "未找到"+errcode+"匹配的中文信息" : properties.getProperty(errcode);
		return errcode_zn;
	}
    
    /**
     * 获取错误代码的中文，放入传入的Map中
     * @param returnMap
     * @param errcode
     * @return
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getExceptionMsg(Map returnMap, String errcode){
		returnMap.put("WXException", getExceptionMsg(errcode));
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
