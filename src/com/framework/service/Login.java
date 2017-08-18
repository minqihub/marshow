package com.framework.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 登陆相关类
 * @author minqi 2017-07-31 16:04:48
 *
 */
@Controller
@RequestMapping("/trust/login")
public class Login {

	//loginType[]
	private static final String[] loginType = {"手机号登陆","微信登陆","QQ登陆","微博登陆"};
	
	
	private static final int Default_CookieAge = 2*60*60;			//默认的cookie有效期为2小时（普通登陆）
	private static final int ShutDown_CookieAge = -1;				//关闭浏览器即失效（用于陌生机器的访客登陆）
	
	/**
	 * 登陆方法
	 * @param json
	 * @param request
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/login.do")
	public Map login(String json, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		//http://localhost:8080/marshow/trust/login/login.do
		JSONObject data = Json.toJO(json);
		String username = data.getString("username");
		String password = data.getString("password");
		
		boolean flag = false;
		
		//判断前台传来的登陆类型
		switch (data.getInteger("loginType")) {
		case 1:			//微信登陆
			//前台点击微信登陆，获取code，获取openid，在此鉴别是否存在这个传来的openid
			
			
			break;
		case 2:			//QQ登陆
				
			break;
		case 3:			//微博登陆
				
			break;
		default:		//手机号登陆
			flag = Authorization.checkUser(username, password);
			break;
		}
		
		
		Map resultMap = new HashMap();
		if(flag){
			resultMap.put("MSGID", "S");
			resultMap.put("MESSAGE", "登陆成功");
			
			//登陆成功后，将sessionId存入cookie
			String sessionId = request.getSession().getId();
			Cookie cookie = new Cookie("ticket", sessionId);
			cookie.setMaxAge(Default_CookieAge);
			response.addCookie(cookie);
			System.out.println("sessionId："+sessionId);
			
			// TODO 写成存储过程：登陆成功就把sessionId写入数据库
			String sql = "update s_user set sessionid ='"+sessionId+"' where username = '"+ username+"'";
			MySQLUtils.sqlExecute(DataSource.community, sql);
			
			//

			
		}else{
			resultMap.put("MSGID", "E");
			resultMap.put("MESSAGE", "登陆失败");
		}
		HttpUtils.printString(response, Json.toJO(resultMap));
		return resultMap;
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
	 * 
	 * @param data
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/checkLogin.do")
	public static Map checkLogin(HttpServletRequest request, HttpServletResponse response){
		
		
		
		
		//检查cookie
		
		//检查session
		
		Map resultMap = new HashMap();
		resultMap.put("MSGID", "S");
		resultMap.put("MESSAGE", "");
		return resultMap;
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
			row = MySQLUtils.sqlExecute(DataSource.community, sql);
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
