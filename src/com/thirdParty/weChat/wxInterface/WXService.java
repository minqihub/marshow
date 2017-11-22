package com.thirdParty.weChat.wxInterface;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 微信公众号客服
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1458044813
 * @author minqi 2017-09-20 13:04:11
 *
 */
public class WXService {

	/**
	 * 添加客服帐号
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map addService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String url = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=" + tokenMap.get("access_token");
			
			JSONObject sendJson = new JSONObject();
			// 账号前缀@公众号微信号
			sendJson.put("kf_account", data.get("test1@test").toString());		//帐号前缀最多10个字符，必须是英文、数字字符或者下划线
			sendJson.put("nickname", data.get("客服1").toString());				//客服昵称，最长16个字
//			sendJson.put("password", data.get("pswmd5").toString());
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		}
		return returnMap;
	}
	
	
	/**
	 * 邀请绑定客服帐号
	 * 新添加的客服帐号是不能直接使用的，只有客服人员用微信号绑定了客服账号后，方可登录Web客服进行操作。
	 * 此接口发起一个绑定邀请到客服人员微信号，客服人员需要在微信客户端上用该微信号确认后帐号才可用。
	 * 尚未绑定微信号的帐号可以进行绑定邀请操作，邀请未失效时不能对该帐号进行再次绑定微信号邀请
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map inviteService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String url = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=" + tokenMap.get("access_token");
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("kf_account", data.get("kf_account"));
			sendJson.put("invite_wx", data.get("invite_wx"));				//接收绑定邀请的客服微信号
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		}
		return returnMap;
	}
	
	
	
	
	/**
	 * 设置客服信息
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map editService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String url = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=" + tokenMap.get("access_token");
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("kf_account", data.get("test1@test"));
			sendJson.put("nickname", data.get("客服1"));
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "写入数据库失败");
		}
		return returnMap;
	}
	
	/**
	 * 删除微信公众号客服；请求方式GET
	 * @param appid
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map delService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String access_token = tokenMap.get("access_token").toString();
			String kf_account = data.get("kf_account").toString();
			
			String url = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token="+access_token+"&kf_account=" + kf_account;
			returnMap = HttpUtils.doGet(url, null, null);
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
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
	 * 查询所有客服信息
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map queryAllService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String access_token = tokenMap.get("access_token").toString();
			String url = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=" + access_token;
			
			returnMap = HttpUtils.doGet(url, null, null);
			
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	
	/**
	 * 查询在线客服信息
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map queryOnlineService(String json){
		Map data = Json.toMap(json);

		Map returnMap = new HashMap();
		try {
			Map tokenMap = WXTools.getWeChatToken(data.get("appid").toString());
			String access_token = tokenMap.get("access_token").toString();
			String url = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=" + access_token;
			
			returnMap = HttpUtils.doGet(url, null, null);
			
			returnMap.put("MSGID", "S");
			if(!returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", returnMap.get("errmsg"));
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求失败");
		}
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
