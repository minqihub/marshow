<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.HttpURLConnection,java.net.URL,java.util.*"%>
<%@ page import="java.io.BufferedReader,java.io.InputStreamReader"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.framework.utils.XmlUtils"%>
<%@ page import="com.framework.pay.PayUtils"%>
<%
	
	//创建支付应答对象
	Map parameters = XmlUtils.xmlToMap(request);
	System.out.println("微信回调全部参数：" + parameters);
	
	Map<String, String> newparameters = new HashMap<String, String>(); 
	String ValidSign = "";
	Set<String> set = parameters.keySet();
	Iterator<String> it = set.iterator();
	while (it.hasNext()) {
		String k = (String) it.next();
		String name = (String) parameters.get(k);
		String v = name;
		if (!"sign".equals(k) && null != v && !"".equals(v)) {
			newparameters.put(k, v);
		}
		if ("sign".equals(k)) {
			ValidSign = v;
		}
	}

	//通过ORDER_ID查找卖家信息，找到微信支付宝配置，找到对应微信商户平台密钥
	String key = "";
	
	String newsign = PayUtils.getSignature(newparameters, key, null);
	
	//验证签名
	if (ValidSign.equals(newsign) ) {
		System.out.println("验签成功");
		
			//判断支付结果
			if(parameters.get("return_code").toString().equals("SUCCESS")){		//业务结果result_code
				System.out.println("微信支付成功");
				//成功支付，在此处理商户业务逻辑
					
				if(true){
					out.println("success"); 					
				}else{
					out.println("fail");
				}
			}else{
				out.println("fail");
			}

	} else {
		 out.println("fail");
	}
%>