package com.thirdParty.weChat.wxPay;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.pay.PayUtils;
import com.framework.utils.DataUtils;
import com.framework.utils.Json;
import com.framework.utils.XmlUtils;

/**
 * 微信企业付款（原型方法，不含业务逻辑）
 * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_1
 * @author minqi 2017-12-14 16:02:53
 */
@Controller
@RequestMapping("/trust/WXMchPay")
public class WXMchPay {
	
	/**
	 * 企业付款到零钱（需要证书）
	 * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
	 * 需在微信商户平台开通企业付款功能，企业的付款账户和收款账户不是同一个账户，需要单独充值
	 * 注意：单笔最小1元；给同一用户打款间隔不低于15秒，不多于10次
	 * @param json 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toWallet.do")
	public Map toWallet(String json, HttpServletRequest request) {
		Map map = Json.toMap(json);		
		
		Map returnMap = new HashMap();
		try {
			String mch_appid = map.get("appid").toString();							//appid
			String mchid = map.get("mch_id").toString();							//微信支付分配的商户号
			String key = map.get("key").toString();									//微信商户号分配的密钥
			String nonce_str = PayUtils.getNonceStr();
			String partner_trade_no = map.get("partner_trade_no").toString();		//商户订单号（33位以下）
			String openid = map.get("openid").toString();							//用户openid
			
			String check_name = "";													//NO_CHECK：不校验真实姓名 ；FORCE_CHECK：强校验真实姓名
			String re_user_name = "";												//收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
			if(map.get("isCheckName").toString().equals("1")){
				check_name = "FORCE_CHECK";
				re_user_name = map.get("name").toString();
			}else{
				check_name = "NO_CHECK";
				re_user_name = "张三";
			}
			String amount = map.get("money").toString();							//企业付款金额，单位为分，最低100分
			String desc = !DataUtils.isNull(map.get("desc")) ? map.get("desc").toString() : "默认用户提现";		//企业付款描述信息
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
			
			packageParams.put("sign", sign);
			String postXml = XmlUtils.mapToXml(packageParams);
			
			String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
			String resultXml= WXPay.deployWithCert(url, postXml, map);			//需要验证证书
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
	
	
	/**
	 * 查询企业付款到零钱（需要证书）
	 * 只支持查询30天内的订单，30天之前的订单请登录商户平台查询
	 * @param json 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toWalletQuery.do")
	public Map toWalletQuery(String json, HttpServletRequest request) {
		Map map = Json.toMap(json);		
		
		Map returnMap = new HashMap();
		try {
			String appid = map.get("appid").toString();								//appid
			String mch_id = map.get("mch_id").toString();							//微信支付分配的商户号
			String key = map.get("key").toString();									//微信商户号分配的密钥
			String partner_trade_no = map.get("partner_trade_no").toString();		//商户订单号（8-32位）
			String nonce_str = PayUtils.getNonceStr();
			
			
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("partner_trade_no", partner_trade_no);
			packageParams.put("nonce_str", nonce_str);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String postXml = XmlUtils.mapToXml(packageParams);

			String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";
			String resultXml= WXPay.deployWithCert(url, postXml, map);			//需要验证证书
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
	
	
	
	/**
	 * 企业付款到银行卡（需要证书）
	 * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=24_2
	 * 注意：单商户日限额——单日100w；单次限额——单次5w；单商户给同一银行卡单日限额——单日5w
	 * @param json 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toBankCard.do")
	public Map toBankCard(String json, HttpServletRequest request) {
		Map map = Json.toMap(json);		
		
		Map returnMap = new HashMap();
		try {
			String mch_id = map.get("mch_id").toString();							//微信支付分配的商户号
			String key = map.get("key").toString();									//微信商户号分配的密钥
			String partner_trade_no = map.get("partner_trade_no").toString();		//商户订单号（8-32位）
			String nonce_str = PayUtils.getNonceStr();
			String enc_bank_no = map.get("enc_bank_no").toString();					//收款方银行卡号
			String enc_true_name = map.get("enc_true_name").toString();				//收款方用户名
			String bank_code = map.get("bank_code").toString();						//收款方开户行
			String amount = map.get("amount").toString();							//付款金额（单位分，最低100分）
			
			String desc = !DataUtils.isNull(map.get("desc")) ? map.get("desc").toString() : "默认";		//付款说明（100字符内）
			
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("mch_id", mch_id);
			packageParams.put("partner_trade_no", partner_trade_no);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("enc_bank_no", enc_bank_no);
			packageParams.put("enc_true_name", enc_true_name);
			packageParams.put("bank_code", bank_code);
			packageParams.put("amount", amount);
			packageParams.put("desc", desc);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String postXml = XmlUtils.mapToXml(packageParams);

			String url = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";
			String resultXml= WXPay.deployWithCert(url, postXml, map);			//需要验证证书
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
	
	
	/**
	 * 查询企业付款到银行卡（需要证书）
	 * @param json 所需参数：添加了注释的变量
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toBankCardQuery.do")
	public Map toBankCardQuery(String json, HttpServletRequest request) {
		Map map = Json.toMap(json);		
		
		Map returnMap = new HashMap();
		try {
			String mch_id = map.get("mch_id").toString();							//微信支付分配的商户号
			String key = map.get("key").toString();									//微信商户号分配的密钥
			String partner_trade_no = map.get("partner_trade_no").toString();		//商户订单号（8-32位）
			String nonce_str = PayUtils.getNonceStr();
			
			
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("mch_id", mch_id);
			packageParams.put("partner_trade_no", partner_trade_no);
			packageParams.put("nonce_str", nonce_str);
			
			String sign = PayUtils.getSignature(packageParams, key, "MD5");
			
			packageParams.put("sign", sign);
			String postXml = XmlUtils.mapToXml(packageParams);

			String url = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";
			String resultXml= WXPay.deployWithCert(url, postXml, map);			//需要验证证书
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
