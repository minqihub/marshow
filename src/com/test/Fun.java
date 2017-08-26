package com.test;

import java.io.UnsupportedEncodingException;

import org.springframework.util.StringUtils;

public class Fun {
	public static void main(String[] args) {
		
		String aaa = "http://mmbiz.qpic.cn/mmbiz_jpg/3lm96vzrXqykpknnOWiccZQLnMkj4iaVaDUqdicic2XhibVSr3j6F6z3lYzia93kZIGibgqEyHq51AhQCWI0HQHuMa2Uw/0";

		int index = aaa.lastIndexOf('/');
		int leanth = aaa.length();
		
		String ccc = "";
		if(aaa.length() - aaa.lastIndexOf('/') == 2){
			ccc = aaa.substring(0, index);
		}
		
		
		System.out.println(aaa);
		System.out.println(index);
		System.out.println(leanth);
		System.out.println(ccc);
		
	}
}
