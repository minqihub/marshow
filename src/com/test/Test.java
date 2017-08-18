package com.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/test")
public class Test {
	
	@RequestMapping("/fun1.do")
	public void fun(String json, HttpServletResponse response) throws IOException{
		System.out.println("传入的json是："+json);
		
		JSONObject map = new JSONObject();
		map.put("MSGID", "s");
		map.put("MESSAGE", "恭喜调用成功");
		
		
		PrintWriter pw = null;
		try{
			pw = response.getWriter();
			pw.print(map);
		}finally{
			if(pw != null){
				pw.close();
		    }
		}
	}
	
	@RequestMapping("/fun2.do")
	public Map fun2(String json, HttpServletResponse response) throws IOException{
		System.out.println("传入的json是："+json);
		
		Map map = new HashMap();
		map.put("MSGID", "s");
		map.put("MESSAGE", "恭喜调用成功");
		
		
		
		
		return map;
	}
	
	
	
	
	
}
