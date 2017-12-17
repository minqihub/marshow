package com.shopSystem;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.DataUtils;
import com.framework.utils.Json;

/**
 * 交易订单
 * @author minqi
 *
 */
public class Order {
	
	//订单状态STATE：orderOpen,orderPaid,oderCancel,orderRefund,orderClosed,orderFinished
	
	
	/**
	 * 生成订单
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map createOrder(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		String goodId = map.get("goodId").toString();		//商品编号
		String buyer = "";									//买家
		String seller = "";									//卖家
		String orderTime = DataUtils.getSysTime();			//订单时间
		
		return null;
	}
	
	

	/**
	 * 
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map queryOrder(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		
		return null;
	}
	
	

	/**
	 * 
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map updateOrder(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		
		return null;
	}
	
	
	
	
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
		sendJson.put("", "");
		
		Map returnMap = new HashMap();
		returnMap.put("MSGID", "S");
		returnMap.put("MESSAGE", "删除成功");
		
		
		return returnMap;
	}
	
}
