//url: '/marshow/test/getData.do'
layui.use('table', function(){
	var table = layui.table;

	  //方法级渲染
	  var ins1 = table.render({
	    elem: '#LAY_table_user'
	    ,url: '/marshow/test/getData.do'
	    	//where: {token: 'sasasas', id: 123}
	    	//method: 'post'
	    ,cols: [[
	      {checkbox: true, fixed: true}
	      ,{field:'id', title: 'ID', width:80, sort: true, fixed: true}
	      ,{field:'username', title: '用户名', width:80}
	      ,{field:'sex', title: '性别', width:80, sort: true}
	      ,{field:'city', title: '城市', width:80}
	      ,{field:'sign', title: '签名', width:177}
	      ,{field:'experience', title: '积分', sort: true, width:80}
	      ,{field:'score', title: '评分', sort: true, width:80}
	      ,{field:'classify', title: '职业', width:80}
	      ,{field:'wealth', title: '财富', sort: true, width:135}
	    ]]
	    ,page: true		//默认false
	    ,height: 315
	    ,skin: 'line' //行边框风格
	  ,even: true //开启隔行背景
	  ,size: 'sm' //小尺寸的表格
	    ,done: function(res, curr, count){
	        //如果是异步请求数据方式，res即为你接口返回的信息。
	        //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
	        console.log(res);
	        
	        //得到当前页码
	        console.log(curr); 
	        
	        //得到数据总量
	        console.log(count);
	      }
	    
	    
	  });
	
	  
	  var $ = layui.$, active = {
	    getCheckData: function(){
	      var checkStatus = table.checkStatus('test1')
	      ,data = checkStatus.data;
	      layer.alert(JSON.stringify(data));
	    }
	    ,getCheckLength: function(){
	      var checkStatus = table.checkStatus('test1')
	      ,data = checkStatus.data;
	      layer.msg('选中了：'+ data.length + ' 个');
	    }
	    ,isAll: function(){
	      var checkStatus = table.checkStatus('test1');
	      layer.msg(checkStatus.isAll ? '全选': '未全选')
	    }
	    ,reload: function(){
	      var demoReload = $('#demoReload');
	      
	      ins1.reload({
	        where: {
	          key: {
	            id: demoReload.val()
	          }
	        }
	      });
	    }
	    ,parseTable: function(){
	      table.init('parse-table-demo');
	    }
	  };
	  
	  $('.demoTable .layui-btn').on('click', function(){
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
	
});
  
  
  