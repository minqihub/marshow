$(".layui-form").attr("action", form.getprojectName() + "/backStage/index.html");

//设置登陆次数，超过一定次数，则要求输入验证码
var times = 0;

//表单控件（开关字段：关闭状态无数据；开启状态则该字段为"on"）
layui.use('form', function(){
	var formUI = layui.form;
	
	//监听提交
	formUI.on('submit(login)', function(data){debugger
		var json = {"json": JSON.stringify(data.field)};
		var url = form.getprojectUrl + "/trust/login/login.do";
		var resultData = form.ajax(json, url).data;
		debugger
		if(resultData.MSGID == "S"){
			//提交成功后，清空表单
			return true;		//自动清空表单
		}else{
			times += 1;
			return false;		//阻止表单提交，页面数据不会丢失
		}
	});
});


//enter键监听（layui表单空间已经封装了回车提交事件）
$(document).on('keydown', function() {
	var e = window.event;
	if(e.keyCode === 13) {
		alert("你按下了回车键");
	}
});

$('#qqLogin').on('click', function() {
	alert("QQ登录，要等正式项目完成，有了正式域名之后才能接入");
	console.info(times);
});

$('#wechatLogin').on('click', function() {
	alert("微信登录，要等正式项目完成，有了正式域名之后才能接入");
	console.info(times);
});

$('#weiboLogin').on('click', function() {
	alert("微博登录，要等正式项目完成，有了正式域名之后才能接入");
	console.info(times);
});