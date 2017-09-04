package com.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.utils.Json;

public class Fun {
	public static void main(String[] args) {
		
		JSONObject json = new JSONObject();
		json.put("aaa", "aaa111");
		
		
		JSONArray ary = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONObject temp = new JSONObject();
			temp.put("aaa", "aaa111");
			ary.add(temp);
		}
		
		json.put("list", ary);
		
		System.out.println("json对象：" + json);
		
		System.out.println("object转JA：" + Json.toJA(json.get("list")));
		System.out.println("string转JA：" + Json.toJA(json.get("list").toString()));
		
	}
}
