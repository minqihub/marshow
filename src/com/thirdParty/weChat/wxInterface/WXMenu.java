package com.thirdParty.weChat.wxInterface;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 公众号自定义菜单
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013
 * @author minqi
 *
 */
public class WXMenu {
	
	private static final String TYPE_view = "view";					//网页型
	private static final String TYPE_click = "click";				//点击型
	private static final String TYPE_miniprogram = "miniprogram";	//小程序型
	
	/**
	 * 创建自定义菜单
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map creatMenu(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + tokenMap.get("access_token");

		JSONObject btn = new JSONObject();
		btn.put("type", "click");
		btn.put("name", "今日歌曲");
		btn.put("key", "5gg4g545g4dg");
		
		JSONArray ary = new JSONArray();
		ary.add(btn);
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("button", ary);
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	
	
	
	
	
}
