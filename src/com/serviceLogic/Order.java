package com.serviceLogic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.Json;

/**
 * 交易订单
 * @author minqi
 *
 */
public class Order {
	
	
	
	/**
	 * 再来一单
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getAnother(String json, HttpServletRequest request, HttpServletResponse response){
		Map map = Json.toMap(json);

		String userId = map.get("userId").toString();
		String orderId = map.get("goodId").toString();					//订单号

		//查找订单数据
		JSONObject sendJson = new JSONObject();
		sendJson.put("", value);
		
		Map returnMap = new HashMap();
		returnMap.put("MSGID", "S");
		returnMap.put("MESSAGE", "删除成功");
		
		
		return returnMap;
	}
	
}
