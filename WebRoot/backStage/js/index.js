debugger
if(!form.checkLogin()){
	window.location.href = form.getprojectUrl + "/backStage/login.html";
}


//根据登陆的账号，找到所属的菜单列表，代办，头像信息数据
var userInfo = JSON.parse(window.localStorage.getItem("userInfo"));
$("#logoImg").attr("src", userInfo.logoImg);
$("#nickName")[0].innerText = userInfo.nickName;


//将数据打入页面





var message;
layui.config({
    base: '../plugins/build/js/',
    version: '1.0.1'
}).use(['app', 'message'], function() {
    var app = layui.app;
    var $ = layui.jquery;
    var layer = layui.layer;
        
    //将message设置为全局以便子页面调用
    message = layui.message;
    
    //主入口
    app.set({type: 'iframe'}).init();
    
    $('dl.skin > dd').on('click', function() {
        var $that = $(this);
        var skin = $that.children('a').data('skin');
        switchSkin(skin);
    });
    
    var setSkin = function(value) {
  		layui.data('kit_skin', {
       		key: 'skin',
     		value: value
       	});
  	};
   	var getSkinName = function() {
     	return layui.data('kit_skin').skin;
 	};
   	var switchSkin = function(value) {
      	var _target = $('link[kit-skin]')[0];
      	_target.href = _target.href.substring(0, _target.href.lastIndexOf('/') + 1) 
      					+ value + _target.href.substring(_target.href.lastIndexOf('.'));
      	setSkin(value);
 	};
   	var initSkin = function() {
    	var skin = getSkinName();
     	switchSkin(skin === undefined ? 'default' : skin);
 	}();
});



/*

layui.use('element', function(){
	var element = layui.element;
//	var $ = layui.jquery;
	debugger
	//加载默认页面
	$("iframe").attr("src", form.getprojectName() + "/backStage/userIndex.html");
	
	element.init(); //这样element对动态生成的元素才会重新有效
});*/


