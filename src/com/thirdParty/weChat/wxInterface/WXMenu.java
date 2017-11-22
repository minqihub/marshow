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
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map createMenu(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + tokenMap.get("access_token");
			
			JSONObject sendJson = new JSONObject();

			//一级菜单
			JSONObject button = new JSONObject();
			
			
			
			
			//菜单匹配规则，字段内容不填则不做匹配
			JSONObject matchrule = new JSONObject();
			matchrule.put("tag_id", "");					//用户标签的id，可通过用户标签管理接口获取
			matchrule.put("sex", "");						//性别：男1女2，
			matchrule.put("country", "");					//国家信息，是用户在微信中设置的地区，具体请参考地区信息表
			matchrule.put("province", "");					//省份信息，是用户在微信中设置的地区，具体请参考地区信息表
			matchrule.put("city", "");						//城市信息，是用户在微信中设置的地区，具体请参考地区信息表
			matchrule.put("client_platform_type", "");		//客户端版本，当前只具体到系统型号：IOS(1), Android(2),Others(3)
			matchrule.put("language", "zh_CN");				//语言
			

			sendJson.put("button", button);
			sendJson.put("matchrule", matchrule);
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必填参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	
	
	/**
	 * 删除自定义菜单
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map delMenu(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String url = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=" + tokenMap.get("access_token");
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("menuid", data.get("menuid").toString());
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必填参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	
	
}
