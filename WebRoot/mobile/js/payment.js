var myApp = new Framework7({
    swipePanel: 'right',		//滑动打开侧边栏
    material: true,				//确定主题
    modalTitle: "提示",			//弹窗的相关配置(Alert, Confirm, Prompt)
    modalButtonOk: "确定",
    modalButtonCancel: "取消",
});

var $$ = Dom7;




//起始时间
var startTime = myApp.picker({
    input: '#picker-start',
    toolbarCloseText: '完成',
    rotateEffect: true,
    formatValue: function (picker, values) {
        return values[0] + "-" + values[1];
    },
    cols: [
           {
               textAlign: 'left',
               values: ['2013','2014','2015','2016','2017'],
           },
           {
        	   values: ['01','02','03','04','05','06','07','08','09','10','11','12'],
           },
    ]
});

//结束时间
var endTime = myApp.picker({
    input: '#picker-end',
    toolbarCloseText: '完成',
    rotateEffect: true,
    formatValue: function (picker, values) {
        return values[0] + "-" + values[1];
    },
    cols: [
           {
               textAlign: 'left',
               values: ['2013','2014','2015','2016','2017'],
           },
           {
        	   values: ['01','02','03','04','05','06','07','08','09','10','11','12'],
           },
    ],
    onClose: function (p){
    	console.info(123);
    }
});


//提交按钮。获取表单数据
$$('.form-to-json').on('click', function(){
	var formData = myApp.formToJSON('#my-form');
	alert(JSON.stringify(formData));
}); 