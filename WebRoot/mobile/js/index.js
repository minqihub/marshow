//http://framework7.taobao.org/docs/app-layout.html#.WaPtSpFJLp4
var myApp = new Framework7({
    swipePanel: 'right',		//滑动打开侧边栏
    material: true,				//确定主题
    modalTitle: "提示",			//弹窗的相关配置(Alert, Confirm, Prompt)
    modalButtonOk: "确定",
    modalButtonCancel: "取消",
});

var $$ = Dom7;
   
// Add view
var mainView = myApp.addView('.view-main', {
	// Because we want to use dynamic navbar, we need to enable it for this view:
//	dynamicNavbar: true
});

//轮播图
var mySwiper = myApp.swiper('.swiper-container', {
    speed: 1000,
    spaceBetween: 50,
    pagination: '.swiper-pagination',
    paginationClickable: true,
    autoplay: 3000,
    loop: true
});

//轮播图点击事件
$$('.swiper-slide').on('click', function () {
	window.location.href = "http://www.baidu.com";
});


//下拉刷新
var ptrContent = $$('.pull-to-refresh-content');
ptrContent.on('refresh', function (e) {
    setTimeout(function () {
        myApp.pullToRefreshDone();
    }, 2000);
});




$$('.alert-text-title-callback').on('click', function () {
    myApp.alert('这是弹窗的内容！！！！', function () {
        myApp.alert('你点击了ok');
    });
});

$$('.confirm-title-ok-cancel').on('click', function () {
	myApp.confirm('您确定这么做吗？', 
	function () {
		myApp.alert('You clicked Ok button');
	},
	function () {
		myApp.alert('You clicked Cancel button');
	});
});

//定位
$$('.left.sliding').on('click', function () {
//	myApp.alert('待开发，定位功能需要调用微信JS-SDK...');
	var data = {};
	data.appid = "wx46e8fbea0168eb02";
	weChatJs.getLocation(data, function(res){debugger

		window.sessionStorage.setItem("locationRes", "")
		
		
		$$('#location')[0].innerText = "经" + res.longitude + "纬" + res.latitude;
//		$$('#locationLoading').attr("style","display:none");
//		myApp.alert("获取的地理位置数据："+ JSON.stringify(res));
	});
});

//扫码
$$('.right.sliding').on('click', function () {
//	myApp.alert('待开发，扫码功能需要调用微信JS-SDK...');
	var data = {};
	data.appid = "wx46e8fbea0168eb02";
	weChatJs.scanQRCode(data,function(res){debugger
		var qrCode = res.resultStr;
		myApp.alert("二维码内容：" + qrCode);
		
	});
});

//图标导航
$$('.col-25').on('click', function () {
	var jumpUrl = $$(this).attr("jumpUrl");
	window.location.href = form.getprojectName() + jumpUrl;
});

//底部工具栏切换效果
$$('.tab-link').on('click', function () {debugger
	var aaa = $$(this);
	
	aaa[0].children[1].innerHTML += "_fill"; 
	
//	var others = aaa.siblings();
//	for ( var i = 0; i < others.length; i++) {
//		if(others[i].className.indexOf("tab-link-highlight") != -1){
//			others[i].children[0].innerHTML = others[i].children[0].innerHTML.replace("_fill", "");
//		}
//	}
	
});

//解决跨域链接跳转问题
$$('.jump').on('click', function () {
	var jumpUrl = $$(this).attr("jumpUrl");
	window.location.href = jumpUrl;
});