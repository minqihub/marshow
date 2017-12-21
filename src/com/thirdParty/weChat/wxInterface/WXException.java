package com.thirdParty.weChat.wxInterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.framework.config.V1;
import com.framework.utils.DataUtils;

/**
 * 微信接口的异常
 * @author minqi 2017-12-12 10:04:47
 *
 */
public class WXException extends Exception {
	
	static {
		
	}
    
    /**
     * 获取错误代码的中文
     * @param errcode
     * @return
     */
	public static String getExceptionMsg(String errcode){
		String errcode_zn = DataUtils.isNull(V1.getProperty(errcode)) ? "未找到"+errcode+"匹配的中文信息" : V1.getProperty(errcode);
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
