<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
    	<base href="<%=basePath%>">
    
    	<title>分发页面（暂）</title>
    	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no, minimal-ui">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
	
		<script type="text/javascript" src="plugins/formTools.js"></script>
		<script type="text/javascript" src="plugins/jquery-3.1.1.js"></script>
		<script type="text/javascript">
			
  		</script>
  	</head>
  
  	<body>
   		<p><b>分发页面（暂）</b></p>
   		
   		<a href="homePage/index.html">宣传首页</a> 
		<br/>
		<p>（建议PC端访问）</p>
		<br/>
   		<a href="backStage/index.html">后台管理</a> 
		<br/>
		<p>（建议PC端访问）</p>
		<br/>
   		<a href="mobile/index.html">移动用户界面</a>
		<p>（建议手机访问，或PC浏览器开发模式）</p>
   		
  	</body>
  	
</html>
