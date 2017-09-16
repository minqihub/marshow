package com.shopSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.framework.utils.DataUtils;
import com.framework.utils.Json;

/**
 * 购物车
 * @author minqi
 *
 */
public class Cart {
	
	
	
	/**
	 * 查找购物车
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List findCart(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		String userId = map.get("userId").toString();
		
		
		
		
		
		
		return null;
	}
	
	/**
	 * 添加到购物车
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map addCart(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);

		String userId = map.get("userId").toString();
		String goodId = map.get("goodId").toString();					//商品编码
		
		//如果没有数量，则默认1
		int number = 1;
		if(!DataUtils.isNull(map.get("number"))){
			number = Integer.parseInt(map.get("number").toString());	//增加的数量
		}
		
		//查询该用户、该商品的购物车数据
		
		//判断是新增购物车数据，还是修改购物车数量
		if(true){
			
		}else{
			
		}
		
		
		
		return null;
	}
	
	/**
	 * 删除购物车商品
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map delCart(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);

		String userId = map.get("userId").toString();
		String goodId = map.get("goodId").toString();					//商品编码
		
		Map returnMap = new HashMap();
		
		//删除购物车
		try {
			
		} catch (Exception e) {
			//回滚
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "删除失败");
		}
		
		
		returnMap.put("MSGID", "S");
		returnMap.put("MESSAGE", "删除成功");
		
		
		return returnMap;
	}
	
	
	
	
	
}
