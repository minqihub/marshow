package com.thirdParty.weChat.wxInterface;

import java.util.HashMap;
import java.util.Map;

import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 微信素材
 * @author minqi 2017-11-04 20:40:04
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738726
 */
public class WXMedia {
	
	//type：图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	
	
	/**
	 * 新增临时素材
	 * 媒体文件在微信后台保存时间为3天，即3天后media_id失效
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map addTempMedia(String json){
		Map data = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String access_token = tokenMap.get("access_token").toString();
			String type = "";
			
			String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + access_token + "&type=" + type;
			
//			returnMap = HttpUtils.doPostStream(url, headers, querys, body);
			
			if(returnMap.get("errcode").equals("0")){
				returnMap.put("MSGID", "S");
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "必填参数未填写");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	/**
	 * 获取临时素材
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getTempMedia(String json){
		Map data = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String access_token = tokenMap.get("access_token").toString();
			String media_id = "";

			String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + access_token + "&media_id=" + media_id;
			
			returnMap = HttpUtils.doGet(url, null, null);
			
			if(returnMap.get("errcode").equals("0")){
				returnMap.put("MSGID", "S");
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "必填参数未填写");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
