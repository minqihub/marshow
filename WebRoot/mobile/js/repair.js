var myApp = new Framework7({
    swipePanel: 'right',		//滑动打开侧边栏
    material: true,				//确定主题
    modalTitle: "提示",			//弹窗的相关配置(Alert, Confirm, Prompt)
    modalButtonOk: "确定",
    modalButtonCancel: "取消",
});

var $$ = Dom7;

//提交按钮。获取表单数据
$$('.form-to-json').on('click', function(){
	var formData = myApp.formToJSON('#my-form');
	alert(JSON.stringify(formData));
}); 

//TODO 查询社区结构






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