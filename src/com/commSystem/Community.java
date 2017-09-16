package com.commSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 社区
 * TODO 待重构为MyBatis
 * @author minqi
 *
 */
@Controller
@RequestMapping("/community")
public class Community {

	
	
	/**
	 * 查询社区结构
	 * @param json
	 * @param request
	 * @return
	 * http://localhost:8080/marshow/community/getStructure.do
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getStructure.do")
	public List getStructure(String json, HttpServletResponse response){
		Map data = Json.toJO(json);
		
		//TODO 测试数据
		data.put("commId", "comm0001");
		
		
		JdbcTemplate community = DataSource.comm;
		String sql = "SELECT * FROM C_CommStructure WHERE commId = '" + data.get("commId") +"'";
		List list = MySQLUtils.sqlQueryForList(community, sql);
		System.out.println(list);
		HttpUtils.printString(response, list);
		return list;
	}
	
	
	
	
	
	/**
	 * 定义社区结构
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("/defineStructure.do")
	public Map defineStructure(String json, HttpServletResponse response){
		
		
		
		return new HashMap();
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
