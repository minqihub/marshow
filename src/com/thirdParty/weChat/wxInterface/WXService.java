package com.thirdParty.weChat.wxInterface;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 微信公众号客服
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547
 * @author minqi 2017-09-20 13:04:11
 *
 */
public class WXService {

	/**
	 * 添加微信公众号客服
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map addService(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=" + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("kf_account", data.get("test1@test"));		//账号前缀@公众号微信号
		sendJson.put("nickname", data.get("客服1"));
		sendJson.put("password", data.get("pswmd5"));
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	/**
	 * 修改微信公众号客服
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map editService(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=" + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("kf_account", data.get("test1@test"));
		sendJson.put("nickname", data.get("客服1"));
		sendJson.put("password", data.get("pswmd5"));
		
		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	/**
	 * 删除微信公众号客服；请求方式GET
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map delService(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=" + tokenMap.get("access_token");
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("kf_account", data.get("test1@test"));
		sendJson.put("nickname", data.get("客服1"));
		sendJson.put("password", data.get("pswmd5"));
		
		Map returnMap = new HashMap();
		try {
//			returnMap = HttpUtils.doGet(url, headers, querys));
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	

	/**
	 * 设置微信公众号客服头像；请求方式POST/FORM
	 * 开发者可调用本接口来上传图片作为客服人员的头像，头像图片文件必须是jpg格式，推荐使用640*640大小的图片以达到最佳效果
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map setServiceHeadImg(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT" + tokenMap.get("access_token");
		
		//调用示例：使用curl命令，用FORM表单方式上传一个多媒体文件，curl命令的具体用法请自行了解
		
		Map returnMap = new HashMap();
		try {
//			returnMap = HttpUtils.doPostString(url, null, null, sendJson.toString());
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	/**
	 * 获取所有微信公众号客服账号；请求方式GET
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getAllService(String appid, String json){
		Map data = Json.toMap(json);
		
		Map tokenMap = WXTools.getWeChatToken(appid);
		String url = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=" + tokenMap.get("access_token");

		Map returnMap = new HashMap();
		try {
			returnMap = HttpUtils.doGet(url, null, null);
			
/*	请求结果示例		
			{
			    "kf_list": [
			        {
			            "kf_account": "test1@test", 
			            "kf_nick": "ntest1", 
			            "kf_id": "1001"
			            "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0"
			        }, 
			        {
			            "kf_account": "test2@test", 
			            "kf_nick": "ntest2", 
			            "kf_id": "1002"
			            "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw /0"
			        }, 
			        {
			            "kf_account": "test3@test", 
			            "kf_nick": "ntest3", 
			            "kf_id": "1003"
			            "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw /0"
			        }
			    ]
			}
*/		
			
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
