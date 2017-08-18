package com.framework.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.database.DBHandler;
import com.framework.database.DataSource;
import com.framework.utils.Json;

/**
 * 注册类
 * @author minqi 2017-07-29 09:08:34
 *
 */
@Controller
@RequestMapping("/regist")
public class Regist extends DBHandler{
	
	
	/**
	 * 基本注册方法
	 * @param json
	 * @param response
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/regist.do")
	public Map regist(String json, HttpServletRequest request) throws SQLException{
		Map map = Json.toMap(json);
		
		
		
		
		
		
		return new HashMap();
	}
	
	/**
	 * 检查字段是否存在
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean checkExist(String key, Object value){
		
		
		
		
		return true;
	}
	
	
	
	private void returnData(String jsonString, HttpServletResponse response){
		PrintWriter pw = null;
		try{
			try {
				pw = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			pw.print(jsonString);
		}finally{
			if(pw != null){
				pw.close();
		    }
		}
	}
	
	
	
}
