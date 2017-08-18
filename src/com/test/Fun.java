package com.test;

import java.io.UnsupportedEncodingException;

public class Fun {
	public static void main(String[] args) {
		
		String aaa = new String("GBK");
		aaa="测试中文";
		
		
		String bbb = null;
		try {
			bbb = new String(aaa.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(aaa);
		System.out.println(bbb);
		
	}
}
