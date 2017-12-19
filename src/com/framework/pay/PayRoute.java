package com.framework.pay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.Ali.aliPay.AliPay;
import com.thirdParty.weChat.wxPay.WXPay;

/**
 * 支付配置与路由
 * @author minqi 2017-10-27 22:01:28
 *
 */
@Controller
@RequestMapping("/trust/payRoute")
public class PayRoute {

	
	//支付方式PAYWAY：cashPay,weChatPay,aliPay,bankCardPay
	
	
	
	
	/**
	 * 获取支付配置
	 * @param json 参数：SERVICE_ID、PAYTYPE
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getPayConfig(Map data) {
		Map returnMap = new HashMap();
		try {
			Map queryMap = new HashMap();
			queryMap.put("SERVICE_ID", data.get("SERVICE_ID").toString());
			queryMap.put("PAYTYPE", data.get("PAYTYPE").toString());
			
			String sqlTemplate = "SELECT * FROM S_PAYMENT_CONFIG WHERE SERVICE_ID = ?SERVICE_ID AND PAYTYPE = ?PAYTYPE";
			returnMap = MySQLUtils.sqlQueryForMap(DataSource.comm, sqlTemplate, queryMap);
			
			if(returnMap.isEmpty()){
				returnMap.put("MSGID", "E");
			}else{
				returnMap.put("MSGID", "S");
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "其他异常：" + e);
		}
		return returnMap;
	}

	
	
	/**
	 * 客户端直接调用支付
	 * @param json 参数：SERVICE_ID、PAYTYPE、payMoney、orderId、describe、extra、openid（微信必填）、productId（支付宝必填）、、、
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/customerPay.do")
	public Map customerPay(String json, HttpServletRequest request, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		//获取支付配置
		Map configMap = getPayConfig(data);
		
		Map returnMap = new HashMap();
		if(configMap.get("MSGID").equals("S")){
			
			try {
				Map payParams = new HashMap();
				if(data.get("PAYTYPE").toString().equals("weChatPay")){
					
					payParams.put("appid", configMap.get("WX_APPID"));
					payParams.put("mch_id", configMap.get("WX_MCH_ID"));
					payParams.put("key", configMap.get("WX_KEY"));
					payParams.put("body", data.get("describe"));
					payParams.put("out_trade_no", data.get("orderId").toString());
					payParams.put("total_fee", Double.parseDouble(data.get("payMoney").toString()) * 100);				//注意微信支付金额单位：分
					payParams.put("spbill_create_ip", request.getRemoteAddr());
					payParams.put("notify_url", configMap.get("WX_NOTIFY_URL"));
					payParams.put("openid", data.get("openid").toString());
					payParams.put("attach", data.get("extra"));
					
					returnMap = WXPay.weChatPay(payParams);
					
				}else if(data.get("PAYTYPE").toString().equals("aliPay")){

					payParams.put("app_id", configMap.get("ALI_APPID"));
					payParams.put("rsa_private_key", configMap.get("ALI_PRIVATE_KEY"));
					payParams.put("public_key", configMap.get("ALI_PUBLIC_KEY"));
					payParams.put("body", "");
					payParams.put("passback_params", data.get("extra"));
					payParams.put("out_trade_no", data.get("orderId").toString());
					payParams.put("subject", data.get("describe"));													//订单名称
					payParams.put("total_amount", Double.parseDouble(data.get("payMoney").toString()));				//注意支付宝支付金额单位：元
					payParams.put("product_code", data.get("productId").toString());
					payParams.put("notify_url", configMap.get("NOTIFY_URL"));
					payParams.put("return_url", configMap.get("ALI_RETURN_URL"));

					payParams.put("isPcPay", "");
					
					returnMap = AliPay.aliPay(configMap);
					
				}else{
					System.out.println("其他类型的支付");
					returnMap.put("MSGID", "E");
				}
			} catch (NullPointerException e) {
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", "空指针异常：" + e);
			}
		}else{
			returnMap = configMap;
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
}
