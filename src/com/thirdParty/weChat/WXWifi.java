package com.thirdParty.weChat;

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
import com.framework.utils.Json;

/**
 * 微信门店WiFi
 * @author minqi 2017-07-31 17:25:24
 *
 */
@Controller
@RequestMapping("/trust/wXWifi")
public class WXWifi {
	
	/**
	 * 获取shop_id（此为轮询方法）
	 * 说明：poi_id是微信门店的id（String）；shop_id：WiFi门店的id（int）；sid：商户自己的id（int）
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getShopId.do")
	public void getShopId() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinWifi/getShopId.do
			//获取access_token
			String access_token = WXTools.getAccessToken();
			String url = "https://api.weixin.qq.com/bizwifi/shop/list?access_token=" + access_token;
			
			JSONObject json = new JSONObject();
			json.put("pageindex", 1);				//分页下标，可不传，默认从1开始
			json.put("pagesize", 20);				//每页的个数，可不传，默认10个，最大20个
			Map resultMap = WXShop.sendJsonPost(url, json.toString());

		
		

	}
	
	/**
	 * 设置wifi门店商家主页\欢迎语
	 * 说明：poi_id是微信门店的id（String）；shop_id：WiFi门店的id（int）；sid：商户自己的id（int）
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/setWiFiConfig.do")
	public void setWiFiConfig() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinWifi/setWiFiConfig.do
			//获取access_token
			String access_token = WXTools.getAccessToken();
				String shop_id = "";
				int storeId = 123;
				
				//设置wifi欢迎语
				JSONObject json1 = new JSONObject();
				json1.put("shop_id", shop_id);
				json1.put("bar_type", 3);						//已连接+门店名称+WiFi名称。
				String url = "https://api.weixin.qq.com/bizwifi/bar/set?access_token=" + access_token;
				WXShop.sendJsonPost(url, json1.toString());
				
				//设置wifi跳转页面
				JSONObject json2 = new JSONObject();
				json2.put("shop_id", shop_id);
				json2.put("template_id", 1);
				
				JSONObject struct = new JSONObject();
				String homepageUrl = "http://www.dqfwy.com/o2o/projects/shopBaofengTV/cjdg/entrance.html?storeId="+storeId+"&appid=wx3b8e77b0a26c8a9c";
				struct.put("url", homepageUrl);
				json2.put("struct", struct);
				url = "https://api.weixin.qq.com/bizwifi/homepage/set?access_token=" + access_token;
				Map resultMap = WXShop.sendJsonPost(url, json2.toString());
					
			
			
		
	}
	
	
	
	/**
	 * 获取二维码
	 * @param XmlData 参数：img_id：物料样式编号：0-纯二维码，可用于自由设计宣传材料；1-二维码物料，155mm×215mm(宽×高)，可直接张贴
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getQrCode.do")
	public void getQrCode() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinWifi/getQrCode.do
		
			//获取access_token
			String access_token = WXTools.getAccessToken();
			String url = "https://api.weixin.qq.com/bizwifi/qrcode/get?access_token=" + access_token;
			

				JSONObject json = new JSONObject();
				json.put("shop_id", shop_id);			//门店id
				json.put("ssid", ssid);				//已添加到门店下的无线网络名称
				json.put("img_id", 0);										//纯二维码
//				json.put("img_id", 1);										//二维码物料
				Map resultMap = WXShop.sendJsonPost(url, json.toString());
				

			
		
	}
	
	/**
	 * 查询wifi设备，获取设备物理地址bssid（删除设备需要bssid字段）
	 *  @param XmlData 参数：pageindex（int）：分页下标，默认从1开始；pagesize（int）：每页的个数，最大20个
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/findWifiDevice.do")
	public Map findWifiDevice(Object shop_id) throws Exception{
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/bizwifi/device/list?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("pageindex", 1);			//分页下标，参数可不传，默认从1开始
		json.put("pagesize", 20);			//每页的个数，参数可不传，默认10个，最大20个
		if(!DataUtils.isNull(shop_id)){
			json.put("shop_id", Integer.parseInt(shop_id.toString()));		//门店id
		}
		Map resultMap = WXShop.sendJsonPost(url, json.toString());
		
		//将设备物理地址更新W_Wifi
		if(resultMap.get("errcode").toString().equals("0")){
			JSONObject data = Json.toJO(resultMap.get("data"));
			JSONArray records = Json.toJA(data.get("records")); 
			
			for (int i = 0; i < records.size(); i++) {								//循环接口数据
				JSONObject row1 = Json.toJO(records.get(i));
				
				Map queryMap = new HashMap();
				queryMap.put("shop_id", row1.get("shop_id"));
				queryMap.put("SSID", row1.getString("ssid"));
				
				Map updateMap = new HashMap();
				updateMap.put("bssid", row1.get("bssid"));							//设备的物理地址，有这个字段表示该设备已激活（连接过）
				updateMap.put("protocol_type", row1.get("protocol_type"));			//门店内设备的设备类型，0-未添加设备，4-密码型设备，31-portal型设备
				update("W_Wifi", queryMap, updateMap);
			}
		}
		return resultMap;
	}
	
	/**
	 * 添加密码型设备（此为轮询方法：将数据库已存在的wifi数据提交到微信审核）
	 * 完成后，务必获取该门店二维码，并确保用6.1以上安卓版微信或6.2.2以上IOS版微信扫码连接Wi-Fi，连网成功即表示设备添加成功。
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/addWifiDevice.do")
	public void addWifiDevice() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinWifi/addWifiDevice.do
		
		Map queryMap = new HashMap();
		queryMap.put("yxbj", 1);
		queryMap.put("wxState", 0);	//查询未提交到微信审核的wifi数据
		
		List wifiList = find("W_Wifi", queryMap, null, null);
		
		if(!wifiList.isEmpty()){
			//获取access_token
			String access_token = WXTools.getAccessToken();
			String url = "https://api.weixin.qq.com/bizwifi/device/add?access_token=" + access_token;
			
			for (int i = 0; i < wifiList.size(); i++) {
				JSONObject row = Json.toJO(wifiList.get(i));
				
				int storeId = row.getIntValue("storeId");
				Map queryMap2 = new HashMap();
				queryMap2.put("storeId", storeId);
				
				Map shopIdMap = findOne("W_Store", queryMap, null);
				int shop_id = Integer.parseInt(shopIdMap.get("shop_id").toString());
				String ssid = row.getString("SSID");
				String password = row.getString("passwd");
				
				JSONObject json = new JSONObject();
				json.put("shop_id", shop_id);			//门店id
				json.put("ssid", ssid);					//无线网络设备的ssid。ssid和密码必须有一个以大写字母“WX”开头；32个字符以内；ssid支持中文，但可能因设备兼容性问题导致显示乱码，或无法连接等问题，相关风险自行承担！
				json.put("password", password);			//无线网络设备的密码。8-24个字符；不能包含中文字符；ssid和密码必须有一个以大写字母“WX”开头
				Map resultMap = WXShop.sendJsonPost(url, json.toString());
				
				//将设备信息写入W_Wifi
				if(resultMap.get("errcode").toString().equals("0")){
					Map queryMap3 = new HashMap();
					queryMap3.put("storeId", storeId);
					
					Map updateMap = new HashMap();
					updateMap.put("wxState", 1);
					updateMap.put("shop_id", shop_id);
					
					update("W_Wifi", queryMap, updateMap);
				}							
			}
		}
		
		
		
	}
	
	/**
	 * 修改指定设备
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/updateWifiDevice.do")
	public Map updateWifiDevice(String XmlData, HttpServletRequest request) throws Exception{
		Map map = Json.toMap(XmlData);
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/bizwifi/shop/update?access_token=" + access_token;
		
		int shop_id = Integer.parseInt(map.get("shop_id").toString());
		String old_ssid = map.get("old_ssid").toString();
		String ssid = map.get("ssid").toString();
		
		JSONObject json = new JSONObject();
		json.put("shop_id", shop_id);
		json.put("old_ssid", old_ssid);
		json.put("ssid", ssid);
		Map resultMap = WXShop.sendJsonPost(url, json.toString());
		
		//同步W_Wifi
		if(resultMap.get("errcode").toString().equals("0")){
			Map queryMap = new HashMap();
			queryMap.put("shop_id", shop_id);
			queryMap.put("SSID", old_ssid);
			
			Map updateMap = new HashMap();
			updateMap.put("SSID", ssid);
			update("W_Wifi", queryMap, updateMap);
		}
		return resultMap;
	}
	
	/**
	 * 删除指定设备
	 * 根据bssid删除门店下的单个设备。删除后请尽快修改设备的网络名称和密码，或停止使用设备，否则设备会自动重新添加进来。
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/delWifiDevice.do")
	public Map delWifiDevice(String XmlData, HttpServletRequest request) throws Exception{
		Map map = Json.toMap(XmlData);
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/bizwifi/device/delete?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("bssid", map.get("bssid").toString());		//需要删除的无线网络设备无线mac地址，格式冒号分隔，字符长度17个，并且字母小写，例如：00:1f:7a:ad:5c:a8
		Map resultMap = WXShop.sendJsonPost(url, json.toString());
		
		//同步W_Store
		if(resultMap.get("errcode").toString().equals("0")){
			
		}
		return resultMap;
	}
	
	/**
	 * 清空门店网络设备
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/clearAllDevice.do")
	public Map clearAllDevice(String XmlData, HttpServletRequest request) throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinWifi/clearAllDevice.do
		Map map = Json.toMap(XmlData);
		
		int shop_id = Integer.parseInt(map.get("shop_id").toString());
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/bizwifi/shop/clean?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("shop_id", shop_id);			//门店id
		Map resultMap = WXShop.sendJsonPost(url, json.toString());
		
		//同步清空W_Wifi
		if(resultMap.get("errcode").toString().equals("0")){
			Map queryMap = new HashMap();
			queryMap.put("storeId", shop_id);
			remove("W_Wifi", queryMap);
		}
		return resultMap;
	}
}