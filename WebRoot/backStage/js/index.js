layui.use('element', function(){
	var element = layui.element;
	var $ = layui.jquery;
	element.init(); //这样element对动态生成的元素才会重新有效
});

//根据登陆的账号，找到所属的菜单列表，代办，头像信息数据


//将数据打入页面



/**
 * 导航的点击事件，替换iframe的内容
 * 注意：
 * 1.jumpUrl测试环境是/marshow/xx/x.html；而正是环境无需项目名，即/xx/x.html
 * 2.正式环境运行缓存，去掉Math的随机数
 */
$('a').on('click', function() {
	var jumpUrl = $(this).attr("jumpUrl");
	if(!form.isNull(jumpUrl)){
		$("#iframe").attr("src", jumpUrl+"?rid="+Math.random());
	}else{
//		alert("不需要切换iframe里的内容");
	}
});


$('#logOut').on('click', function() {
	alert("logOut点击了");
	//完成后台退出登录操作
	
	
	//返回到登录界面
	window.location.href = "login.html";
	
});



