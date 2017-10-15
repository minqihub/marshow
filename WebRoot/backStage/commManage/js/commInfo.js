debugger

var map = new BMap.Map("container");          // 创建地图实例  
var point = new BMap.Point(116.404, 39.915);  // 创建点坐标  
map.enableScrollWheelZoom();						//开启鼠标滚轮缩放功能
map.enableContinuousZoom(); 							//开启地图惯性拖动
map.centerAndZoom(point, 15);                 // 初始化地图，设置中心点坐标和地图级别（视图会以该点居中）

//添加控件
	map.setCurrentCity("北京"); 						// 仅当设置城市信息时，MapTypeControl的切换功能才能可用
map.addControl(new BMap.MapTypeControl());		//右上方地图类型控件（地图，卫星，三位）		
map.addControl(new BMap.NavigationControl());  //左上方缩放、移动控件  
	map.addControl(new BMap.ScaleControl());    	//左下方比例尺控件
map.addControl(new BMap.OverviewMapControl());    //右下方缩略图控件
map.addControl(new BMap.PanoramaControl({		//右上方全景控件
	offset: new BMap.Size(20, 50),				//Size(距离右边距, 距离左边距)
}));
map.addControl(new BMap.CityListControl({			//左上角城市列表控件
	anchor: BMAP_ANCHOR_TOP_LEFT,
    offset: new BMap.Size(80, 20),					//Size(距离左边距, 距离上边距)
    onChangeBefore: function(){// 切换城市之间事件
    	alert('before');
    },
  	onChangeAfter:function(){// 切换城市之后事件
   		alert('after');
	}
}));
	var locationControl = new BMap.GeolocationControl();			// 左下角定位控件
locationControl.setOffset(new BMap.Size(20, 60));				//Size(距离左边距, 距离下边距)
	locationControl.addEventListener("locationSuccess", function(e){
    var address = '';
    address += e.addressComponent.province;
    address += e.addressComponent.city;
    address += e.addressComponent.district;
    address += e.addressComponent.street;
    address += e.addressComponent.streetNumber;
    alert("当前定位地址为：" + address);
});
	locationControl.addEventListener("locationError",function(e){
	alert(e.message);
	});
map.addControl(locationControl);





// 创建地址解析器实例
var geoc = new BMap.Geocoder();  
map.localToAddress = function(){
	alert("定位到这个位置");
};

// 将地址解析结果显示在地图上,并调整地图视野
geoc.getPoint("湖北省武汉市江汉区新华路186号", function(point){
	if (point) {
		map.centerAndZoom(point, 16);
		map.addOverlay(new BMap.Marker(point));
	}else{
		alert("您选择地址没有解析到结果!");
	}
}, "武汉市");


//添加事件		
map.addEventListener("click", function(e){ 		//点击地图事件
	map.clearOverlays();						//清除标注
		point = new BMap.Point(e.point.lng, e.point.lat);
	var marker = new BMap.Marker(point);
	marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	map.addOverlay(marker);
/* 			alert("您点击了地图：" + e.point.lng + ", " + e.point.lat);    */ 


	var pt = e.point;
	geoc.getLocation(pt, function(rs){
		var addComp = rs.addressComponents;
		alert(addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber);
	}); 
});

map.addEventListener("dragend", function(){    //拖拽地图事件
	var center = map.getCenter();    
/* 			alert("地图中心点变更为：" + center.lng + ", " + center.lat);    */ 
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