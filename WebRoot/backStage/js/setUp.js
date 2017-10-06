layui.use('upload', function(){
	var $ = layui.jquery;
	var upload = layui.upload;
   
	//执行实例
	var uploadInst = upload.render({
		elem: '#test1', 						//绑定元素
		url: form.getprojectName() + '/ftpUtils/publicUpload.do', 		//上传接口
		accept: 'file', 						//允许上传的文件类型images（默认图片）、file（所有文件）、video（视频）、audio（音频）
		size: 5000, 							//最大允许上传的文件大小，单位KB，默认不限制
		auto: false,							//选择后自动上传（默认true）
		bindAction: '#upload', 					//指向一个按钮触发上传
//		data: {id: 123, abc: 'xxx'}, 			//额外的参数
		done: function(res, index, upload){debugger
			//上传完毕回调
			layer.closeAll('loading'); //关闭loading
		},
		error: function(index, upload){debugger
			//请求异常回调
			layer.closeAll('loading'); //关闭loading
		},
		before:function(obj){debugger
			layer.load(); 						//上传loading
//			obj.preview(function(index, file, result){
//				$('#imgView').append('<img src="'+ result +'" alt="'+ file.name +'" class="layui-upload-img">')
//			});
		},
		choose: function(obj){debugger
			//将每次选择的文件追加到文件队列
			var files = obj.pushFile();
		    
		    //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
		    obj.preview(function(index, file, result){

				$('#imgView').append('<img src="'+ result +'" alt="'+ file.name +'" class="layui-upload-img">')
		    	
		    	console.log(index); 	//得到文件索引
		     	console.log(file); 		//得到文件对象
//		    	console.log(result); 	//得到文件base64编码，比如图片
		      
		    	//这里还可以做一些 append 文件列表 DOM 的操作
		      
		    	//obj.upload(index, file); //对上传失败的单个文件重新上传，一般在某个事件中使用
		    	//delete files[index]; //删除列表中对应的文件，一般在某个事件中使用
			});
		}
	});
	
});