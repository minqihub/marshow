/**
 * 百度地图js合集
 * 注意：	1.调用页面需引用<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=eQtoeOZrFimZG7Eg5k8eIMffGNpOxLOA"></script>
 * 		 	2.调用依赖formTools.js和jquery
 */
var bdMapJs = {};


//初始化标记
bdMapJs.map = null;

//初始化地图
bdMapJs.init = function(ElementID){debugger
	
	if(ElementID == "" || ElementID == null){
		return null
	}
	if(this.map != null){
		return this.map;
	}
	
	var map = new BMap.Map(ElementID);          	//创建地图实例（<div id="container"></div>）
	var point = new BMap.Point(116.404, 39.915);  	//创建点坐标  
	map.enableScrollWheelZoom();					//开启鼠标滚轮缩放功能
	map.enableContinuousZoom(); 					//开启地图惯性拖动
	map.centerAndZoom(point, 15);                	//初始化地图，设置中心点坐标和地图级别（视图会以该点居中）
	
	this.map = map;
	return this.map;
}

