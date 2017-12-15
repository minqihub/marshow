package com.thirdParty.Ali.aliPay;

import java.util.HashMap;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;

/**
 * 支付宝商户支付相关
 * 资金能力API：https://docs.open.alipay.com/309/106237/
 * @author minqi 2017-12-15 13:21:28
 */
public class AliMchPay {
	
	/**
	 * 单笔转账到支付宝账户
	 * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map transferToAccount(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient alipayClient = AliPay.getAlipayClient(map);
			
		    //封装请求支付信息
		    AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
		    model.setOutBizNo(map.get("out_biz_no").toString());				//商户订单号，商户网站订单系统中唯一订单号，必填
		    model.setPayeeType(map.get("payee_type").toString());				//ALIPAY_USERID；ALIPAY_LOGONID
		    model.setPayeeAccount(map.get("payee_account").toString());			//收款方账户（以2088开头的16位纯数字；手机号/邮箱）
		    model.setAmount(map.get("amount").toString());						//转账金额，单位：元（必须大于0.1元）
		    model.setPayerShowName(map.get("payer_show_name").toString());		//付款方姓名，可空（默认显示付款方的支付宝认证姓名或单位名称）
		    model.setPayeeRealName(map.get("payee_real_name").toString());		//真实姓名，可空（若非空则会校验真实姓名）
		    model.setRemark(map.get("remark").toString());						//转账备注（支持200个英文/100个汉字）
		    
		 	AlipayFundTransToaccountTransferRequest alipay_request = new AlipayFundTransToaccountTransferRequest();
		    alipay_request.setBizModel(model);
		    AlipayFundTransToaccountTransferResponse alipay_response = alipayClient.execute(alipay_request);
		    String result = alipay_response.getBody();
		    
		    if(alipay_response.isSuccess()){
		    	System.out.println("调用成功");
				returnMap.put("MSGID", "S");
				returnMap.put("MESSAGE", result);
		    } else {
		    	System.out.println("调用失败");
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", result);
		    }
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
	 * 单笔转账到支付宝账户
	 * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
	 * @param map 所需参数：添加了注释的变量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map quetyTransferToAccount(Map map){
		Map returnMap = new HashMap();
		try {
		    AlipayClient alipayClient = AliPay.getAlipayClient(map);
			
		    //封装请求支付信息
		    AlipayFundTransOrderQueryModel model = new AlipayFundTransOrderQueryModel();
		    model.setOutBizNo(map.get("out_biz_no").toString());				//商户订单号，商户网站订单系统中唯一订单号，必填
		    model.setOrderId(map.get("order_id").toString());					//支付宝转账单据号（二者不能同时为空）
		    
		    AlipayFundTransOrderQueryRequest alipay_request = new AlipayFundTransOrderQueryRequest();
		    alipay_request.setBizModel(model);
		    AlipayFundTransOrderQueryResponse alipay_response = alipayClient.execute(alipay_request);
		    String result = alipay_response.getBody();
		    
		    if(alipay_response.isSuccess()){
		    	System.out.println("调用成功");
				returnMap.put("MSGID", "S");
				returnMap.put("MESSAGE", result);
		    } else {
		    	System.out.println("调用失败");
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", result);
		    }
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
