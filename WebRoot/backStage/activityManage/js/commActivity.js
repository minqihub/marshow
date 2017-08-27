layui.use('laydate', function(){
	var laydate = layui.laydate;
	
	
	
	var dateObj = laydate.render({
		elem: '#startTime',
		type: 'datetime',
//		range: 'true',		//或range： '~'连接符
		format: 'yyyy-MM-dd HH:mm:ss',
//		value: '',		//初始值
//		min: '',
//		max: '',
		trigger: 'click',	//输入框默认focus，非输入框默认click
//		show: true,
//		closeStop: '#startTime',
//		position: '',	//默认absolute
//		zIndex: 9999999,		//如果position设置为static，则无效
//		showBottom: true,		//显示底部按钮
//		btns: ['clear','now','comfirm'],		//此为默认的、控件可识别的
//		lang: 'en',		//中文zn，英文en
//		theme: '',		//默认default，molv，#颜色值，grid
		calendar: true,		//是否显示公历节日
//		mark:{			//自定义重要的日子
//			'0-4-21':'闵奇生日',	//0-表示每一年
//			'2017-8-15':'项目开始构思'
//		},
//		done:function(value, date){debugger
//			if(date.year === 2017 && date.month === 4 && date.date === 21){
//				dateObj.hint("提示：这一天是闵奇的生日");
//			}
//		},
//		ready:function(date){
//			console.info(date);
//		},
//		change:function(value, date, endDate){
//			
//		},
//		done:function(value, date, endDate){		//点击日期、清空、现在、确定会触发
//			
//		},
	});
});

layui.use('layedit', function(){
	var layedit = layui.layedit;
	
	layedit.build('content');
	
});