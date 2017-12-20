package com.commSystem;

import java.sql.SQLException;
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

		String sql = "SELECT * FROM C_COMM_STRUCTURE WHERE COMM_ID = '" + data.get("commId") +"' AND VALIDMARK = '1' ORDER BY CODE";
		List list = MySQLUtils.sqlQueryForList(DataSource.comm, sql);
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
//					JSONObject temp = new JSONObject();
//					temp.put("name", row.get("name"));
//					temp.put("code", row.get("code"));
//					returnAry.add(temp);
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
//					JSONObject childJson = new JSONObject();
//					childJson.put("name", row.getString("name"));
//					childJson.put("code", row.getString("code"));
//					childJson.put("children", handleData(childAry, row.get("code").toString()));
//					childAry.add(childJson);
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
		map.put("COMM_ID", choosedNode.getString("commId"));
		map.put("CODE", choosedNode.getString("code"));
		map.put("NAME", data.get("name"));
		String sql = "UPDATE C_COMM_STRUCTURE SET NAME = ?NAME WHERE CODE = ?CODE AND COMM_ID = ?COMM_ID";
		try {
			MySQLUtils.sqlExecuteMap(DataSource.comm, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "修改成功");
		} catch (Exception e) {
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
		String sql = "INSERT INTO C_COMM_STRUCTURE (commId, name, code, sjCode, isLast, isFirst) VALUES (?commId, ?newNodeName, ?newNodeCode, ?sjCode, ?isLast, ?isFirst);";
		try {
			MySQLUtils.sqlExecuteMap(DataSource.comm, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "新增成功");
		} catch (Exception e) {
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
		//TODO 新增子节点注意：如果在原有的末级节点下新增它的子节点，那么原有的末级标记变为非末级
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("commId", choosedNode.getString("commId"));
		map.put("code", choosedNode.getString("code"));
		map.put("isLast", choosedNode.getString("isLast"));
		map.put("isFirst", choosedNode.getString("isFirst"));
		map.put("newNodeCode", data.get("newNodeCode"));
		map.put("newNodeName", data.get("newNodeName"));
		String sql = "INSERT INTO C_COMM_STRUCTURE (commId, name, code, sjCode, isLast, isFirst) VALUES (?commId, ?newNodeName, ?newNodeCode, ?code, '1', '0')";
		try {
			MySQLUtils.sqlExecuteMap(DataSource.comm, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "新增成功");
		} catch (Exception e) {
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
		//TODO 需先判断是否含有子节点，一并删除？？？
		Map returnMap = new HashMap();
		Map map = new HashMap();
		map.put("COMM_ID", choosedNode.getString("commId"));
		map.put("CODE", choosedNode.getString("code"));
		String sql = "UPDATE C_COMM_STRUCTURE SET VALIDMARK = '0' WHERE CODE = ?CODE AND COMM_ID = ?COMM_ID";
		try {
			MySQLUtils.sqlExecuteMap(DataSource.comm, sql, map);
			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "删除成功");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "删除失败：" + e);
		}
		return returnMap;
	}

}
