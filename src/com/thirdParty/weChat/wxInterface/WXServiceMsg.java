package com.thirdParty.weChat.wxInterface;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 微信客服消息
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547
 * 仅在公众号开启服务器链接，且绑定了公众号客服
 * @author minqi 2017-09-20 12:47:48
 *
 */
public class WXServiceMsg {
	
	
	/**
	 * 公众号接收用户发送的消息类型
	 */
	public static final String MsgType_text = "text";					//文本消息
	public static final String MsgType_image = "image";					//图片消息
	public static final String MsgType_voice = "voice";					//语音消息
	public static final String MsgType_video = "video";					//视频消息
	public static final String MsgType_shortvideo = "shortvideo";		//小视频消息
	public static final String MsgType_location = "location";			//地理位置消息
	public static final String MsgType_link = "link";					//链接消息
	
	private static final String DEPLOY_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
	
	
	/**
	 * 公众号被动发送（自动回复）
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140543
	 * @param receiveData 用户发送的消息，示例：{Content=123123, CreateTime=1505878925, ToUserName=gh_4cd6ce95f880, FromUserName=oz29Y0rzM_1KT1CyySU_Zh7nPJYA, MsgType=text, MsgId=6467700735044133770}
	 */
	@SuppressWarnings("rawtypes")
	public static String autoReply_test(Map receiveData){
		String replyText = "";
		
		if(receiveData.get("MsgType").toString().equals(MsgType_text)){
			replyText = "测试自动回复。您发送的是文本消息，内容：" + receiveData.get("Content");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_image)){
			replyText = "测试自动回复。您发送的是图片消息，图片链接：" + receiveData.get("PicUrl");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_voice)){
			replyText = "测试自动回复。您发送的是语音消息，媒体Id：" + receiveData.get("MediaId") + "；语音识别结果：" + receiveData.get("Recognition");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_video)){
			replyText = "测试自动回复。您发送的是视频消息，媒体Id：" + receiveData.get("MediaId") + "；视频缩略图Id：" + receiveData.get("ThumbMediaId");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_shortvideo)){
			replyText = "测试自动回复。您发送的是短视频消息，媒体Id：" + receiveData.get("MediaId") + "；段视频缩略图Id：" + receiveData.get("ThumbMediaId");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_location)){
			replyText = "测试自动回复。您发送的是地址信息，经度：" + receiveData.get("Location_Y") + "，纬度：" + receiveData.get("Location_X") + "；地理位置信息：" + receiveData.get("Label");
		}else if(receiveData.get("MsgType").toString().equals(MsgType_link)){
			replyText = "测试自动回复。您发送的是链接信息，标题：" + receiveData.get("Title") + "，描述：" + receiveData.get("Description") + "；链接：" + receiveData.get("Url");
		}
		
		String replyXml = "<xml>"
				+ "<ToUserName><![CDATA[" + receiveData.get("FromUserName") + "]]></ToUserName>"
				+ "<FromUserName><![CDATA[" + receiveData.get("ToUserName") + "]]></FromUserName>"
				+ "<CreateTime>" + WXTools.create_timestamp() + "</CreateTime>"
				+ "<MsgType><![CDATA[text]]></MsgType>"
				+ "<Content><![CDATA[" + replyText + "]]></Content>"
				+ "</xml>";
		return replyXml;
	}
	
	/**
	 * 将消息转发给客服
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140543
	 * @param receiveData 用户发送的消息，示例：{Content=123123, CreateTime=1505878925, ToUserName=gh_4cd6ce95f880, FromUserName=oz29Y0rzM_1KT1CyySU_Zh7nPJYA, MsgType=text, MsgId=6467700735044133770}
	 */
	@SuppressWarnings("rawtypes")
	public static String autoReply_toService(Map receiveData){
		//转发给客服
		String replyXml = " <xml>"
				+ "<ToUserName><![CDATA[touser]]></ToUserName>"
				+ "<FromUserName><![CDATA[fromuser]]></FromUserName>"
				+ "<CreateTime>" + WXTools.create_timestamp() + "</CreateTime>"
				+ "<MsgType><![CDATA[transfer_customer_service]]></MsgType>"
				+ "</xml>";
		return replyXml;
	}
	
	/**
	 * 公众号主动发送文本消息
	 * @param appid
	 * @param json 参数openid、content
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map sendText(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = DEPLOY_URL + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("touser", data.get("openid"));
		sendJson.put("msgtype", "text");
		
		JSONObject text = new JSONObject();
		text.put("content", data.get("content"));
		sendJson.put("text", text);
		
		if(!DataUtils.isNull(data.get("kf_account"))){
			//以指定客服身份回复
			JSONObject customservice = new JSONObject();
			customservice.put("kf_account", data.get("kf_account"));		//"test1@kftest"
			sendJson.put("customservice", customservice);
		}
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	/**
	 * 公众号主动发送图片消息
	 * @param appid
	 * @param json 参数openid、media_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map sendImage(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = DEPLOY_URL + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("touser", data.get("openid"));
		sendJson.put("msgtype", "image");
		
		JSONObject image = new JSONObject();
		image.put("media_id", data.get("media_id"));
		sendJson.put("image", image);

		if(!DataUtils.isNull(data.get("kf_account"))){
			//以指定客服身份回复
			JSONObject customservice = new JSONObject();
			customservice.put("kf_account", data.get("kf_account"));		//"test1@kftest"
			sendJson.put("customservice", customservice);
		}
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	/**
	 * 公众号主动发送语音消息
	 * @param appid
	 * @param json 参数openid、media_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map sendVoice(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = DEPLOY_URL + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("touser", data.get("openid"));			//用户openid
		sendJson.put("msgtype", "voice");
		
		JSONObject voice = new JSONObject();
		voice.put("media_id", data.get("media_id"));
		sendJson.put("voice", voice);

		if(!DataUtils.isNull(data.get("kf_account"))){
			//以指定客服身份回复
			JSONObject customservice = new JSONObject();
			customservice.put("kf_account", data.get("kf_account"));		//"test1@kftest"
			sendJson.put("customservice", customservice);
		}
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	/**
	 * 公众号主动发送视频消息
	 * @param appid
	 * @param json 参数openid、video
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map sendVideo(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = DEPLOY_URL + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("touser", data.get("openid"));
		sendJson.put("msgtype", "video");
		
		JSONObject video = new JSONObject();
		video.put("media_id", data.get("media_id"));
		video.put("thumb_media_id", data.get("thumb_media_id"));
		video.put("title", data.get("title"));
		video.put("description", data.get("description"));
		sendJson.put("video", video);

		if(!DataUtils.isNull(data.get("kf_account"))){
			//以指定客服身份回复
			JSONObject customservice = new JSONObject();
			customservice.put("kf_account", data.get("kf_account"));
			sendJson.put("customservice", customservice);
		}
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			returnMap.put("", "");
		}
		return returnMap;
	}
	
	
	
	
}
