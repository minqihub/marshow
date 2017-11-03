package com.thirdParty.weChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

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

import com.framework.file.XmlUtils;
import com.framework.pay.PayRoute;
import com.framework.pay.PayUtils;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 微信支付相关（原型方法，不含业务逻辑）
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 * @author minqi 2017-10-27 21:40:50
 *
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
	@RequestMapping("/protoPay.do")
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
			String trade_type = "JSAPI";
			String openid = map.get("openid").toString();					//openid
			String attach;
			if(map.containsKey("attach") && !DataUtils.isNull(map.get("attach"))){
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
			packageParams.put("openid", openid);
			packageParams.put("attach", attach);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			String payXml = "<xml>" + 
					"<appid>" + appid + "</appid>" + 
					"<attach>" + attach + "</attach>"+
					"<body>" + body + "</body>" + 
					"<mch_id>" + mch_id + "</mch_id>"+ 
					"<nonce_str>" + nonce_str + "</nonce_str>" + 
					"<notify_url>" + notify_url + "</notify_url>" + 
					"<openid>" + openid + "</openid>" +
					"<out_trade_no>" + out_trade_no + "</out_trade_no>" + 
					"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +				
					"<total_fee>" + total_fee + "</total_fee>" + 
					"<trade_type>" + trade_type + "</trade_type>" + 
					"<sign>" + sign + "</sign>" + 
					"</xml>";
			
//			String resultXml = (String) WeixinUtil.httpRequest(PayRoute.WeChatPayUrl, "POST", payXml, "1");
			String resultXml = HttpUtils.doPostString(PayRoute.WeChatPayUrl, null, null, payXml);
			
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("微信支付返回：" + resultMap);
			
			//避免空指针
			String return_code = resultMap.get("return_code") == null ? "" : resultMap.get("return_code").toString();
			String result_code = resultMap.get("result_code") == null ? "" : resultMap.get("result_code").toString();
			String return_msg = resultMap.get("return_msg") == null ? "" : resultMap.get("return_msg").toString();
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					Map packageParams2 = new HashMap();
					String timeStamp = Long.toString(System.currentTimeMillis());
					String nonceStr = PayUtils.getNonceStr();
					String wx_package = "prepay_id=" + resultMap.get("prepay_id").toString();
					
					packageParams2.put("appId", appid);
					packageParams2.put("timeStamp", timeStamp);
					packageParams2.put("nonceStr", nonceStr);
					packageParams2.put("package", wx_package);
					packageParams2.put("signType", "MD5");
					
					String sign2 = PayUtils.getSignature(packageParams2, key, "MD5");
					returnMap.put("flag", "1");
					returnMap.put("appId", appid);
					returnMap.put("timeStamp", timeStamp);
					returnMap.put("nonceStr", nonceStr);
					returnMap.put("package", wx_package);
					returnMap.put("signType", "MD5");
					returnMap.put("sign", sign2);
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
	 * 微信扫码支付
	 * 是否调用成功，根据"MSGID":"S"且"flag":"1"判断
	 * @param XmlData 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/qrPay.do")
	public Map qrPay(String json, HttpServletRequest request){
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
			if(map.containsKey("attach") && !DataUtils.isNull(map.get("attach"))){
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
			String payXml = "<xml>" + 
					"<appid>" + appid + "</appid>" + 
					"<attach>" + attach + "</attach>"+
					"<body>" + body + "</body>" + 
					"<mch_id>" + mch_id + "</mch_id>"+ 
					"<nonce_str>" + nonce_str + "</nonce_str>" + 
					"<notify_url>" + notify_url + "</notify_url>" + 
					"<out_trade_no>" + out_trade_no + "</out_trade_no>" + 
					"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +				
					"<total_fee>" + total_fee + "</total_fee>" + 
					"<trade_type>" + trade_type + "</trade_type>" + 
					"<sign>" + sign + "</sign>" + 
					"</xml>";
			
			String resultXml = HttpUtils.doPostString(PayRoute.WeChatPayUrl, null, null, payXml);
			
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
			
			String refundXml = "<xml>" + 
					"<appid>" + appid + "</appid>" + 
					"<mch_id>" + mch_id + "</mch_id>"+ 
					"<nonce_str>" + nonce_str + "</nonce_str>" + 
					"<out_refund_no>" + out_refund_no + "</out_refund_no>" + 
					tempParam +
					"<refund_fee>" + refund_fee + "</refund_fee>" + 
					"<total_fee>" + total_fee + "</total_fee>" + 
					"<sign>" + sign + "</sign>" + 
					"</xml>";

			//退款请求需要证书的双向验证；因为deployWithCert()方法被改动过，所以按此形式拼接configMap参数过去
			String refundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
			Map configMap = new HashMap();
			configMap.put("certPath", certPath);
			configMap.put("mch_id", mch_id);
			String resultXml = deployWithCert(refundUrl, refundXml , configMap);
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
     * 带证书的调用，微信双向证书验签
     * @param url 调用地址
     * @param data xml报文
     * @param configMap {"certPath"=证书路径,"mch_id"=微信支付商户号}
     * @return 加密结果
     * @throws Exception
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
	public String deployWithCert(String url, String data, Map configMap) throws Exception {
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
	
	/**
	 * 企业付款，即微信公众号向用户转账（需要证书）
	 * 需在微信商户平台开通企业付款功能，企业的付款账户和收款账户不是同一个账户，需要单独充值
	 * 注意：单笔最小1元；给同一用户打款间隔不低于15秒，不多于10次
	 * @param json 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getCash.do")
	public Map getCash(String json, HttpServletRequest request) {
		Map map = Json.toMap(json);		
		
		Map returnMap = new HashMap();
		try {
			String mch_appid = map.get("appid").toString();							//appid
			String mchid = map.get("mch_id").toString();							//微信支付分配的商户号
			String key = map.get("key").toString();									//微信商户号分配的密钥
			String nonce_str = PayUtils.getNonceStr();
			String partner_trade_no = map.get("partner_trade_no").toString();		//商户订单号（33位以下）
			String openid = map.get("openid").toString();							//用户openid
			String check_name = "NO_CHECK";											//NO_CHECK：不校验真实姓名 ；FORCE_CHECK：强校验真实姓名
			String re_user_name = "张三";											//收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
			String amount = map.get("money").toString();							//企业付款金额，单位为分，最低100分
			String desc = !DataUtils.isNull(map.get("desc")) ? map.get("desc").toString() : "余额佣金提现";		//企业付款描述信息
			String spbill_create_ip = request.getRemoteAddr();
			
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("mch_appid", mch_appid);
			packageParams.put("mchid", mchid);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("partner_trade_no", partner_trade_no);
			packageParams.put("openid", openid);
			packageParams.put("check_name", check_name);
			packageParams.put("re_user_name", re_user_name);
			packageParams.put("amount", amount);
			packageParams.put("desc", desc);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			String postXml = "<xml>" +
							"<mch_appid>" + mch_appid + "</mch_appid>" +
							"<mchid>" + mchid + "</mchid>" +
							"<nonce_str>" + nonce_str + "</nonce_str>" +
							"<partner_trade_no>" + partner_trade_no + "</partner_trade_no>" +
							"<openid>" + openid + "</openid>" +
							"<check_name>" + check_name+"</check_name>" +
							"<re_user_name>" + re_user_name + "</re_user_name>" +
							"<amount>" + amount + "</amount>" +
			 				"<desc>" + desc + "</desc>" +
							"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +
							"<sign>" + sign + "</sign>" +
							"</xml>";

			String resultXml= deployWithCert(PayRoute.WeChatGetCashUrl, postXml, map);			//需要验证证书
			Map resultMap = XmlUtils.xmlToMap(resultXml);
			System.out.println("！！！！！微信体现返回的returnMap：" + resultMap);
			
			String return_code = (String) resultMap.get("return_code");
			String result_code = (String) resultMap.get("result_code");
			
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					//成功转账的业务逻辑
					returnMap.put("flag", "1");			//不在此处执行业务，返回给调用者执行
					returnMap.put("MSGID", "S");
				} else {
					String err_code_des = resultMap.get("err_code_des").toString();
					returnMap.put("flag", "0");
					returnMap.put("err_code_des", err_code_des);
					returnMap.put("MSGID", "E");
				}
			} else {
				String return_msg = (String) resultMap.get("return_msg");
				returnMap.put("flag", "0");
				returnMap.put("err_code_des", return_msg);
				returnMap.put("MSGID", "E");
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少传入的支付参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信支付失败");
		}
		return returnMap;
	}
	
	
	
}
