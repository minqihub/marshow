layui.use('element', function(){
	var element = layui.element;
//	var $ = layui.jquery;
	element.init(); //这样element对动态生成的元素才会重新有效
});

//根据登陆的账号，找到所属的菜单列表，代办，头像信息数据


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
//		$("#iframe").attr("src", jumpUrl+"?rid="+Math.random());
		
		//原有的选中变为非选中状态
		$(".layui-this").attr("class", "");
		$(".layui-tab-item.layui-show").attr("class", "layui-tab-item");
		
		var name = $(this)[0].innerHTML;
		var title = '<li class="layui-this">' + name + '</li>';
		$(".layui-tab-title").append(title);

		var content = '<div class="layui-tab-item layui-show"><iframe src="' + "/marshow" + jumpUrl + '"></iframe></div>';
		$(".layui-tab-content").append(content);
		
		//手机端选择后，自动隐藏导航
		$('body').removeClass('site-mobile');
		
		//动态加载的需init一下
		layui.element.init();
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



