package com.commSystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.database.SQLConvertor;
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

	//查询当前社区的所有结构数据
	private List list = null;
	private JdbcTemplate community = DataSource.comm;
	
	/**
	 * 查询社区结构
	 * @param json
	 * @param request
	 * @return
	 * http://localhost:8080/marshow/community/getStructure.do
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/getStructure.do")
	public List getStructure(String json, HttpServletResponse response){
		Map data = Json.toJO(json);

		String sql = "SELECT * FROM C_CommStructure WHERE commId = '" + data.get("commId") +"' AND validMark = '1' ORDER BY code";
		List list = MySQLUtils.sqlQueryForList(community, sql);
		this.list = list;
		
		//处理数据
		List resultList = handleData(null, null);

		HttpUtils.printString(response, resultList);
		return resultList;
	}
	
	/**
	 * 处理数据，以满足layui要求
	 * @param list 总数据
	 * @param code 需要递归查询的当前节点代码
	 * @return {name: '父节点2',code:'02', children: [{name: '子节点21',code:'0201', children: [{name: '子节点211',code:'020101'}]}]}
	 */
	@SuppressWarnings("rawtypes")
	private List handleData(JSONArray jsonAry, String code){
		if(jsonAry == null){									//首次，获取顶级结构
			JSONArray returnAry = new JSONArray();
			for (int i = 0; i < this.list.size(); i++) {
				JSONObject row = Json.toJO(this.list.get(i));
				if(row.getString("isFirst").equals("1")){		//查询顶级结构
					returnAry.add(row);
				}
			}
			return handleData(returnAry, null);					//递归
		}else if(code == null){
			for (int i = 0; i < jsonAry.size(); i++) {			//获取顶级节点的子节点
				JSONObject row = Json.toJO(jsonAry.get(i));
				row.put("children", handleData(jsonAry, row.get("code").toString()));
			}
			return jsonAry;
		}else{													//递归产生子节点的（子节点）*n
			JSONArray childAry = new JSONArray();
			for (int i = 0; i < this.list.size(); i++) {
				JSONObject row = Json.toJO(this.list.get(i));
				if(row.get("sjCode") != null && row.getString("sjCode").equals(code)){
					row.put("children", handleData(childAry, row.get("code").toString()));
					childAry.add(row);
				}
			}			
			return childAry;
		}
	}
	
	
	/**
	 * 修改社区结构
	 * @param json
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/updateStructure.do")
	public Map updateStructure(String json, HttpServletResponse response){
		Map data = Json.toJO(json);
		
		String option = data.get("option").toString();
		JSONObject choosedNode = Json.toJO(data.get("choosedNode"));
		
		Map returnMap = new HashMap();
		if(option.equals("edit")){						//修改节点
			returnMap = edit(data, choosedNode);
		}else if(option.equals("add")){					//新增同级节点
			returnMap = add(data, choosedNode);
		}else if(option.equals("addSub")){				//新增子节点
			returnMap = addSub(data, choosedNode);
		}else if(option.equals("del")){					//删除节点
			returnMap = del(data, choosedNode);
		}else{
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "未识别的操作类型");
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	/**
	 * 修改节点
	 * @param data
	 * @param choosedNode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map edit(Map data, JSONObject choosedNode){
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("commId", choosedNode.getString("commId"));
		map.put("code", choosedNode.getString("code"));
		map.put("name", data.get("name"));
		String sql = "UPDATE C_CommStructure SET name = ?name WHERE code = ?code AND commId = ?commId";
		try {
			MySQLUtils.sqlExecuteMap(community, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "修改成功");
		} catch (SQLException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "修改失败：" + e);
		}
		return returnMap;
	}
	
	/**
	 * 新增同级节点
	 * @param data
	 * @param choosedNode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map add(Map data, JSONObject choosedNode){
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("commId", choosedNode.getString("commId"));
		map.put("sjCode", choosedNode.getString("sjCode"));
		map.put("isLast", choosedNode.getString("isLast"));
		map.put("isFirst", choosedNode.getString("isFirst"));
		map.put("newNodeCode", data.get("newNodeCode"));
		map.put("newNodeName", data.get("newNodeName"));
		String sql = "INSERT INTO C_CommStructure (`commId`, `name`, `code`, `sjCode`, `isLast`, `isFirst`) VALUES (?commId, ?newNodeName, ?newNodeCode, ?sjCode, ?isLast, ?isFirst);";
		try {
			MySQLUtils.sqlExecuteMap(community, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "新增成功");
		} catch (SQLException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "新增失败：" + e);
		}
		return returnMap;
	}
	
	/**
	 * 新增子级节点
	 * @param data
	 * @param choosedNode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map addSub(Map data, JSONObject choosedNode){
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("commId", choosedNode.getString("commId"));
		map.put("code", choosedNode.getString("code"));
		map.put("isLast", choosedNode.getString("isLast"));
		map.put("isFirst", choosedNode.getString("isFirst"));
		map.put("newNodeCode", data.get("newNodeCode"));
		map.put("newNodeName", data.get("newNodeName"));
		
		//新增一个子级节点，它肯定没有下级节点，即它肯定就是末级
		String sql1 = "INSERT INTO C_CommStructure (`commId`, `name`, `code`, `sjCode`, `isLast`, `isFirst`) VALUES (?commId, ?newNodeName, ?newNodeCode, ?code, '1', '0');";
		
		//如果在原有末级节点下新增子节点，那么原有的末级标记变为非末级
		boolean flag = choosedNode.getString("isLast").equals("1") ? true : false;
		String sql2 = "";
		if(flag){
			sql2 = "UPDATE C_CommStructure SET isLast = '0' WHERE code = ?code AND commId = ?commId";
		}
		
		Connection conn = null;
		try {
			conn = community.getDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement sta = conn.createStatement();
			
			sta.executeUpdate(SQLConvertor.format(sql1, map));
			if(flag){
				sta.executeUpdate(SQLConvertor.format(sql2, map));
			}
			
			conn.commit();	
			if(sta != null) sta.close();
			if(conn != null) conn.close();
			
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "新增成功");
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				//TODO 回滚失败
				System.out.println("回滚失败");
			}
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "新增失败：" + e);
		}
		return returnMap;
	}
	
	/**
	 * 删除节点
	 * @param data
	 * @param choosedNode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map del(Map data, JSONObject choosedNode){
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("commId", choosedNode.getString("commId"));
		map.put("code", choosedNode.getString("code"));
		
		//判断是否含有子节点
		boolean flag = Json.toJA(choosedNode.get("children")).size() > 0 ? true : false;
		List childList = new ArrayList();
		if(flag){
			childList = getAllChildren(choosedNode);
		}
		
		Connection conn = null;
		try {
			conn = community.getDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement sta = conn.createStatement();
			
			String sql = "UPDATE C_CommStructure SET validMark = '0' WHERE code = ?code AND commId = ?commId";
			sta.executeUpdate(SQLConvertor.format(sql, map));					//删除本节点
			if(flag){
				for (int i = 0; i < childList.size(); i++) {					//循环删除子节点
					Map childMap = Json.toMap(childList.get(i).toString());
					String childSql = "UPDATE C_CommStructure SET validMark = '0' WHERE code = ?code AND commId = ?commId";
					sta.executeUpdate(SQLConvertor.format(childSql, childMap));
				}
			}
			conn.commit();			
			if(sta != null) sta.close();
			if(conn != null) conn.close();
			
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "新增成功");
		} catch (SQLException e) {
			try {
				conn.rollback();												//若一条出现异常，全部回滚
			} catch (SQLException e1) {
				//TODO 回滚失败
				System.out.println("回滚失败");
			}
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "新增失败：" + e);
		}
		return returnMap;
	}

	/**
	 * 获取所有子节点，将所有子节点放到list中的同一级
	 * @param choosedNode： {name: '父节点2',code:'02', children: [{name: '子节点21',code:'0201', children: [{name: '子节点211',code:'020101'}]}]}
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAllChildren(JSONObject choosedNode){
		List returnAry = new ArrayList();
		JSONArray childrenAry = Json.toJA(choosedNode.get("children"));
		for (int i = 0; i < childrenAry.size(); i++) {
			JSONObject row = Json.toJO(childrenAry.get(i));
			returnAry.add(row);
			returnAry.addAll(getAllChildren(row));
		}
		return returnAry;
	}
	
	
	
}
