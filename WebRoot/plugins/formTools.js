//前台工具类

var form = {};



//登陆（密码加密；登陆成功后将userInfo写到cookie中，并设置有效期）
form.login = function(json){
	var json = {"json":JSON.stringify(json)};
	var url = this.getURL("FormUrl") + "/trust/login/login.do";
	return this.ajax(json, url);
};

//检查登陆状态，通过redirectUrl参数跳回，所以要求login界面需要判断是否有redirectUrl这个参数进行登陆成功后的跳转
form.checkLogin = function(){
	var redirectUrl = location.href;
	var json = {"json":JSON.stringify({})};
	var url = this.getURL("FormUrl") + "/trust/login/checkLogin.do";
	var returnData = this.ajax(json, url);
	if(returnData.MSGID == "S"){
		return true;
	}
	window.lacation.href = this.getURL("FormUrl") + "/login.html?" + redirectUrl;
	return false;
};

//检查客户端类型_2017-08-03 17:33:52
form.getClient = function(){
	var uaInfo = navigator.userAgent;
	//Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36
	//Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1
	//Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.23 Mobile Safari/537.36
	//Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1
	//Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.23 Mobile Safari/537.36
	
	if(uaInfo.toLowerCase().match(/MicroMessenger/i) == "micromessenger"){
		return "wechat";
	}else if(uaInfo.toLowerCase().match(/Windows/i) == "windows"){
		return "pc";
	}else if(uaInfo.toLowerCase().match(/Android/i) == "android" || uaInfo.toLowerCase().match(/IPhone/i) == "iphone" 
		|| uaInfo.toLowerCase().match(/IPad/i) == "ipad"){
		return "mobile";
	}else{
		return "other";
	}
};

//获取url参数_2017-08-03 17:47:29
form.getUrlParams = function(src){
	var json = {};
	var paramaters = src.substring(src.indexOf("?") + 1, src.length);
	if(src.indexOf("?") != -1 && !form.isNull(paramaters)){
		paramaters = paramaters.split("&");
		for( var i=0; i<paramaters.length; i++ ){
			var paramater = paramaters[i];
			var key = paramater.substring(0, paramater.indexOf("="));
			var value = paramater.substring(paramater.indexOf("=") + 1, paramater.length);
//			json[key] = value;
			json[key] = decodeURI(value);	//解决url参数中文乱码
		}
	}
	return json;
};

//同步加载_2017-08-03 21:04:01
/**
 * 1.访问的.do方法，必须有返回内容，不是指返回值，而是页面内容
 * 2.JSON.parse()方法，只能转换json格式的字符串，map格式字符串无法解析
 * 3.建议将所有的.do方法的返回内容进行包装{"data":返回值,"":""}，并强制将返回值map转换为json格式
 * 4.待优化：是否选择加密传输
 */
form.ajax = function(json,url){debugger
	var ajaxObj = {async : false};
	ajaxObj['type'] = "POST";
	ajaxObj['url'] = url;
//	ajaxObj['data'] = {"json":JSON.stringify(json)};
	ajaxObj['data'] = json;
	ajaxObj['error'] = function(){
		alert("异步加载失败!");
	};
	ajaxObj['success'] = function(data){debugger
		var type = typeof data;
		if(type == "object"){
			returnData = data;
		}else if(type == "string"){
			returnData = JSON.parse(data);
		}
	};
	var returnData = null;
	$.ajax(ajaxObj);
	return returnData;
};

//异步加载（.do方法必须要有返回内容）_2017-08-03 19:37:59
form.ajaxCallBack = function(json,url,func){
	var ajaxObj = {async : true};
	ajaxObj['type'] = "POST";
	ajaxObj['url'] = url;
//	ajaxObj['data'] = {"json":JSON.stringify(json)};
	ajaxObj['data'] = json;
	ajaxObj['error'] = function(){
		alert("异步加载失败!");
	};
	ajaxObj['success'] = function(data){
		func(data);
	};
	$.ajax(ajaxObj);
};


form.getTimestamp = function(addDay){debugger
	var d = new Date();
	if(addDay instanceof Date){
		d = addDay;
	}else{
		d.setDate(d.getDate()+addDay*1);//当前日期+几天
	}
	var FullYear = d.getFullYear();
	var Month = d.getMonth()+1<10?'0'+(d.getMonth()+1):d.getMonth()+1;
	var Day = d.getDate()<10?'0'+d.getDate():d.getDate();
	var Hours = d.getHours()<10?'0'+d.getHours():d.getHours();
	var Minutes = d.getMinutes()<10?'0'+d.getMinutes():d.getMinutes();
	var Seconds = d.getSeconds()<10?'0'+d.getSeconds():d.getSeconds();
	var str=FullYear+Month+Day+Hours+Minutes+Seconds; 
	return str; 
};

form.formatDate = function(addDay,time){debugger
	var d = new Date();
	if(addDay instanceof Date){
		d = addDay;
	}else{
		d.setDate(d.getDate()+addDay*1);//当前日期+几天
	}
	var str=''; 
	var FullYear = d.getFullYear();
	var Month = d.getMonth()+1<10?'0'+(d.getMonth()+1):d.getMonth()+1;
	var Day = d.getDate()<10?'0'+d.getDate():d.getDate();
	var Hours = d.getHours()<10?'0'+d.getHours():d.getHours();
	var Minutes = d.getMinutes()<10?'0'+d.getMinutes():d.getMinutes();
	var Seconds = d.getSeconds()<10?'0'+d.getSeconds():d.getSeconds();
	str +=FullYear+'-'; //获取当前年份 
	str +=Month+'-'; //获取当前月份（0——11） 
	if(time==1){
		str +=Day;
	}else if(time==2){
		str +=Day+' '; 
		str +=Hours+':'; 
		str +=Minutes+':'; 
		str +=Seconds; 
	}
	return str; 
};

//获取增加时间后的日期
form.getDay = function(addDayCount){debugger
	var dd = new Date();
	dd.setDate(dd.getDate()+addDayCount);//获取AddDayCount天后的日期 
	var y = dd.getFullYear(); 
	var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate(); //获取当前几号，不足10补0
	return y+"-"+m+"-"+d;
};

//获取时间星期几，格式：yyyy-MM-dd
form.getWeek = function(day){debugger
	var dateArr = day.split("-");
	var date = dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0]; 
    var day = new Date(Date.parse(date)); 
    var today = new Array('星期日','星期一','星期二','星期三','星期四','星期五','星期六');  
    var week = today[day.getDay()];
	return week;
};

form.getType = function(str){debugger
	if(typeof str == "undefined"){
		return "undefined";
	}else if(str == null){
		return "null";
	}else if(typeof str == "string" && (str === "" || str === "undefined" || str === "null")){
		return "string";
	}else if(typeof str == "object" && $.isArray(str) && str.length == 0){
		return "list";
	}else if(typeof str == "object" && $.isEmptyObject(str)){
		return "map";
	}
};

//判断是否为空
form.isNull = function(str){
	if(typeof str == "undefined"){
		return true;
	}else if(str == null){
		return true;
	}else if(typeof str == "string" && (str === "" || str === "undefined" || str === "null")){
		return true;
	}else if(typeof str == "object" && $.isArray(str) && str.length == 0){
		return true;
	}else if(typeof str == "object" && $.isEmptyObject(str)){
		return true;
	}else{
		return false;
	}
};

//判断是否为方法
form.isFunction = function(func){
	if(!this.isNull(func) && typeof func == "function"){
		return true;
	}
	return false;
};


//检查是否纯数字
form.isNumber = function(value){
	var reg = /^[0-9]*$/g;
	if(reg.test(value) && !JL.isNull(value)){
		return true;
	}
	return false;
};

//检查是否纯字母
form.isLetter = function(value){
	var reg = /^[a-zA-Z]*$/g;
	if(reg.test(value) && !JL.isNull(value)){
		return true;
	}
	return false;
};

//检查是否数字+纯字母（过滤特殊字符）
form.isNumberLetter = function(value){
	var reg = /^[0-9a-zA-Z]*$/g;
	if(reg.test(value) && !JL.isNull(value)){
		return true;
	}
	return false;
};

//检查邮箱地址
form.isEmail = function(value){
	var regEmail = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
	if(regEmail.test(value) && !JL.isNull(value)){
		return true;
	}
	return false;
};

//检查手机号
form.isPhone = function(value){debugger
	var regMobile = /^0?1[3|4|5|7|8][0-9]\d{8}$/;
	if(!JL.isNull(value) && !isNaN(value) && regMobile.test(value)){
		return true;
	}
	return false;
};

//检查身份证号码
form.isIDNum = function(card){debugger
    //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字、X、x
    var reg = /(^\d{15}$)|(^\d{17}(\d|X|x)$)/;  
    if(reg.test(card) === false){  
        return false;  
    }
    return true;  
};

//格式化、反格式化金额（每3位分隔符）_2017-08-03 19:00:37
form.formatMoney = function(money, flag){
	money = money + "";
	var newMoney = "";
	var count = 1;
	if(flag || form.isNull(flag)){					//格式化（加上分隔符）
		if(money.indexOf('.') != -1){
			var moneyAry1 = new Array();
			moneyAry1 = money.split(".");
			var moneyAry2 = new Array();
			moneyAry2 = moneyAry1[0].split("");
			for ( var i = moneyAry2.length - 1; i >= 0; i--) {
				if(count % 3 == 0 && i != 0){		//从后往前，每3位加分隔符，首位不加分隔符
					newMoney = ',' + moneyAry2[i] + newMoney;
				}else{
					newMoney = moneyAry2[i] + newMoney;
				}
				count ++;
			}
			newMoney = newMoney + '.' +moneyAry1[1];
		}else{
			var moneyAry = new Array();
			moneyAry = money.split("");
			for ( var i = moneyAry.length - 1; i >= 0; i--) {
				if(count % 3 == 0 && i != 0){		//从后往前，每3位加分隔符，首位不加分隔符
					newMoney = ',' + moneyAry[i] + newMoney;
				}else{
					newMoney = moneyAry[i] + newMoney;
				}
				count ++;
			}
		}
	}else{											//反格式化（去掉分隔符）
		newMoney = money.replace(/,/g, "");			//js没有replaceAll方法，用正则去匹配
	}
	return newMoney;
};




//项目头名称
form.projectName = {
	"FormUrl" : "marshow",
	"OutSideWeb" : "",
	"ResourceUrl" : "jlo2oResource"
};

//域名、端口
form.headUrl = location.protocol + "//" + location.host;

//项目头名称，获取当前访问路径_2017-08-03 19:11:15
form.getURL = function(key) {
	var url = "";
	if(this.projectName[key] == ""){
		url = this.headUrl + this.projectName[key];
	}else{
		url = this.headUrl + "/" + this.projectName[key];
	}
	return url;
};

//页面写入JS（动态绝对路径）_2017-08-03 19:19:25
form.writeJs = function(key, url){debugger
	if($("script[src*='"+this.getURL(key) + url+"']").length==0){
		$(document).find("body").append("<script type='text/javascript' src='"+ this.getURL(key) + url +"'><\/script>");	//body标签的结束标签下打入
	}
};

//页面写入CSS（动态绝对路径）_2017-08-03 19:19:21
form.writeCss = function(key, url){
	if($("link[href*='"+this.getURL(key) + url+"']").length==0){
		$(document).find("head").append("<link rel='stylesheet' type='text/css' href='"+ this.getURL(key) + url +"'\/>");
	}
};




