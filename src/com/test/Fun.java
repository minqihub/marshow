package com.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.utils.DataUtils;
import com.framework.utils.Json;

public class Fun {
	public static void main(String[] args) {
		
		String nowTime = DataUtils.getSysTime();
		System.out.println(nowTime);
		
		
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		
		cal.add(Calendar.HOUR_OF_DAY, -2);
		Date justNow = cal.getTime();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println(format.format(now));
		System.out.println(format.format(justNow));
		
		
	}
}
