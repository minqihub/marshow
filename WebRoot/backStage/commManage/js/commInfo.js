debugger

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