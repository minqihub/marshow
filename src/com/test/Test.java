package com.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.wxInterface.WXMenu;
import com.thirdParty.weChat.wxInterface.WXService;

@Controller
@RequestMapping("/trust/test")
public class Test {
	
	private String deployIP = "";
	private long deployTime = 0;
	
	@SuppressWarnings("unused")
	@RequestMapping("/getData.do")
	public void getData(String json, HttpServletRequest request, HttpServletResponse response){
		
		
		Map<String, String[]> paramMap = request.getParameterMap();
		System.out.println("传来的参数map："+ paramMap);
		
		String aaap[] = paramMap.get("id");		
		System.out.println("取特定的key"+paramMap.get("id"));
		
		String aaa = "{\"code\":0,\"msg\":\"\",\"count\":1000,\"data\":[{\"id\":10000,\"username\":\"user-0\",\"sex\":\"女\",\"city\":\"城市-0\",\"sign\":\"签名-0\",\"experience\":255,\"logins\":24,\"wealth\":82830700,\"classify\":\"作家\",\"score\":57},{\"id\":10001,\"username\":\"user-1\",\"sex\":\"男\",\"city\":\"城市-1\",\"sign\":\"签名-1\",\"experience\":884,\"logins\":58,\"wealth\":64928690,\"classify\":\"词人\",\"score\":27},{\"id\":10002,\"username\":\"user-2\",\"sex\":\"女\",\"city\":\"城市-2\",\"sign\":\"签名-2\",\"experience\":650,\"logins\":77,\"wealth\":6298078,\"classify\":\"酱油\",\"score\":31},{\"id\":10003,\"username\":\"user-3\",\"sex\":\"女\",\"city\":\"城市-3\",\"sign\":\"签名-3\",\"experience\":362,\"logins\":157,\"wealth\":37117017,\"classify\":\"诗人\",\"score\":68},{\"id\":10004,\"username\":\"user-4\",\"sex\":\"男\",\"city\":\"城市-4\",\"sign\":\"签名-4\",\"experience\":807,\"logins\":51,\"wealth\":76263262,\"classify\":\"作家\",\"score\":6},{\"id\":10005,\"username\":\"user-5\",\"sex\":\"女\",\"city\":\"城市-5\",\"sign\":\"签名-5\",\"experience\":173,\"logins\":68,\"wealth\":60344147,\"classify\":\"作家\",\"score\":87},{\"id\":10006,\"username\":\"user-6\",\"sex\":\"女\",\"city\":\"城市-6\",\"sign\":\"签名-6\",\"experience\":982,\"logins\":37,\"wealth\":57768166,\"classify\":\"作家\",\"score\":34},{\"id\":10007,\"username\":\"user-7\",\"sex\":\"男\",\"city\":\"城市-7\",\"sign\":\"签名-7\",\"experience\":727,\"logins\":150,\"wealth\":82030578,\"classify\":\"作家\",\"score\":28},{\"id\":10008,\"username\":\"user-8\",\"sex\":\"男\",\"city\":\"城市-8\",\"sign\":\"签名-8\",\"experience\":951,\"logins\":133,\"wealth\":16503371,\"classify\":\"词人\",\"score\":14},{\"id\":10009,\"username\":\"user-9\",\"sex\":\"女\",\"city\":\"城市-9\",\"sign\":\"签名-9\",\"experience\":484,\"logins\":25,\"wealth\":86801934,\"classify\":\"词人\",\"score\":75}]}";
		
		JSONObject dataJson = Json.toJO(aaa);
		
		response.setCharacterEncoding("utf-8");
		HttpUtils.printString(response, dataJson);
		System.out.println(dataJson);
	}
	
	/**
	 * 防止1秒内多次调用
	 * @param request
	 * @return
	 */
	public boolean defend(HttpServletRequest request){
		String nowIP = request.getRemoteAddr();					//TODO 获取真实IP的方法
		long nowTime = System.currentTimeMillis() / 1000;
		System.out.println("本次IP和时间_"+nowIP+"_"+nowTime);
		System.out.println("上次IP和时间_"+this.deployIP+"_"+this.deployTime);
		
		//首次调用：放行+记录ip和时间
		if(this.deployIP.equals("") || this.deployTime == 0){
			this.deployIP = nowIP;
			this.deployTime = nowTime;
			return true;
		//和上次ip不一样且调用间隔时间大于1秒：放行+更新时间
		}else if(this.deployIP.equals(nowIP) && nowTime - this.deployTime > 1 ){
			this.deployTime = nowTime;
			return true;
		//其他均不放行
		}else{
			this.deployTime = nowTime;
			return false;
		}
	}
	
	
	/**
	 * http://localhost:8080/marshow/trust/test/fun1.do
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/fun1.do")
	public void fun(String json, HttpServletRequest request, HttpServletResponse response){
		
		if(defend(request)){
			System.out.println("调用成功");
		}else{
			System.out.println("被拦截了被拦截了被拦截了被拦截了被拦截了被拦截了");
		}
	}
	
	@RequestMapping("/fun2.do")
	public void fun2(String json, HttpServletResponse response) throws IOException{
		//http://localhost:8080/marshow/test/fun2.do
		
		String appid = "wxa64a805a9fd3d634";
//		JSONObject sendJson = new JSONObject();
//		sendJson.put("kf_account", "preName@gh_a1ebce799aff");
//		sendJson.put("nickname", "小闵");
//		WXService.addService(appid, sendJson.toString());
		
		
		
		
	}
	
	
	
	
	
}
