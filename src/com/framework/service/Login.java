package com.framework.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.database.DBHandler;
import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.database.SQLConvertor;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 登陆相关类
 * @author minqi 2017-07-31 16:04:48
 *
 */
@Controller
@RequestMapping("/trust/login")
public class Login{

	//loginType[]
	private static final String[] loginType = {"手机号登陆","微信登陆","QQ登陆","微博登陆"};
	
	
	private static final int Default_CookieAge = 2*60*60;			//默认的cookie有效期为2小时（普通登陆）
	private static final int ShutDown_CookieAge = -1;				//关闭浏览器即失效（用于陌生机器的访客登陆）
	
	private static JdbcTemplate comm = DataSource.comm;
	
	/**
	 * 登陆方法
	 * @param json
	 * @param request
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/login.do")
	public Map login(String json, HttpServletRequest request, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		Map loginMap = new HashMap();
		
		Map returnMap = new HashMap();
		returnMap.put("MSGID", "E");
		returnMap.put("MESSAGE", "登陆失败");
		
		try {
			String SESSION_ID = request.getSession().getId();
			Map result = new HashMap();
			
			if(DataUtils.isNull(data.get("loginType"))){					//默认手机号登陆
				
				loginMap.put("MOBILE", data.get("username").toString());
				loginMap.put("PASSWORD", data.get("password").toString());
				String sqlTemplate = "SELECT * FROM S_USER WHERE MOBILE = ?MOBILE and PASSWORD = ?PASSWORD";
				
				result = MySQLUtils.sqlQueryForMap(comm, sqlTemplate, loginMap);
				if(!result.isEmpty()){
					returnMap.put("MSGID", "S");
					returnMap.put("MESSAGE", "登陆成功");
				}
				
			}else if(data.get("loginType").toString().equals("1")){			//TODO 微信登陆
				
			}else if(data.get("loginType").toString().equals("2")){			//TODO QQ登陆
				
			}else if(data.get("loginType").toString().equals("3")){			//TODO 微博登陆
				
			}
			
			//验证成功之后，写入sessionId
			if(returnMap.get("MSGID").toString().equals("S")){
				String sql = "UPDATE S_USER SET SESSION_ID = ?SESSION_ID WHERE USER_ID = ?USER_ID";
				Map sessionMap = new HashMap();
				sessionMap.put("USER_ID", result.get("USER_ID").toString());
				sessionMap.put("SESSION_ID", SESSION_ID);
				MySQLUtils.sqlExecuteMap(comm, sql, sessionMap);
			}
			
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "数据库异常");
		}
		
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	
	/**
	 * 退出登陆方法
	 * @param data
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/logout.do")
	public Map logout(String json, HttpServletRequest request, HttpServletResponse response){
		JSONObject data = Json.toJO(json);
		
		
		
		
		return null;
	}
	
	
	/**
	 * 检查是否登陆
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/checkLogin.do")
	public static Map checkLogin(HttpServletRequest request, HttpServletResponse response){
		Map returnMap = Authorization.getUserInfo(request);
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	/**
	 * 重置密码
	 * @param json
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/resetPassword.do")
	public static Map resetPassword(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		//接收传入的新密码
		String mobile = map.get("mobile").toString();
		String password = map.get("password").toString();
		
		//更新数据库的原始密码
		int row = 0;
		String sql = "update s_user set password ='" + password + "' where mobile = '" + mobile + "'";
		try {
			row = MySQLUtils.sqlExecute(DataSource.comm, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Map resultMap = new HashMap();
		if(row == 1){
			resultMap.put("MSGID", "S");
			resultMap.put("MESSAGE", "修改成功");
		}else{
			resultMap.put("MSGID", "E");
			resultMap.put("MESSAGE", "修改失败，数据库连接异常");
		}
		HttpUtils.printString(response, resultMap);
		return resultMap;
	}
	
	
}
