/**
 * 微信js-sdk合集
 * 注意：	1.调用页面需引用<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
 * 		 	2.调用依赖formTools.js和jquery
 */
var weChatJs = {};


//初始化标记，避免重复初始化
weChatJs.isInit = false;		

//微信跳转授权获取用户code；5分钟未被使用自动过期
weChatJs.jumpGetCode = function(data){debugger
	var appid = data.appid;
	var redirect_uri = data.redirect_uri || window.location.href;
	var scope = data.scope || "snsapi_base";	//snsapi_base静默授权；snsapi_userinfo用户手动授权
	var state = data.state || "default";
	var jumpUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid +
					"&redirect_uri=" + encodeURIComponent(redirect_uri) +
					"&response_type=code" +
					"&scope=" + scope +
					"&state=" + state + "#wechat_redirect";
	//页面跳转形式：redirect_uri/?code=CODE&state=STATE
	window.location.href = jumpUrl;
}


//初始化
weChatJs.init = function(appid, func){debugger
	if(weChatJs.isInit){
		return;
	}
	//获取微信js配置参数
	var json = {"json":JSON.stringify({"appid":appid, "url":window.location.href})};
	var url = form.getprojectUrl + "/wxTools/weChatJsSign.do";
	var wxConfig = form.ajax(json, url).data;
	
	wx.config({
	    debug: false, 									// 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: wxConfig.appid, 							// 必填，公众号的唯一标识
	    timestamp: wxConfig.timestamp, 					// 必填，生成签名的时间戳
	    nonceStr: wxConfig.nonceStr, 					// 必填，生成签名的随机串
	    signature: wxConfig.signature,					// 必填，签名，见附录1
	    jsApiList: ['scanQRCode','getLocation','openLocation','chooseImage','uploadImage','downloadImage',
	    			'onMenuShareTimeline','startRecord','stopRecord'] 		// 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});
	
	// config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
	wx.error(function(res){
		alert("微信config信息验证失败："+ JSON.stringify(res));
	});
	
	// config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
	wx.ready(function(){debugger
		weChatJs.isInit = true;
		if(typeof func == "function"){
			func();
		}
	});
};

//微信扫码
weChatJs.scanQRCode = function(data, func){debugger
	this.init(data.appid);
	wx.scanQRCode({
	    needResult: 1, 						// 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
	    scanType: ["qrCode","barCode"], 	// 可以指定扫二维码还是一维码，默认二者都有
	    success: function (res) {debugger
//	    	alert("微信扫一扫数据："+ JSON.stringify(res));
		    var result = res.resultStr; 	// 当needResult 为 1 时，扫码返回的结果
		    if(typeof func == "function"){
		    	func(res);
		    }
		}
	});
};

//获取地理位置
weChatJs.getLocation = function(data, func){debugger
	this.init(data.appid);
	wx.getLocation({
	    type: 'gcj02', 						// 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
	    success: function (res) {debugger
//	    	alert("获取的地理位置数据："+ JSON.stringify(res));
	        var latitude = res.latitude; 	// 纬度，浮点数，范围为90 ~ -90
	        var longitude = res.longitude; 	// 经度，浮点数，范围为180 ~ -180。
	        var speed = res.speed; 			// 速度，以米/每秒计
	        var accuracy = res.accuracy; 	// 位置精度
	        if(typeof func == "function"){
		    	func(res);
		    }
	    }
	});
};

//打开微信内置地图
weChatJs.openLocation = function(data){debugger
	this.init(data.appid);
	wx.openLocation({
	    latitude: data.lat, 				// 纬度，浮点数，范围为90 ~ -90
	    longitude: data.lon, 				// 经度，浮点数，范围为180 ~ -180。
	    name: data.name, 					// 位置名
	    address: data.address, 				// 地址详情说明
	    scale: 20, 							// 地图缩放级别,整形值,范围从1~28。默认为最大
	    infoUrl: data.url 					// 在查看位置界面底部显示的超链接,可点击跳转
	});
};

//拍照或从手机相册中选图接口
weChatJs.chooseImage = function(data, func){debugger
	this.init(data.appid);
	wx.chooseImage({
	    count: 1, 									// 默认9
	    sizeType: ['original', 'compressed'], 		// 可以指定是原图还是压缩图，默认二者都有
	    sourceType: ['album', 'camera'], 			// 可以指定来源是相册还是相机，默认二者都有
	    success: function (res) {debugger
	        var localIds = res.localIds; 			// 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
	        if(typeof func == "function"){
		    	func(res);
		    }
	    }
	});
}

//上传图片接口
weChatJs.uploadImage = function(data, func){debugger
	this.init(data.appid);
	wx.uploadImage({
	    localId: data.localId, 						// 需要上传的图片的本地ID，由chooseImage接口获得
	    isShowProgressTips: 1, 						// 默认为1，显示进度提示
	    success: function (res) {debugger
	    	//上传图片有效期3天，可用微信多媒体接口下载图片到自己的服务器，此处获得的 serverId 即 media_id
	        var serverId = res.serverId; 			// 返回图片的服务器端ID
	        if(typeof func == "function"){
		    	func(res);
		    }
	    }
	});
}

//下载图片接口
weChatJs.downloadImage = function(data, func){debugger
	this.init(data.appid);
	wx.downloadImage({
	    serverId: data.serverId, 					// 需要下载的图片的服务器端ID，由uploadImage接口获得
	    isShowProgressTips: 1, 						// 默认为1，显示进度提示
	    success: function (res) {debugger
	        var localId = res.localId; 				// 返回图片下载后的本地ID
	        if(typeof func == "function"){
		    	func(res);
		    }
	    }
	});
}

//获取“分享到朋友圈”按钮点击状态及自定义分享内容接口
weChatJs.onMenuShareTimeline = function(data){debugger
	this.init(data.appid, function(){debugger
		wx.onMenuShareTimeline({
		    title: '快来围观智慧社区', 							// 分享标题
		    link: 'http://www.marshow.top', 					// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		    imgUrl: 'http://www.marshow.top/images/weibo.png', 	// 分享图标
		    success: function () { debugger
		        // 用户确认分享后执行的回调函数
		    },
		    cancel: function () { debugger
		        // 用户取消分享后执行的回调函数
		    },
		    trigger: function (res) { debugger 
	            //  alert('用户点击发送给朋友');    
		    },
		});
	});
}

//开始录音
weChatJs.startRecord = function(data){debugger
	this.init(data.appid);
	wx.startRecord();
}

//停止录音
weChatJs.stopRecord = function(data, func){debugger
	this.init(data.appid);
	wx.stopRecord({
	    success: function (res) {
	        var localId = res.localId;
	        if(typeof func == "function"){
		    	func(res);
		    }
	    }
	});
}

//监听录音自动停止
weChatJs.onVoiceRecordEnd = function(data){debugger
	this.init(data.appid);
	wx.onVoiceRecordEnd({
	    // 录音时间超过一分钟没有停止的时候会执行 complete 回调
	    complete: function (res) {
	        var localId = res.localId; 
	    }
	});
}

//播放录音
weChatJs.playVoice = function(data){debugger
	this.init(data.appid);
	wx.playVoice({
	    localId: data.localId 			// 需要播放的音频的本地ID，由stopRecord接口获得
	});
}












