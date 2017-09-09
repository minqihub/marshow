var myApp = new Framework7({
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

var mySearchbar = myApp.searchbar('.searchbar', {
    searchList: '.list-block-search',
    searchIn: '.item-title',
    overlay: '.searchbar-overlay',
    found: '.searchbar-found',
});  


//下拉刷新
var ptrContent = $$('.pull-to-refresh-content');
ptrContent.on('refresh', function (e) {
    setTimeout(function () {
        myApp.pullToRefreshDone();
    }, 2000);
});