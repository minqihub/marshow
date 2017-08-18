package com.framework.pay.weChatPay;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.DataUtils;
import com.framework.utils.Json;

@Controller
@RequestMapping("/trust/weChatOnline")
public class WeChatOnline {
		
		/**
		 * 用户提现：微信公众号向用户转账（需在微信商户平台开通企业付款功能，企业的付款账户和收款账户不是同一个账户，需要单独充值）
		 * @param json 注意：单笔最小1元；给同一用户打款间隔不低于15秒，不多于10次
		 * @param request
		 * @return
		 * @throws Exception 
		 */
	/*	
	@RequestMapping("/getWeChatCash.do")
		public Map getWeChatCash(String json, HttpServletRequest request) throws Exception{
			//http:localhost:8080/jlo2o/trust/weChatOnline/getWeChatCash.do			//加了trust，记得删掉
			Map jsonMap = Json.toMap(json);

	//写死测试数据，此为需要传递进来的参数
			jsonMap.put("openid", "ovO7is8jef5Ui7_tRufbs2SwLZuQ");
			jsonMap.put("money", "100");
	//写死测试数据，此为需要传递进来的参数
			
			
			//通过zxxx01去W_PayType找配置
			Map configMap = payRoute.getWebChatConfig(-1, 1,"order");	
			
			String mch_appid = configMap.get("webChat_app_id").toString();		//appId
			String mchid = configMap.get("webChat_partner").toString();			//微信支付分配的商户号
//			String device_info = "";											//设备号(可为空)
			String nonce_str = DataUtils.getUUID();								//随机字符串（33位以下）
			String partner_trade_no = "TX" + nonce_str.substring(0, 8);			//商户订单号（33位以下）
			String openid = jsonMap.get("openid").toString();					//用户openid
			String check_name = "NO_CHECK";										//NO_CHECK：不校验真实姓名 ；FORCE_CHECK：强校验真实姓名
			String re_user_name = "张三";											//收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
			String amount = jsonMap.get("money").toString();					//企业付款金额，单位为分，最低100
			String desc = "预付款提现";												//企业付款描述信息
			String spbill_create_ip = "192.168.31.114";							//调用接口的机器ip地址
			
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
			
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(configMap.get("webChat_app_id").toString(), configMap.get("webChat_app_secret").toString(), configMap.get("webChat_partner_key").toString());
			String sign = reqHandler.createSign(packageParams);
			
			System.out.println("未包含签名参数：" + packageParams);
			System.out.println("签名字符串：" + sign);
			
			String postXml = "<xml>" +
					"<mch_appid>" + mch_appid + "</mch_appid>" +
					"<mchid>"+ mchid + "</mchid>" +
					"<nonce_str>" + nonce_str + "</nonce_str>" +
					"<partner_trade_no>" + partner_trade_no + "</partner_trade_no>" +
					"<openid>" + openid + "</openid>" +
					"<check_name>"+check_name+"</check_name>" +
					"<re_user_name>" + re_user_name + "</re_user_name>" +
					"<amount>" + amount + "</amount>" +
	 				"<desc>" + desc + "</desc>" +
					"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +
					"<sign>" + sign + "</sign>" +
					"</xml>";
			System.out.println("！！！！！！postXml："+postXml);
			String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
			
			String resultXml= ClientCustomSSL.doRefund(url, postXml ,configMap);		//需要验证证书
			System.out.println("！！！！！微信返回转换前的resultXml：" + resultXml);
			
			Map returnMap = XMLUtil.doXMLParse(resultXml);
			System.out.println("！！！！！微信返回转换后的returnMap：" + returnMap);
			
			String return_code = (String) returnMap.get("return_code");
			String result_code = (String) returnMap.get("result_code");
			
			Map resultMap = new HashMap();
			if ("SUCCESS".equals(return_code)) {
				if ("SUCCESS".equals(result_code)) {
					//成功转账的业务逻辑
					
					
					
				} else {
					String err_code_des = returnMap.get("err_code_des").toString();
					resultMap.put("flag", "0");
					resultMap.put("err_code_des", err_code_des);
				}
			}else{
				String return_msg = (String) returnMap.get("return_msg");
				resultMap.put("flag", "0");
				resultMap.put("err_code_des", return_msg);
			}
			return resultMap;
		}
		
*/
		
		

}
