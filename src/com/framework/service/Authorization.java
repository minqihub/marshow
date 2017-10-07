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
		sessionMap.put("sessionId", request.getSession().getId());
		String sqlTemplate = "SELECT userId,mobile,email,nickName,logoImg,roleMark,commId,commName,location FROM S_User WHERE sessionId LIKE ?sessionId";
		
		Map returnMap = MySQLUtils.sqlQueryForMap(comm, SQLConvertor.format(sqlTemplate, sessionMap));
		if(!returnMap.isEmpty()){
			returnMap.put("MSGID", "S");
		}else{
			returnMap.put("MSGID", "E");
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
