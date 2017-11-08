var myApp = new Framework7({
    swipePanel: 'right',		//滑动打开侧边栏
    material: true,				//确定主题
    modalTitle: "提示",			//弹窗的相关配置(Alert, Confirm, Prompt)
    modalButtonOk: "确定",
    modalButtonCancel: "取消",
});

//初始化试图（smart-select必须）
var mainView = myApp.addView('.view-main', {
	// Because we want to use dynamic navbar, we need to enable it for this view:
//	dynamicNavbar: true
});

var $$ = Dom7;


//TODO 查询社区结构



//日期
var myCalendar = myApp.calendar({
    input: '#calendar-input',
    closeOnSelect: true,
    toolbarCloseText: '确定',
    monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月' , '九月' , '十月', '十一月', '十二月'],
    dayNamesShort: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
    firstDay: 7,
    onOpen: function (p){
    	$$(".picker-calendar-selected-date")[0].innerHTML = "请选择日期";
    }
});





//地址选择器
var pickerDependent = myApp.picker({
    input: '#picker-dependent',
    toolbarCloseText: '完成',
    rotateEffect: true,
    formatValue: function (picker, values) {
        return values[0] + "-" + values[1] + "-" + values[2] + "-" + values[3];
    },
    cols: [
           {
               textAlign: 'left',
               values: ['东区','南区','西区','北区'],
           },
           {
        	   values: ['1栋','2栋','3栋','4栋','5栋','6栋','7栋','8栋','9栋','10栋'],
           },
           {
        	   values: ['1单元','2单元','3单元','4单元','5单元','6单元','7单元','8单元','9单元','10单元'],
           },
           {
        	   //values为数组即可
               values: ('101 102 201 202 301 302 401 402').split(' ')
           },
    ]
});
//pickerDependent.open(); // open Picker


//提交按钮。获取表单数据
$$('.form-to-json').on('click', function(){
	var formData = myApp.formToJSON('#my-form');
	alert(JSON.stringify(formData));
}); 