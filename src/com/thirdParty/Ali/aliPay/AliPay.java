package com.thirdParty.Ali.aliPay;

import java.util.HashMap;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.framework.utils.DataUtils;

/**
 * 支付宝支付（原型方法，不含业务逻辑）
 * https://docs.open.alipay.com/203/105285/
 * @author minqi 2017-12-15 10:00:25
 */
public class AliPay {

	private static final String url = "https://openapi.alipay.com/gateway.do";
	private static final String format = "JSON";
	private static final String charset = "UTF-8";
	private static final String sign_type = "RSA2";
	
	/**
	 * 简易封装初始化AlipayClient对象
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static AlipayClient getAlipayClient(Map map){
		String app_id = map.get("app_id").toString();						//应用ID
		String rsa_private_key = map.get("rsa_private_key").toString();		//应用密钥
		String public_key = map.get("public_key").toString();				//应用公钥
		
		//SDK公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签

		//TODO 加入try catch，测试使用错误信息初始化的时候，是否在此处抛出异常
	    AlipayClient client = new DefaultAlipayClient(url, app_id, rsa_private_key, format, charset, public_key, sign_type);
		return client;
	}
	
	
	/**
	 * 支付宝支付
	 * 手机网站支付：https://docs.open.alipay.com/203
	 * 电脑网站支付：https://docs.open.alipay.com/270/alipay.trade.page.pay
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map aliPay(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
			
			//可空的业务请求参数
		    String body = map.get("body").toString();							//商品描述，可空
		    String timeout_express = "2m"; 										//超时时间，可空
		    String passback_params = map.get("passback_params").toString(); 	//公共回传参数，可空

		    String formData;
		    if(DataUtils.isNull(map.get("isPcPay"))){							//默认wap支付（移动端，唤起手机支付宝支付）
			    //封装请求支付信息
			    AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
			    model.setOutTradeNo(map.get("out_trade_no").toString());		//商户订单号，必填
			    model.setSubject(map.get("subject").toString());				//订单名称，必填
			    model.setTotalAmount(map.get("total_amount").toString());		//付款金额，必填
			    model.setProductCode(map.get("product_code").toString());		//销售产品码， 必填
			    model.setBody(body);
			    model.setTimeoutExpress(timeout_express);
			    model.setPassbackParams(passback_params);
			    
			    //调用RSA签名方式
			    AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
			    alipay_request.setBizModel(model);
			    alipay_request.setNotifyUrl(map.get("notify_url").toString());	//异步通知地址
			    alipay_request.setReturnUrl(map.get("return_url").toString());	//同步通知地址
			    
			    //调用SDK生成表单，form表单生产
			    formData = client.pageExecute(alipay_request).getBody();
		    }else{																//电脑支付宝收银台支付
			    //封装请求支付信息
		    	AlipayTradePagePayModel model = new AlipayTradePagePayModel();
			    model.setOutTradeNo(map.get("out_trade_no").toString());		//商户订单号，商户网站订单系统中唯一订单号，必填
			    model.setSubject(map.get("subject").toString());				//订单名称，必填
			    model.setTotalAmount(map.get("total_amount").toString());		//付款金额，必填
			    model.setProductCode(map.get("product_code").toString());		//销售产品码， 必填
			    model.setTimeoutExpress(timeout_express);
			    model.setPassbackParams(passback_params);
			    
			    //调用RSA签名方式
			    AlipayTradePagePayRequest alipay_request = new AlipayTradePagePayRequest();
			    alipay_request.setBizModel(model);
			    alipay_request.setNotifyUrl(map.get("notify_url").toString());	//异步通知地址
			    alipay_request.setReturnUrl(map.get("return_url").toString());	//同步通知地址
			    
			    //调用SDK生成表单，form表单生产
			    formData = client.pageExecute(alipay_request).getBody();
		    }
		    System.out.println(formData);
//			response.setContentType("text/html;charset=" + charset); 
//		    response.getWriter().write(form);							//直接将完整的表单html输出到页面 
//		    response.getWriter().flush(); 
//		    response.getWriter().close();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", formData);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	
	/**
	 * 交易查询
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map queryTrade(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
		    
		    //封装请求支付信息
		    AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		    model.setOutTradeNo(map.get("out_trade_no").toString());			//商户订单号，必填
		 	model.setTradeNo(map.get("trade_no").toString());					//订单名称，必填
		    
		 	AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
		    alipay_request.setBizModel(model);
		    AlipayTradeQueryResponse alipay_response = client.execute(alipay_request);
		    String result = alipay_response.getBody();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", result);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	
	/**
	 * 交易退款
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map refund(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
		    
		    //封装请求支付信息
		    AlipayTradeRefundModel model = new AlipayTradeRefundModel();
			model.setOutTradeNo(map.get("out_trade_no").toString());			//商户订单号（二选一）
			model.setTradeNo(map.get("trade_no").toString());					//支付宝交易号（二选一）同时存在优先取trade_no
			model.setRefundAmount(map.get("refund_amount").toString());			//退款金额
			model.setRefundReason(map.get("refund_reason").toString());			//退款的原因说明
			model.setOutRequestNo(map.get("out_request_no").toString());		//标记一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
		    
			AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
		    alipay_request.setBizModel(model);
		    AlipayTradeRefundResponse alipay_response = client.execute(alipay_request);
		    String result = alipay_response.getBody();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", result);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	
	/**
	 * 查询交易退款
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map queryRefund(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
		    
		    //封装请求支付信息
		    AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
			model.setOutTradeNo(map.get("out_trade_no").toString());			//商户订单号（二选一）
			model.setTradeNo(map.get("trade_no").toString());					//支付宝交易号（二选一）同时存在优先取trade_no
			model.setOutRequestNo(map.get("out_request_no").toString());		//标记一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
		    
			AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();
		    alipay_request.setBizModel(model);
		    AlipayTradeFastpayRefundQueryResponse alipay_response = client.execute(alipay_request);
		    String result = alipay_response.getBody();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", result);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	/**
	 * 交易关闭
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map closeTrade(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
		    
		    //封装请求支付信息
		    AlipayTradeCloseModel model = new AlipayTradeCloseModel();
			model.setOutTradeNo(map.get("out_trade_no").toString());			//商户订单号（二选一）
			model.setTradeNo(map.get("trade_no").toString());					//支付宝交易号（二选一）同时存在优先取trade_no
		    
			AlipayTradeCloseRequest alipay_request=new AlipayTradeCloseRequest();
		    alipay_request.setBizModel(model);
		    AlipayTradeCloseResponse alipay_response=client.execute(alipay_request);
		    String result = alipay_response.getBody();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", result);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	
	/**
	 * 下载对账单
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map downloadBill(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient client = getAlipayClient(map);
		    
		    //封装请求支付信息
		    AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
		    model.setBillType(map.get("bill_type").toString());					// 账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型：trade、signcustomer；
																				// trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单；
			model.setBillDate(map.get("bill_date").toString());					//账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM
		    
			AlipayDataDataserviceBillDownloadurlQueryRequest alipay_request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
		    alipay_request.setBizModel(model);
		    AlipayDataDataserviceBillDownloadurlQueryResponse alipay_response = client.execute(alipay_request);
		    String result = alipay_response.getBody();

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", result);
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (AlipayApiException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "支付宝内部异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请他异常：" + e);
		}
		return returnMap;
	}
	
	
}
