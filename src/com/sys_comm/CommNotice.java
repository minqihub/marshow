package com.sys_comm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 社区通知、活动
 * TODO 待重构为MyBatis
 * @author minqi
 *
 */
@Controller
@RequestMapping("/commNotice")
public class CommNotice {

	
	/**
	 * 查找通知、活动
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getNotice.do")
	public List getNotice(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
//		String sql = "SELECT * FROM C_Notice WHERE commId = '" + data.get("commId") +"' AND validMark = '1' AND status = '20' ORDER BY startTime";
		String sql = "SELECT * FROM C_COMM_NOTICE WHERE COMM_ID = '" + data.get("commId") +"' AND VALIDMARK = '1' ORDER BY START_TIME";
		List list = MySQLUtils.sqlQueryForList(DataSource.comm, sql);
		System.out.println(list);
		
		//TODO 数据库中的时间有小数点的问题
//		for (int i = 0; i < list.size(); i++) {
//			JSONObject row = Json.toJO(list.get(i));
//			
//			java.sql.Timestamp timeFound = (java.sql.Timestamp)row.get("timeFound");
//			java.sql.Timestamp startTime = (java.sql.Timestamp)row.get("startTime");
//			java.sql.Timestamp endTime = (java.sql.Timestamp)row.get("endTime");
//			
//			row.put("timeFound", new SimpleDateFormat("yyyy-MM-dd").format(row.get("timeFound")));
//			row.put("startTime", new SimpleDateFormat("yyyy-MM-dd").format(row.get("startTime")));
//			row.put("endTime", new SimpleDateFormat("yyyy-MM-dd").format(row.get("endTime")));
//		}
//		System.out.println(list);
		
		
		JSONObject returnJson = new JSONObject();
		returnJson.put("code", 0);
		returnJson.put("msg", "");
		returnJson.put("count", list.size());
		returnJson.put("data", list);
		
//		{ code: 0,  msg: "", count: 1000, data: [数据] } 
		
        PrintWriter pw = null;
		try {
			pw = response.getWriter();
            pw.print(returnJson);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
            pw.close();
        }
		return list;
	}
	
	/**
	 * 添加通知、活动
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/addNotice.do")
	public Map addNotice(String json, HttpServletResponse response){
		Map data = Json.toMap(json);

		data.put("COMM_ID", "comm0001");
		data.put("USER_ID", "user0001");
		data.put("TIME_FOUND", DataUtils.getSysTime());
		data.put("STATE", "10");
		data.put("OPINION", "");
		
		String sql = "INSERT INTO C_COMM_NOTICE (COMM_ID, TITLE, CONTENT, USER_ID, TIME_FOUND, START_TIME, END_TIME, NOTICE_TYPE, STATE, OPINION, VISIBLE) "
				+ "VALUES (?COMM_ID, ?TITLE, ?CONTENT, ?USER_ID, ?TIME_FOUND, ?START_TIME, ?END_TIME, ?NOTICE_TYPE, ?STATE, ?OPINION, ?VISIBLE);";
		Map returnMap = new HashMap();
		try {
			MySQLUtils.sqlExecuteMap(DataSource.comm, sql, data);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "发布成功");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "发布失败：" + e);
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	
	
	
}
