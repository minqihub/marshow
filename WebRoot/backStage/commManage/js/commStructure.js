//取数据库结构数据
//var nodes = [
//    {name: '父节点1',code:'01', children: [{name: '子节点11',code:'0101'} , {name: '子节点12',code:'0102'}]},
//		{name: '父节点2',code:'02', children: [{name: '子节点21',code:'0201', children: [{name: '子节点211',code:'020101'}]}]}
//];

var json = {"json":JSON.stringify({"commId":"comm0001"})};
var url = form.getprojectUrl + "/community/getStructure.do";
var nodes = form.ajax(json, url).data;





var option;			//全局操作名称
var choosedNode;	//全局选中的节点

layui.use('tree', function(){
	
	layui.tree({
		elem: '#demo', //传入元素选择器
		nodes: nodes,
		click: function(node){debugger
			choosedNode = node;
			if($("#nodeName")[0].disabled == false){
				$("#nodeName")[0].disabled = true;
			}
			if($("#nodeCode")[0].disabled == false){
				$("#nodeCode")[0].disabled = true;
			}

			$("#nodeName").val(node.name);
			$("#nodeCode").val(node.code);
		} 
	});
  
});

//编辑
$('#edit').on('click', function() {
	if($("#nodeName").val() != ""){
		option = "edit";
		$("#nodeName").removeAttr("disabled");
//		$("#nodeCode").removeAttr("disabled");
		$("#toolBar").fadeIn(600);
	}
});

//增加同级节点
$('#add').on('click', function() {
	if($("#nodeName").val() != ""){
		option = "add";
		$("#new").fadeIn(600);
		$("#toolBar").fadeIn(600);
	}
});

//增加子节点
$('#addSub').on('click', function() {
	if($("#nodeName").val() != ""){
		option = "addSub";
		$("#new").fadeIn(600);
		$("#toolBar").fadeIn(600);
	}
});

//删除节点（TODO 待confirm）
$('#del').on('click', function() {
	if($("#nodeName").val() != ""){
		option = "del";
		
		alert("暂不允许删除，待添加判断");
//		var url = form.getprojectUrl + "/community/updateStructure.do";
//		var json = {"json":JSON.stringify({"option":option, "choosedNode":choosedNode})};
//		result = form.ajax(json, url).data;
//		
//		if(result.MSGID == "S"){
//			location.reload();
//		}
	}
});

//提交
$('#submit').on('click', function() {
	var code = $("#nodeCode").val();
	var name = $("#nodeName").val();
	var url = form.getprojectUrl + "/community/updateStructure.do";
	
	//编辑、删除当前节点
	if(option == "edit" || option == "del"){
		var json = {"json":JSON.stringify({"option":option, "choosedNode":choosedNode, "name":name})};
		result = form.ajax(json, url).data;

	//新增同级、子级节点
	}else if(option == "add" || option == "addSub"){
		var newNodeCode = $("#newNodeCode").val();
		var newNodeName = $("#newNodeName").val();
		var json = {"json":JSON.stringify({"option":option, "choosedNode":choosedNode, "newNodeCode":newNodeCode, "newNodeName":newNodeName})};
		result = form.ajax(json, url).data;
	}
	if(result.MSGID == "S"){
		location.reload();
	}
});

//取消
$('#cancel').on('click', function() {
	if(option == "edit"){
		if($("#nodeName")[0].disabled == false){
			$("#nodeName")[0].disabled = true;
		}
		if($("#nodeCode")[0].disabled == false){
			$("#nodeCode")[0].disabled = true;
		}
		$("#toolBar").fadeOut(600);
	}else if(option == "add"){
		$("#new").fadeOut(600);
		$("#toolBar").fadeOut(600);
	}else if(option == "addSub"){
		$("#new").fadeOut(600);
		$("#toolBar").fadeOut(600);
	}else{
		alert("del");
	}
});
