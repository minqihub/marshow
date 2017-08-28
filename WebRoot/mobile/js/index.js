//http://framework7.taobao.org/docs/app-layout.html#.WaPtSpFJLp4
var myApp = new Framework7({
    swipePanel: 'right'		//滑动打开侧边栏
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

//下拉刷新
var ptrContent = $$('.pull-to-refresh-content');
ptrContent.on('refresh', function (e) {
    setTimeout(function () {
        myApp.pullToRefreshDone();
    }, 2000);
});




$$('.alert-text-title-callback').on('click', function () {
    myApp.alert('这是弹窗的内容！！！！', '这是弹窗的标题', function () {
        myApp.alert('你点击了ok');
    });
});

$$('.confirm-title-ok-cancel').on('click', function () {
	myApp.confirm('Are you sure?', 'Custom Title', 
	function () {
		myApp.alert('You clicked Ok button');
	},
	function () {
		myApp.alert('You clicked Cancel button');
	});
});
