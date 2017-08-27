layui.use('tree', function(){
	
	layui.tree({
		elem: '#demo', //传入元素选择器
		nodes: [
		        {name: '父节点1', children: [{name: '子节点11'} , {name: '子节点12'}]},
		  		{name: '父节点2', children: [{name: '子节点21', children: [{name: '子节点211'}]}]}
		],
		click: function(node){
			console.log(node); //node即为当前点击的节点数据
		} 
	});
  
  
  
  
  
});