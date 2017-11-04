package com.thirdParty.weChat.wxInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.framework.database.DBHandler;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.thirdParty.weChat.WXTools;

/**
 * 微信门店WiFi
 * @author minqi 2017-07-31 17:25:24
 *
 */
@Controller
@RequestMapping("/trust/wXWifi")
public class WXWifi {
	
	/*			{
    "errcode":0,
    "data":{
        "totalcount":16,
        "pageindex":1,
        "pagecount":8,
        "records":[
            {
            	"shop_id":429620,
                "shop_name":"南山店",
                "ssid":"WX123",
                "ssid_list":["WX123","WX456"],
                "protocol_type":4,
                "sid":"",
                "poi_id":"285633617"
            },{
                "shop_id":7921527,
                "shop_name":"宝安店",
                "ssid":"",
                "ssid_list":[],
                "protocol_type":0,
                "sid":"",
                "poi_id":"285623614"
            }
        ]
    }
}*/
	
	/**
	 * 获取Wi-Fi门店列表（获取shop_id）
	 * 通过此接口获取WiFi的门店列表，该列表包括公众平台的门店信息、以及添加设备后的WiFi相关信息。创建门店方法请参考“微信门店接口”。
	 * 注：微信连Wi-Fi下的所有接口中的shop_id，必需先通过此接口获取。
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getWifiShopAll(Map data){
		Map returnMap = new HashMap();
		List returnList = new ArrayList();
		try {
			JSONObject range = new JSONObject();
			int pageindex = 1;
			range.put("pageindex", pageindex);							//分页下标，默认从1开始
			int pagesize = 20;
			range.put("pagesize", pagesize);							//每页的个数，默认10个，最大20个
		
			String appid = data.get("appid").toString();
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/bizwifi/shop/list?access_token=" + access_token;
		
			returnMap = HttpUtils.doPostStringForMap(url, null, null, range.toString());	//第一次调用
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
				
				JSONObject resultJson = Json.toJO(returnMap.get("data"));
				JSONArray tempList = Json.toJA(resultJson.get("records"));
				returnList.addAll(tempList);
				
				int totalcount = resultJson.getIntValue("totalcount");				//门店总条数
				int deployTime = (totalcount / pagesize) + 1;						//调用次数，忽略小数的运算
				
				for (int i = 0; i < deployTime; i++) {
					pageindex += (i + 1) ;
					range.put("pageindex", pageindex);
					returnMap = HttpUtils.doPostStringForMap(url, null, null, range.toString());	//第n次调用

					JSONObject resultJson2 = Json.toJO(returnMap.get("data"));
					JSONArray tempList2 = Json.toJA(resultJson2.get("records"));
					returnList.addAll(tempList2);
				}
			}else{
				throw new Exception();
			}
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信接口失败");
		}
		return returnMap;
	}
	
	/**
	 * 查询门店Wi-Fi信息
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map queryWifiShopInfo(Map data){
		Map returnMap = new HashMap();
		try {
			String shop_id = data.get("shop_id").toString();
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/bizwifi/shop/get?access_token=" + access_token;
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("shop_id", shop_id);				//微信的wifi门店id
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		return returnMap;
	}
	
	
	/**
	 * 添加密码型设备
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map addWifiDevice(Map data){
		Map returnMap = new HashMap();
		try {
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/bizwifi/device/add?access_token=" + access_token;
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("shop_id", data.get("shop_id").toString());				//微信的wifi门店id
			sendJson.put("ssid", data.get("ssid").toString());						//32个字符以内
			sendJson.put("password", data.get("password").toString());				//8-24个字符；不能包含中文字符
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		return returnMap;
	}
	
	
	/**
	 * 查询设备
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map queryWifiDeviceInfo(Map data){
		Map returnMap = new HashMap();
		try {
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/bizwifi/device/list?access_token=" + access_token;
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("pageindex", 1);								//分页下标，默认从1开始
			sendJson.put("pagesize", 20);								//每页的个数，默认10个，最大20个
			sendJson.put("shop_id", data.get("shop_id").toString());	//微信的wifi门店id
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		return returnMap;
	}
	

	/**
	 * 获取物料二维码
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getWifiQrcode(Map data){
		Map returnMap = new HashMap();
		try {
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/bizwifi/qrcode/get?access_token=" + access_token;
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("shop_id", data.get("shop_id").toString());				//微信的wifi门店id
			sendJson.put("ssid", data.get("ssid").toString());
			sendJson.put("img_id", 1);												//物料二维码
			
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		return returnMap;
	}
	
	/**
	 * 设置商家主页、欢迎语、连网完成页
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map setWifiOther(Map data){
		Map returnMap = new HashMap();
		try {
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("shop_id", data.get("shop_id").toString());				//微信的wifi门店id
			sendJson.put("template_id", 1);
			JSONObject struct = new JSONObject();
			struct.put("url", data.get("url").toString());							//物料二维码
			sendJson.put("struct", struct);
			
			String url = "https://api.weixin.qq.com/bizwifi/homepage/set?access_token=" + access_token;
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			
			sendJson.remove("template_id");
			sendJson.remove("struct");
			sendJson.put("bar_type", 3);//0--欢迎光临+公众号名称；1--欢迎光临+门店名称；2--已连接+公众号名称+WiFi；3--已连接+门店名称+Wi-Fi
			url = "https://api.weixin.qq.com/bizwifi/bar/set?access_token=" + access_token;
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			
			sendJson.remove("bar_type");
			sendJson.put("finishpage_url", data.get("url").toString());
			url = "https://api.weixin.qq.com/bizwifi/finishpage/set?access_token=" + access_token;
			returnMap = HttpUtils.doPostStringForMap(url, null, null, sendJson.toString());
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		return returnMap;
	}
	
	
	
	
	
}