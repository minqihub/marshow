package com.framework.function;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.service.Authorization;
import com.framework.utils.Json;
import com.thirdParty.Ali.AliAPI;

/**
 * 短信类
 * @author minqi 2017-10-08 16:39:13
 *
 */
@SuppressWarnings("unused")
@Controller
@RequestMapping("/trust/sMS")
public class SMS {
	
	private long timeLimit = 0;
	
	/**
	 * 发送短信的时间间隔限制60秒
	 * @return
	 */
	private boolean checkTimeLimit(){
		System.out.println("当前倒计时剩余：" + this.timeLimit);
		
		long nowTime = System.currentTimeMillis() / 1000;
		System.out.println("当前时间：" + nowTime);
		
		if(nowTime - this.timeLimit < 60){
			return false;
		}
		this.timeLimit = nowTime;
		return true;
	}
	
	/**
	 * 发送短信验证码
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/sendSmsCode.do")
	public Map sendSmsCode(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		Map returnMap = new HashMap();
		try {
			String mobile = map.get("mobile").toString();
			String smsCode = map.get("smsCode").toString();
			if(checkTimeLimit()){
				AliAPI.smsCode(mobile, smsCode);
				returnMap.put("MSGID", "S");
				returnMap.put("MESSAGE", "发送成功");
			}else{
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", "时间间隔未超过60秒");
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "接收人或短信内容为空");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "短信接口调用失败");
		}
		return null;
	}
	
	
	/**
	 * 发送短信通知消息（要求登陆）
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/sendSmsNotice.do")
	public Map sendSmsNotice(String json, HttpServletRequest request, HttpServletResponse response){
		Map userInfo = Authorization.getUserInfo(request);
		
		Map returnMap = new HashMap();
		if(userInfo.isEmpty()){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "未登录");
			return null;
		}
		
		Map map = Json.toMap(json);
		try {
			String mobile = map.get("mobile").toString();
			String message = map.get("message").toString();
			AliAPI.smsCode(mobile, message);
			
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "接收人或短信内容为空");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "短信接口调用失败");
		}
		return null;
	}
	
	
	
	
}
