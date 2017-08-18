<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<script type="text/javascript" src="lib/formTools.js"></script>
	<script type="text/javascript" src="lib/jquery-3.1.1.js"></script>
	<script type="text/javascript">
	  	var fun1 = function(){debugger
	  		var json = {"key1" : "哈哈哈","key2" : "吼吼吼"};
	  		var url = "http://localhost:8080/marshow" + "/test/fun1.do";
			form.ajaxCallBack(json, url,function(data){
				alert(data);
			});
	  	};
	  	
	  	var fun2 = function(){debugger
	  		var json = {"key1" : "哈哈哈","key2" : "吼吼吼"};
	  		var url = "http://localhost:8080/marshow" + "/test/fun1.do";
			var aaa = form.ajax(json, url);
			alert(aaa);
		};
  </script>
  </head>
  
  <body>
    This is my JSP page. <br>
    
    <a href="#" onclick="fun1();">异步调用测试</a><br/>
    <a href="#" onclick="fun2();">同步调用测试</a><br/>
    <a href="pages/regist.html">跳转注册页面</a><br/>
    <a href="supermall/index.html">跳转京东页面</a><br/>
    <a href="admin/login.html">跳转后台管理页面</a><br/>
    <a href="test.html">测试pubConfig.js</a><br/>
    <a href="thirdParty/baidu/chooseMap.html">百度地图测试页面</a><br/>
    <a href="test/tLogin.html">测试登陆功能</a><br/>
  </body>
</html>
