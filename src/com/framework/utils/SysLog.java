package com.framework.utils;

/**
 * 系统日志类
 * @author 2017-07-29 12:35:18
 *
 */
public class SysLog {
	
	
	/**
	 * 控制台打印日志
	 * @param clazz
	 * @param info
	 */
	public static void log(Class clazz, String info) {
		System.out.println("["+DataUtils.getSysTime()+"  "+clazz+"]【"+info+"】");
	}
	
	
	
	
	
	
}
