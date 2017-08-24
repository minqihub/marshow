layui.use('element', function(){
	var element = layui.element;
//	var $ = layui.jquery;
	element.init(); //这样element对动态生成的元素才会重新有效
});


layui.use('upload', function(){
	var $ = layui.jquery;
	var upload = layui.upload;
	  
	//拖拽上传
	upload.render({
		elem: '#test4',
		url: '/upload/',
		accept: 'file' ,			//普通文件
		exts: 'xlx|xlsx' ,		//只允许上传压缩文件
		done: function(res){
			console.log(res);
	    }
	});

	
});