//数据表格控件
layui.use('table', function(){
	var table = layui.table;
	var $ = layui.jquery;
	
	var reloadObj = table.render({
		elem: '#demo',
		url: form.getprojectName() + '/commNotice/getNotice.do',
		where: {"json":JSON.stringify({"commId": "comm0001"})},
		method: 'post',
		cols: [[
		        {field:'jlbh', title: 'jlbh', width:80, sort: true, fixed: true},
		        {field:'noticeType', title: '类型', width:80},
		        {field:'title', title: '标题', width:200},
		        {field:'content', title: '内容', width:200, sort: true},
		        {field:'userFound', title: '发布人', width:100},
		        {field:'timeFound', title: '发布时间', width:150},
		        {field:'startTime', title: '开始时间', sort: true, width:150},
		        {field:'endTime', title: '结束时间', sort: true, width:150},
		        {field:'status', title: '状态', width:80},
		        {field:'shareMark', title: '可见状态', width:80},
		        {field:'opinion', title: '审核意见', sort: true, width:150},
		        {fixed: 'right', width:150, align:'center', toolbar: '#barDemo'},
		]],
    	page: true,
//    	height: 615,
    	even: true ,	//开启隔行背景
    	size: 'sm' ,	//小尺寸的表格sm\lg
    	skin: 'line' ,	//行边框风格line\row\nob
    	done: function(res, curr, count){
		    //如果是异步请求数据方式，res即为你接口返回的信息。
//		    console.log(res);
		    
		    //得到当前页码
//		    console.log(curr); 
		    
		    //得到数据总量
//		    console.log(count);
		}
	});

	//监听工具条
	table.on('tool(test)', function(obj){debugger 		//注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
		var data = obj.data; 		//获得当前行数据
	  	var layEvent = obj.event; 	//获得 lay-event 对应的值
	  	var tr = obj.tr; 			//获得当前行 tr 的DOM对象
	 
	  	if(layEvent === 'detail'){debugger 			//查看
	  		//do somehing
	  		
	  	} else if(layEvent === 'del'){ 				//删除
	  		layer.confirm('真的删除行么', function(index){
	  			obj.del(); //删除对应行（tr）的DOM结构
	  			layer.close(index);
	  			//向服务端发送删除指令
	  		});
	  	} else if(layEvent === 'edit'){debugger 	//编辑
	  		//do something
	  		
	  	}
	});
	
	$('#searchBtn').on('click', function() {debugger
		reloadObj.reload({
			where: { //设定异步数据接口的参数
				id: $('#demoReload').val()
			}
		});
		
	});
	
	
	//表格重载，只有url接口取数据才行
//	reloadObj.reload({
//		where: { //设定异步数据接口的参数
//			key: 'xxx',
//			value: 'yyy'
//		}
//	});

});

//日期控件
layui.use('laydate', function(){
	var laydate = layui.laydate;
	
	var dateObj = laydate.render({
		elem: '#rangeTime',
		type: 'datetime',
		range: '~',		//或range： '~'连接符
		format: 'yyyy-MM-dd HH:mm:ss',
		trigger: 'click',	//输入框默认focus，非输入框默认click
		calendar: true,		//是否显示公历节日
		done:function(value, date){
			if(date.year === 2017 && date.month === 4 && date.date === 21){
				dateObj.hint("提示：这一天是闵奇的生日");
			}
		},
		ready:function(date){
			console.info(date);
		},
		change:function(value, date, endDate){
			
		},
		done:function(value, date, endDate){		//点击日期、清空、现在、确定会触发
			
		},
	});
});

//表单控件（开关字段：关闭状态无数据；开启状态则该字段为"on"）
layui.use('form', function(){
	var formUI = layui.form;
	
	//监听提交
	formUI.on('submit(formDemo)', function(data){
		if(data.field.shareMark == null){
			data.field.shareMark = "0";
		}else{
			data.field.shareMark = "1";
		}
		
		var rangeTime = (data.field.rangeTime).split("~");
		data.field.startTime = $.trim(rangeTime[0]);
		data.field.endTime = $.trim(rangeTime[1]);
		
		var json = {"json": JSON.stringify(data.field)};
		var url = form.getprojectUrl + "/commNotice/addNotice.do";
		var resultData = form.ajax(json, url).data;
		debugger
		if(resultData.MSGID == "S"){
			//提交成功后，清空表单
			return true;		//自动清空表单
		}else{
			return false;		//阻止表单提交，页面数据不会丢失
		}
//		layer.msg(resultData.MESSAGE);			//TODO 表单刷新过快，导致看不到layer.msg
	});
});