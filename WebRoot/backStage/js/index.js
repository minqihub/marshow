debugger
if(!form.checkLogin()){
	window.location.href = form.getprojectUrl + "/backStage/login.html";
}

layui.use('element', function(){
	var element = layui.element;
//	var $ = layui.jquery;
	debugger
	//加载默认页面
	$("iframe").attr("src", form.getprojectName() + "/backStage/userIndex.html");
	
	element.init(); //这样element对动态生成的元素才会重新有效
});

//根据登陆的账号，找到所属的菜单列表，代办，头像信息数据
var userInfo = JSON.parse(window.localStorage.getItem("userInfo"));
$("#logoImg").attr("src", userInfo.logoImg);
$("#nickName")[0].innerText = userInfo.nickName;


//将数据打入页面

//iframe自适应
$(window).on('resize', function() {
	var $content = $('.admin-nav-card .layui-tab-content');
	$content.height($(this).height() - 147);
	$content.find('iframe').each(function() {
		$(this).height($content.height());
	});
}).resize();

//手机设备的简单适配
var treeMobile = $('.site-tree-mobile');
var shadeMobile = $('.site-mobile-shade');
treeMobile.on('click', function() {
	var $foot = $(".layui-footer");
	$foot.attr("class","layui-hide");
	$('body').addClass('site-mobile');
});
shadeMobile.on('click', function() {
	$('body').removeClass('site-mobile');
});

/**
 * 导航的点击事件，替换iframe的内容
 * 注意：
 * 1.jumpUrl测试环境是/marshow/xx/x.html；而正是环境无需项目名，即/xx/x.html
 * 2.正式环境运行缓存，去掉Math的随机数
 */
$('a').on('click', function() {
	var jumpUrl = $(this).attr("jumpUrl");
	if(!form.isNull(jumpUrl)){
		var name = $(this)[0].innerHTML;					//菜单名称
		var allNames = $(".layui-tab-title")[0].innerText;	//选项卡所有的菜单名
		
		//原有的选中变为非选中状态
		$(".layui-this").attr("class", "");
		$(".layui-tab-item.layui-show").attr("class", "layui-tab-item");
		
		if(allNames.indexOf(name) == -1){	//不是重复点击
			var title = '<li class="layui-this">' + name + '</li>';
			$(".layui-tab-title").append(title);
			var content = '<div class="layui-tab-item layui-show"><iframe src="' + form.getprojectName() + jumpUrl + "?rid="+Math.random() + '"></iframe></div>';
			$(".layui-tab-content").append(content);
			
			//手机端选择后，自动隐藏导航
			$('body').removeClass('site-mobile');
			//动态加载的需init一下
			layui.element.init();
		}else{
			var childTitles = $(".layui-tab-title")[0].children;
			for ( var i = 0; i < childTitles.length; i++) {
				if(childTitles[i].innerText.indexOf(name) != -1){
					childTitles[i].className =  "layui-this";
					break;
				}
			}
			var childContents = $(".layui-tab-content")[0].children;
			for ( var j = 0; j < childContents.length; j++) {
				if(childContents[i].innerHTML.indexOf(jumpUrl) != -1){
					childContents[i].className =  "layui-tab-item layui-show";
					break;
				}
			}
		}
	}else{
//		alert("不需要切换iframe里的内容");
	}
});

//键盘监听
$(document).on('keydown', function() {
	var e = window.event;
	if(e.keyCode === 76 && e.altKey) {
		alert("你按下了alt+l");
	}
});

//退出登录
$('#logOut').on('click', function() {
	alert("logOut点击了");
	//完成后台退出登录操作
	
	
	//返回到登录界面
	window.location.href = "login.html";
	
});



