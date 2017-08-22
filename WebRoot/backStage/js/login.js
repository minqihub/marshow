layui.use(['layer', 'form'], function() {debugger
	var layer = layui.layer,
//	$ = layui.jquery,
	form = layui.form;
//	
//	form.on('submit(login)',function(data){
//	
//	location.href='index.html';
//		return false;
//	});
});


//enter键监听（layui表单空间已经封装了回车提交事件）
$(document).on('keydown', function() {
	var e = window.event;
	if(e.keyCode === 13) {
		alert("你按下了回车键");
	}
});



//设置登陆次数，超过一定次数，则要求输入验证码
var loginTimes = 0;





$('#qqLogin').on('click', function() {
	alert("QQ登录，要等正式项目完成，有了正式域名之后才能接入");
	times += 1;
	console.info(times);
});

$('#wechatLogin').on('click', function() {
	alert("微信登录，要等正式项目完成，有了正式域名之后才能接入");
	times += 1;
	console.info(times);
});

$('#weiboLogin').on('click', function() {
	alert("微博登录，要等正式项目完成，有了正式域名之后才能接入");
	times += 1;
	console.info(times);
});