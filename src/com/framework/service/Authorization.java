package com.framework.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.database.SQLConvertor;

public class Authorization {

	private static JdbcTemplate comm = DataSource.comm;
	
	/**
	 * 请求对象中获取用户信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getUserInfo(HttpServletRequest request){
		
		//获取sessionId
		Map sessionMap = new HashMap();
		sessionMap.put("SESSION_ID", request.getSession().getId());
		String sqlTemplate = "SELECT * FROM S_USER WHERE SESSION_ID = ?SESSION_ID";
		
		Map returnMap = MySQLUtils.sqlQueryForMap(comm, SQLConvertor.format(sqlTemplate, sessionMap));
		if(!returnMap.isEmpty()){
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "已登录状态");
		}else{
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "未登录拒绝访问");
		}
			
		return returnMap;
	}
	
	
	
	
	/**
	 * 检查登陆的合法性
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkUser(String mobile, String password){
		//查询数据库，此password应该是前台MD5加密后的 //TODO
		
		String sql = "select passWord from s_user where mobile = '"+ mobile +"'";
		Map resultMap = MySQLUtils.sqlQueryForMap(DataSource.comm, sql);
		
		if(resultMap.get("password").equals(password)){
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
}
