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
	 * http://localhost:8080/marshow/trust/test/fun1.do
	 * @param json
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@ResponseBody
	@RequestMapping("/fun1.do")
	public @ResponseBody Map fun(String json, HttpServletResponse response) throws IOException{
		
		
		Map map = new HashMap();
		
		map.put("MSGID", "s");
		map.put("MESSAGE", "恭喜调用成功");

		return map;
		
//		PrintWriter pw = null;
//		try{
//			pw = response.getWriter();
//			pw.print(map);
//		}finally{
//			if(pw != null){
//				pw.close();
//		    }
//		}
	}
	
	@RequestMapping("/fun2.do")
	public void fun2(String json, HttpServletResponse response) throws IOException{
		//http://localhost:8080/marshow/test/fun2.do
		
		String appid = "wxa64a805a9fd3d634";
//		JSONObject sendJson = new JSONObject();
//		sendJson.put("kf_account", "preName@gh_a1ebce799aff");
//		sendJson.put("nickname", "小闵");
//		WXService.addService(appid, sendJson.toString());
		
		
		
		WXMenu.creatMenu(appid, null);
		
	}
	
	
	
	
	
}
