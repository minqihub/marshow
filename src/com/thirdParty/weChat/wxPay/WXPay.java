package com.thirdParty.weChat.wxPay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.pay.PayUtils;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.framework.utils.XmlUtils;

/**
 * 微信支付相关（原型方法，不含业务逻辑）
 * 通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 * @author minqi 2017-10-27 21:40:50
 */
@Controller
@RequestMapping("/trust/WXPay")
public class WXPay {
	
	/**
	 * 微信客户端支付
	 * 微信内H5调起支付；是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/weChatPay.do")
	public Map weChatPay(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String body = map.get("body").toString();						//商品名称
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			String total_fee = map.get("total_fee").toString();				//支付总金额，单位分
			String spbill_create_ip = request.getRemoteAddr();
			String notify_url = map.get("notify_url").toString();			//回调地址
			String trade_type = "JSAPI";
			String openid = map.get("openid").toString();					//openid
			String attach;
			if(!DataUtils.isNull(map.get("attach"))){
				attach = map.get("attach").toString();						//额外参数（可空）
			}else{
				//参与验签的参数不能为空，故给定非空字符串
				attach = "empty";
			}

			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("total_fee", total_fee);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("notify_url", notify_url);
			packageParams.put("trade_type", trade_type);
			packageParams.put("openid", openid);
			packageParams.put("attach", attach);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String WeChatPayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			String resultXml = HttpUtils.doPostString(WeChatPayUrl, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信支付返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					String timeStamp = Long.toString(System.currentTimeMillis());
					String nonceStr = PayUtils.getNonceStr();
					String wx_package = "prepay_id=" + resultMap.get("prepay_id").toString();
					
					returnMap.put("appid", appid);
					returnMap.put("timeStamp", timeStamp);
					returnMap.put("nonceStr", nonceStr);
					returnMap.put("package", wx_package);
					returnMap.put("signType", "MD5");
					
					String sign2 = PayUtils.getSignature(returnMap, key, "MD5");
					returnMap.put("flag", "1");
					returnMap.put("sign", sign2);
					
					//以下参数是扫码支付（模式一回调所需参数）
					returnMap.put("mch_id", mch_id);
					returnMap.put("prepay_id", resultMap.get("prepay_id").toString());
					returnMap.put("return_code", return_code);
					returnMap.put("result_code", result_code);
				} else {
					returnMap.put("flag", "0");
					returnMap.put("err", return_msg);
				}
			}else{
				returnMap.put("flag", "0");
				returnMap.put("err", return_msg);
			}
			returnMap.put("MSGID", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信支付参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		System.out.println("微信支付最终返回结果："+returnMap);
		return returnMap;
	}
	
	/**
	 * 在微信客户端外的页面使用微信支付,从外部浏览器唤起微信支付
	 * https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=9_20&index=1
	 * 微信内H5调起支付；是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/h5Pay.do")
	public Map h5Pay(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String body = map.get("body").toString();						//商品名称
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			String total_fee = map.get("total_fee").toString();				//支付总金额，单位分
			String spbill_create_ip = request.getRemoteAddr();
			String notify_url = map.get("notify_url").toString();			//回调地址
			String trade_type = "MWEB";
			
			JSONObject sceneJson = new JSONObject();
			JSONObject h5_info = new JSONObject();
			h5_info.put("type", "Wap");//场景类型
			h5_info.put("wap_url", map.get("wap_url").toString());			//WAP网站URL地址
			h5_info.put("wap_name", map.get("wap_name").toString());		//WAP网站名
			sceneJson.put("h5_info", h5_info);
			
			String scene_info = sceneJson.toString();
			
			String attach;
			if(!DataUtils.isNull(map.get("attach"))){
				attach = map.get("attach").toString();						//额外参数（可空）
			}else{
				//参与验签的参数不能为空，故给定非空字符串
				attach = "empty";
			}

			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("total_fee", total_fee);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("notify_url", notify_url);
			packageParams.put("trade_type", trade_type);
			packageParams.put("scene_info", scene_info);
			packageParams.put("attach", attach);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信外部H5支付返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					
				
				} else {
					returnMap.put("flag", "0");
					returnMap.put("err", return_msg);
				}
			}else{
				returnMap.put("flag", "0");
				returnMap.put("err", return_msg);
			}
			returnMap.put("MSGID", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信支付参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		System.out.println("微信支付最终返回结果："+returnMap);
		return returnMap;
	}
	
	/**
	 * 刷卡支付，扫微信用户的付款码
	 * https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_10&index=1
	 * 是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/microPay.do")
	public Map microPay(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String body = map.get("body").toString();						//商品名称
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			String total_fee = map.get("total_fee").toString();				//支付总金额，单位分
			String spbill_create_ip = request.getRemoteAddr();
			String auth_code = map.get("auth_code").toString();				//扫码支付授权码，设备读取用户微信中的条码或者二维码信息
			
			JSONObject sceneJson = new JSONObject();
			JSONObject store_info = new JSONObject();
			store_info.put("id", map.get("id").toString());					//门店id
			store_info.put("name", map.get("name").toString());				//门店名称
			store_info.put("area_code", map.get("area_code").toString());	//门店行政区划码
			store_info.put("address", map.get("address").toString());		//门店详细地址 
			sceneJson.put("store_info", store_info);
			
			String scene_info = sceneJson.toString();
			
			String attach;
			if(!DataUtils.isNull(map.get("attach"))){
				attach = map.get("attach").toString();						//额外参数（可空）
			}else{
				//参与验签的参数不能为空，故给定非空字符串
				attach = "empty";
			}

			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("total_fee", total_fee);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("auth_code", auth_code);
			packageParams.put("scene_info", scene_info);
			packageParams.put("attach", attach);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
//			String payXml = "<xml>" + 
//					"<appid>" + appid + "</appid>" + 
//					"<attach>" + attach + "</attach>"+
//					"<body>" + body + "</body>" + 
//					"<mch_id>" + mch_id + "</mch_id>"+ 
//					"<nonce_str>" + nonce_str + "</nonce_str>" + 
//					"<out_trade_no>" + out_trade_no + "</out_trade_no>" + 
//					"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +				
//					"<total_fee>" + total_fee + "</total_fee>" + 				
//					"<auth_code>" + auth_code + "</auth_code>" + 
//					"<scene_info>" + scene_info + "</scene_info>" + 
//					"<sign>" + sign + "</sign>" + 
//					"</xml>";
			
			String WeChatMicroUrl = "https://api.mch.weixin.qq.com/pay/micropay";
			String resultXml = HttpUtils.doPostString(WeChatMicroUrl, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信刷卡支付返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					
				
				} else {
					returnMap.put("flag", "0");
					returnMap.put("err", return_msg);
				}
			}else{
				returnMap.put("flag", "0");
				returnMap.put("err", return_msg);
			}
			returnMap.put("MSGID", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信支付参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		System.out.println("微信支付最终返回结果："+returnMap);
		return returnMap;
	}
	
	/**
	 * 微信扫码支付（模式一）
	 * 商户必须在公众平台后台设置支付回调URL。URL实现的功能：接收用户扫码后微信支付系统回调的productid和openid
	 * 是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/qrPay1.do")
	public Map qrPay1(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String product_id = map.get("product_id").toString();			//商户定义的商品id或者订单号
			String nonce_str = PayUtils.getNonceStr();
			String time_stamp = Long.toString(System.currentTimeMillis() / 1000);

			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("product_id", product_id);
			packageParams.put("time_stamp", time_stamp);
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			//weixin：//wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX
			String qrCodeUrl = "weixin://wxpay/bizpayurl?"
					+ "sign=" + sign
					+ "&appid=" + appid
					+ "&mch_id=" + mch_id
					+ "&product_id=" + product_id
					+ "&time_stamp=" + time_stamp
					+ "&nonce_str=" + nonce_str;
			
			returnMap.put("MSGID", "S");
			returnMap.put("qrCodeUrl", qrCodeUrl);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		return returnMap;
	}
	
	/**
	 * 微信扫码支付回调地址（模式一）
	 * 商户必须在公众平台后台设置支付回调URL。URL实现的功能：接收用户扫码后微信支付系统回调的productid和openid
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/qrPay1_Notify.do")
	public void qrPay1_Notify(HttpServletRequest request, HttpServletResponse response){
		
		JSONObject json = new JSONObject();
		json.put("appid", request.getParameter("appid"));
		json.put("openid", request.getParameter("openid"));
		json.put("mch_id", request.getParameter("mch_id"));
		json.put("is_subscribe", request.getParameter("is_subscribe"));	//是否关注公众账号：Y关注；N未关注
		json.put("nonce_str", request.getParameter("nonce_str"));
		json.put("product_id", request.getParameter("product_id"));
		json.put("sign", request.getParameter("sign"));
		
		//调用统一下单接口
		Map returnMap = this.weChatPay(json.toString(), request);
		
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
            pw.print(returnMap);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
            pw.close();
        }
	}
	
	/**
	 * 微信扫码支付（模式二）
	 * 是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/qrPay2.do")
	public Map qrPay2(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String body = map.get("body").toString();						//商品名称
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			String total_fee = map.get("total_fee").toString();				//支付总金额，单位分
			String spbill_create_ip = request.getRemoteAddr();
			String notify_url = map.get("notify_url").toString();			//回调地址
			String trade_type = "NATIVE";
			String attach;
			if(!DataUtils.isNull(map.get("attach"))){
				attach = map.get("attach").toString();						//额外参数，可空
			}else{
				//参与验签的参数不能为空，故给定非空字符串
				attach = "empty";
			}

			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("total_fee", total_fee);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("notify_url", notify_url);
			packageParams.put("trade_type", trade_type);
			packageParams.put("attach", attach);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信支付返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					String code_url = returnMap.get("code_url").toString();
					String prepay_id = returnMap.get("prepay_id").toString();
					returnMap.put("flag", "1");
					returnMap.put("code_url", code_url);
					returnMap.put("prepay_id", prepay_id);
				} else {
					returnMap.put("flag", "0");
					returnMap.put("err", return_msg);
				}
			}else{
				returnMap.put("flag", "0");
				returnMap.put("err", return_msg);
			}
			returnMap.put("MSGID", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信支付参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		System.out.println("微信扫码支付最终返回结果："+returnMap);
		return returnMap;
	}
	
	/**
	 * 查询订单
	 * 是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/queryOrder.do")
	public Map queryOrder(String json, HttpServletRequest request){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();

			//判断二选一参数
			String transaction_id = "";
			String out_trade_no = "";
			String tempParam = "";
			if(!DataUtils.isNull(map.get("transaction_id"))){
				transaction_id = map.get("transaction_id").toString();		//微信内部订单号
				tempParam = "<transaction_id>" + transaction_id + "</transaction_id>";
			}else{
				out_trade_no = map.get("out_trade_no").toString();			//商户订单号
				tempParam = "<out_trade_no>" + out_trade_no + "</out_trade_no>";
			}
			
			
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String WeChatQueryOrderUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
			String resultXml = HttpUtils.doPostString(WeChatQueryOrderUrl, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信查询订单返回：" + resultMap);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信支付参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		return returnMap;
	}
	
	/**
	 * 关闭订单
	 * 以下情况需要调用关单接口：商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。 
注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。 
	 * 发起微信退款；是否退款成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param json 所需参数：添加了注释的变量
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/closeOrder.do")
	public Map closeOrder(String json){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			
			
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/pay/closeorder";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信关闭订单返回：" + resultMap);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信退款参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信退款失败");
		}
		System.out.println("微信关闭订单最终返回结果："+returnMap);
		return returnMap;
	}
	
	/**
	 * 微信退款（需要证书）
	 * 参数transaction_id和out_trade_no二选一，若同时存在则transaction_id优先
	 * 发起微信退款；是否退款成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param json 所需参数：添加了注释的变量
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/refund.do")
	public Map refund(String json){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String certPath = map.get("certPath").toString();				//证书路径
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			
			//判断二选一参数
			String transaction_id = "";
			String out_trade_no = "";
			String tempParam = "";
			if(!DataUtils.isNull(map.get("transaction_id"))){
				transaction_id = map.get("transaction_id").toString();		//微信内部订单号
				tempParam = "<transaction_id>" + transaction_id + "</transaction_id>";
			}else{
				out_trade_no = map.get("out_trade_no").toString();			//商户订单号
				tempParam = "<out_trade_no>" + out_trade_no + "</out_trade_no>";
			}
			
			String out_refund_no = map.get("out_refund_no").toString();		//商户退款单号
			String total_fee = map.get("total_fee").toString();				//支付总金额，单位分
			String refund_fee = map.get("refund_fee").toString();			//退款总金额，单位分

			//签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_refund_no", out_refund_no);
			if(!transaction_id.equals("")){
				packageParams.put("transaction_id", transaction_id);
			}else{
				packageParams.put("out_trade_no", out_trade_no);
			}
			packageParams.put("refund_fee", total_fee);
			packageParams.put("total_fee", total_fee);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String refundXml = XmlUtils.mapToXml(packageParams);

			//退款请求需要证书的双向验证；因为deployWithCert()方法被改动过，所以按此形式拼接configMap参数过去
			Map configMap = new HashMap();
			configMap.put("certPath", certPath);
			configMap.put("mch_id", mch_id);
			
			String WeChatRefundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
			String resultXml = deployWithCert(WeChatRefundUrl, refundXml , configMap);
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信退款返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					returnMap.put("transaction_id", resultMap.get("transaction_id"));	//微信订单号
					returnMap.put("out_trade_no", resultMap.get("out_trade_no"));		//商户订单号
					returnMap.put("out_refund_no", resultMap.get("out_refund_no"));		//商户退款单号
					//微信返回的退款单号refund_id
					returnMap.put("refund_id", resultMap.get("refund_id"));				//微信退款单号
					returnMap.put("flag", "1");
				} else {
					returnMap.put("flag", "0");
					returnMap.put("err", return_msg);
				}
			}else{
				returnMap.put("flag", "0");
				returnMap.put("err", return_msg);
			}
			returnMap.put("MSGID", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信退款参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信退款失败");
		}
		System.out.println("微信退款最终返回结果："+returnMap);
		return returnMap;
	}

	/**
	 * 查询退款
	 * 退款有一定延时，用零钱支付的退款20分钟内到账，银行卡支付的退款3个工作日后重新查询退款状态。
		注意：如果单个支付订单部分退款次数超过20次请使用退款单号查询
	 * @param json 所需参数：添加了注释的变量
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/queryRefund.do")
	public Map queryRefund(String json){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			
			
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/pay/refundquery";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信关闭订单返回：" + resultMap);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信退款参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信退款失败");
		}
		System.out.println("微信关闭订单最终返回结果："+returnMap);
		return returnMap;
	}
	
	
	/**
	 * 下载对账单
	 * @param json 所需参数：添加了注释的变量
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/queryRefund.do")
	public Map downloadBill(String json){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			
			
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/pay/downloadbill";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信关闭订单返回：" + resultMap);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信退款参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信退款失败");
		}
		System.out.println("微信关闭订单最终返回结果："+returnMap);
		return returnMap;
	}
	
	
	/**
	 * 拉取订单评价数据
	 * @param json 所需参数：添加了注释的变量
	 * @author minqi
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/queryComment.do")
	public Map queryComment(String json){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();						//appid
			String mch_id = map.get("mch_id").toString();					//微信支付商户号
			String key = map.get("key").toString();							//微信支付商户密钥
			String nonce_str = PayUtils.getNonceStr();
			String out_trade_no = map.get("out_trade_no").toString();		//商户订单号
			
			
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String payXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/billcommentsp/batchquerycomment";
			String resultXml = HttpUtils.doPostString(url, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信关闭订单返回：" + resultMap);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的参数");
		} catch (IOException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信退款参数转换失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信退款失败");
		}
		System.out.println("微信关闭订单最终返回结果："+returnMap);
		return returnMap;
	}

    /**
     * 带证书的调用，微信双向证书验签
     * @param url 调用地址
     * @param data xml报文
     * @param configMap {"certPath"=证书路径,"mch_id"=微信支付商户号}
     * @return 加密结果
     * @throws Exception
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
	public static String deployWithCert(String url, String data, Map configMap) throws Exception {
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(configMap.get("certPath").toString()));	//P12文件目录
        try {
            keyStore.load(instream, configMap.get("mch_id").toString().toCharArray());						//这里写密码..默认是你的MCHID
        } finally {
            instream.close();
        }
        
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, configMap.get("mch_id").toString().toCharArray()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        
        try {
        	HttpPost httpost = new HttpPost(url); // 设置响应头信息
        	httpost.addHeader("Connection", "keep-alive");
        	httpost.addHeader("Accept", "*/*");
        	httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        	httpost.addHeader("Host", "api.mch.weixin.qq.com");
        	httpost.addHeader("X-Requested-With", "XMLHttpRequest");
        	httpost.addHeader("Cache-Control", "max-age=0");
        	httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
    		httpost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
            try {
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
	
}
