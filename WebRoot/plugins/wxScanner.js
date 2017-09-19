function openScanner(){
	if(typeof(uexScanner)=="undefined"){
	   setWebCatScan();
	   webCatScan();
	}
}

function setWebCatScan(){
	var timestamp = null;	//"1481253704";//时间戳
	var nonceStr = null;	//"08711cb9-09d0-4b61-934d-d17898410fed";//随机串
	var signature = null;	//"3e4d612c8222ae940f1bbc2c6ffeb880592d6fda";//签名
	
	var ajaxJson = {};
	ajaxJson["src"] = pubJson.getURL("FormUrl") + "/trust/weixin/sign.do";
	ajaxJson["data"] = {"url" : window.location.href,"appid":"wx3b8e77b0a26c8a9c","domain" : window.location.host};
	var resultData = JL.ajax(ajaxJson);
	if(!JL.isNull(resultData)){
		timestamp = resultData.data.timestamp;
		nonceStr = resultData.data.nonceStr;
		signature = resultData.data.signature;
	}
	
	wx.config({
		debug : false, 						// 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		appId : 'wx3b8e77b0a26c8a9c', 		// 必填，公众号的唯一标识
		timestamp : timestamp, 				// 必填，生成签名的时间戳
		nonceStr : nonceStr, 				// 必填，生成签名的随机串
		signature : signature,				// 必填，签名，见附录1
		jsApiList : ['scanQRCode','chooseImage','uploadImage','openLocation']	// 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});
}

//二维码扫描
function webCatScan(){
  wx.scanQRCode({
			
			needResult : 1,						// 默认为0，扫描结果由微信处理，1则直接返回扫描结果
			desc : 'scanQRCode desc',
			success : function(res) {
				
				var returnStr = res.resultStr;	//扫码后获取结果参数赋值给Input
			}
  		
	});
}

//上传图片
var serverIdArr = [];
var uploadImgSize = 0;

var refundImg = [];
function onChooseImage(obj){
	serverIdArr = [];
	uploadImgSize = 0;
	if(typeof(uexScanner)=="undefined"){
		setWebCatScan();
		//选取图片
		wx.chooseImage({
			count: 5,
			sizeType: ['original', 'compressed'],
			sourceType: ['album', 'camera'],
			success: function (res) {
				var localIds = res.localIds;
				for(var i=0;i<localIds.length;i++){
					JL.loading(true);
					var localId = localIds[i];
					wx.uploadImage({
						localId: localId,
						isShowProgressTips: 1,
						success: function (res) {
							uploadImgSize = uploadImgSize + 1;
							var serverId = res.serverId; // 返回图片的服务器端ID
							serverIdArr.push(serverId);
							if(uploadImgSize == localIds.length){
								ajaxJson = {};
								ajaxJson["src"] = pubJson.getURL("FormUrl")+"/weChatOnline/weChatUploadImg.do";
								ajaxJson["data"] ={"json": JSON.stringify(serverIdArr)};
								var fileList = JL.ajaxCall(ajaxJson).data.fileList;
								for(var i=0;i<fileList.length;i++){
									$("#sctp").append("<li class=\"w03\">"+
											"<img jlui=\"div_seeImg\" class=\"w12 mt10\" src=\""+fileList[i].FILE_URL+"\"/>"+
											"<span jlui=\"div_deleteImg\" style=\"position:absolute; top:0;right:2px;line-height:.6rem;padding:0 4px\">x</span></li>");
									refundImg.push(fileList[i]);
								}
								
								obj.setUI({
									"div_seeImg" : {
										"listener" : {
											"click" : function() {
												$("#seeImg").attr("src",$(this).attr("src"));
												$("#codebox").fadeIn();
												$("#seeImg").click(function(){
													$("#codebox").fadeOut();
												});
											}
										}
									},
									"div_deleteImg" : {
										"listener" : {
											"click" : function() {
												var src = $(this).prev().attr("src");
												for(var i=0;i<refundImg.length;i++){
													if(refundImg[i].FILE_URL == src){
														refundImg.remove(i);
													}
												}
												$(this).parent().remove();
											}
										}
									}
								});
								obj.applyUI("div_seeImg");
								obj.applyUI("div_deleteImg");
								JL.loading(false);
							}
						}
					});
				}
			}
		});
	}
}

//获取用户地理位置
function getLocation(){
	if(typeof(uexScanner)=="undefined"){
		setWebCatScan();
		wx.getLocation({
		    type: 'gcj02', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
		    success: function (res) {
		        var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
		        var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
		        var speed = res.speed; // 速度，以米/每秒计
		        var accuracy = res.accuracy; // 位置精度
		        //显示微信信息
		        wx.openLocation({
		            latitude: res.latitude, // 纬度，浮点数，范围为90 ~ -90
		            longitude: res.longitude, // 经度，浮点数，范围为180 ~ -180。
		            name: '测试地址1', // 位置名
		            address: '测试地址', // 地址详情说明
		            scale: 28, // 地图缩放级别,整形值,范围从1~28。默认为最大
		            infoUrl: '', // 在查看位置界面底部显示的超链接,可点击跳转
		            //成功打印信息
		            success: function(res) {
		            	console.log(res);
		            },
		            //失败打印信息
		            fail: function(err) {
		              console.log(err);
		            },
		            //完成打印信息
		            complete: function(info){
		              console.log(info);
		            }
		        });
		    }
		});
	}
}

/**
 * 删除数组指定下标或指定对象
 */
Array.prototype.remove = function(obj) {
	for (var i = 0; i < this.length; i++) {
		var temp = this[i];
		if (!isNaN(obj)) {
			temp = i;
		}
		if (temp == obj) {
			for (var j = i; j < this.length; j++) {
				this[j] = this[j + 1];
			}
			this.length = this.length - 1;
		}
	}
}