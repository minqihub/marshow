//设置登陆次数，超过一定次数，则要求输入验证码
var times = 0;
var layer;
//表单控件（开关字段：关闭状态无数据；开启状态则该字段为"on"）
layui.use(['layer', 'form'], function(){
    layer = layui.layer;
	var formUI = layui.form;
	
	//监听提交
	formUI.on('submit(login)', function(data){debugger
		var json = {"json": JSON.stringify(data.field)};
		var url = form.getprojectUrl + "/trust/login/login.do";
		var resultData = form.ajax(json, url).data;
		debugger
		if(resultData.MSGID == "S"){
			//提交成功后，清空表单
			window.location.href = "index.html";
		}else{
			layer.msg("用户名或密码错误！", {icon: 2});
			//刷新验证码
//			$('#changeCode').click();
			times += 1;
		}
		//阻止表单提交，页面数据不会丢失
		return false;
	});
});

//enter键监听（layui表单空间已经封装了回车提交事件）
$(document).on('keydown', function() {
	var e = window.event;
	if(e.keyCode === 13) {
		alert("你按下了回车键");
	}
});

$('#changeCode').on('click', function() {
    $('#changeCode > img')[0].src = '../images/GetVerfiyCode.png';
});

$('#qqLogin').on('click', function() {
	layer.msg("QQ登录，待接入");
	console.info(times);
});

$('#wechatLogin').on('click', function() {
	layer.msg("微信登录，待接入");
	console.info(times);
});

$('#weiboLogin').on('click', function() {
	layer.msg("微博登录，待接入");
	console.info(times);
});
