package com.sys_shop;

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
	
	private final String[] ORDER_STATE = {"orderOpen","orderPaid","orderDelivering","orderReceived","orderClosed","orderRefund","orderFinished",""};
	private final String[] ORDER_STATE_ZN = {"待支付","待发货","待收货","待评价","订单取消","orderRefund","订单完成",""};
	/**
	 * 订单状态STATE：
	 * orderOpen（待支付）
	 * orderPaid（待发货）
	 * orderDelivering（待收货）
	 * orderReceived（待评价）
	 * orderClosed（订单取消）
	 * orderRefund（）
	 * orderFinished（订单完成）
	 */
	
	
	/**
	 * 生成订单
	 * @param json
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map createOrder(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		try {
			
		} catch (NullPointerException e) {
			
		}
		
		
		
		
		
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
