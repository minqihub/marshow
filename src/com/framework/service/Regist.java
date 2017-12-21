package com.framework.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.config.V1;
import com.framework.database.DBHandler;
import com.framework.database.DataSource;
import com.framework.database.SQLConvertor;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 注册类
 * @author minqi 2017-10-06 19:15:45
 *
 */
@Controller
@RequestMapping("/trust/regist")
public class Regist extends DBHandler{
	
	//默认管理员头像
	private static final String default_Admin_Logo = V1.getProperty("defaultAdminLogo");
	//默认用户头像
	private static final String default_User_Logo = V1.getProperty("defaultUserLogo");
	
	private static JdbcTemplate comm = DataSource.comm;
	
	
	/**
	 * 基本注册方法
	 * @param json 用户填手机号时，已做重复性检查
	 * @param response
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/regist.do")
	public Map regist(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try{
			Map insertMap = new HashMap();
			insertMap.put("USER_ID", request.getSession().getId());
			insertMap.put("MOBILE", map.get("mobile").toString());
			insertMap.put("PASSWORD", map.get("password").toString());
			insertMap.put("NICKNAME", map.get("mobile").toString());
			insertMap.put("LOGOIMG", this.default_User_Logo);
			insertMap.put("CTS", DataUtils.getSysTime());
			
			//角色标记：1社区超管；2社区管理员；3社区居民；4社区商户
			insertMap.put("ROLEMARK", map.get("roleMark").toString());
			
			String sql = "INSERT INTO S_USER (USER_ID, MOBILE, NICKNAME, LOGOIMG, PASSWORD, ROLEMARK, CTS) "
					+ "VALUES (?USER_ID, ?MOBILE, ?NICKNAME, ?LOGOIMG, ?PASSWORD, ?registTime, ?ROLEMARK, ?CTS)";
			sqlExecuteMap(comm, sql, insertMap);

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "注册成功");
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "必填字段请填写完整");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "其他异常：" + e);
		}
		
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	/**
	 * 检查手机号是否存在
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/checkMobileExist.do")
	public Map checkMobileExist(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		Map map = new HashMap();
		map.put("mobile", data.get("mobile").toString());
		
		String sqlTemplate = "SELECT * FROM S_User WHERE mobile LIKE ?mobile";
		
		Map result = sqlQueryForMap(comm, SQLConvertor.format(sqlTemplate, map));
		
		Map returnMap = new HashMap();
		if(result.isEmpty()){
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "mobile不重复");
		}else{
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "mobile重复");
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	
}
