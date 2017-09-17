package com.framework.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;

public class Authorization {

	
	
	/**
	 * 检查登陆的合法性
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkUser(String mobile, String password){
		//查询数据库，此password应该是前台MD5加密后的 //TODO
		
		String sql = "select password from s_user where mobile = '"+ mobile +"'";
		Map resultMap = MySQLUtils.sqlQueryForMap(DataSource.comm, sql);
		
		if(resultMap.get("password").equals(password)){
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	/**
	 * 请求对象中获取登陆信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getUserInfo(HttpServletRequest request){
		Map userInfo = null;
		
		//检查cookie
		Cookie[] cookie = request.getCookies();
		for (Cookie c : cookie) {
			String domain = c.getDomain();
			int maxAge = c.getMaxAge();
			String name = c.getName();
			String value = c.getValue();
		}
		
		
		//获取sessionId
		String sessionId = request.getSession().getId();
		
		//通过sessionId去用户表中查询信息 TODO 写成存储过程
		String sql = "select * from s_user where sessionid ='" + sessionId + "'";
		userInfo = MySQLUtils.sqlQueryForMap(DataSource.comm, sql);
		
		
		
		
		return userInfo;
	}
	
}
