package com.thirdParty.weChat.wxInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * 微信门店
 * @author minqi 2017-07-31 17:25:48
 *
 */
@Controller
@RequestMapping("/trust/wXShop")
public class WXShop extends DBHandler {
	
	private static final String[] available_state = {"","系统错误","审核中","审核通过","审核驳回"};
	//available_state：门店是否可用状态。1 表示系统错误、2 表示审核中、3 审核通过、4 审核驳回。1、2、4 状态不返回poi_id
	
	private static final String[] update_status = {"扩展字段没有在更新中或更新已生效，可以再次更新","扩展字段正在更新中，尚未生效，不允许再次更新"};
	//update_status：扩展字段是否正在更新中。 0 表示扩展字段没有在更新中或更新已生效，可以再次更新；1 表示扩展字段正在更新中，尚未生效，不允许再次更新

	
	
	public Map findShop(String json){
		Map storeMap = Json.toMap(json);
		
		
		JSONObject range = new JSONObject();
		range.put("begin", 0);							//从第一条开始查询
		range.put("limit", 50);							//返回数据条数，最大允许50，默认为20
		
		String appid = storeMap.get("appid").toString();
		String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
		String url = "https://api.weixin.qq.com/cgi-bin/poi/getpoilist?access_token=" + access_token;
		
		Map returnMap = null;
		try {
			returnMap = HttpUtils.doPostString(url, null, null, range.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	
	/**
	 * 添加门店
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map addShop(String json) throws Exception{
		Map storeMap = Json.toMap(json);

		//准备地址信息
		JSONArray addressList = Json.toJA(storeMap.get("address"));
		String province = "",city = "",district = "",address = "";
		for (int j = 0; j < addressList.size(); j++) {
			Map row = Json.toMap(addressList.get(j).toString());
			if(DataUtils.isNull(row.get("LEVELS"))){
				address = row.get("VALUE").toString();
			}else if(row.get("LEVELS").toString().equals("1")){
				province = row.get("VALUE").toString();
			}else if(row.get("LEVELS").toString().equals("2")){
				city = row.get("VALUE").toString();
			}else if(row.get("LEVELS").toString().equals("3")){
				district = row.get("VALUE").toString();
			}
		}
		
		//准备图片信息
		List photo_list = new ArrayList();
		JSONArray imgList = Json.toJA(storeMap.get("storeImg"));
		for (int j = 0; j < imgList.size(); j++) {
			Map row = Json.toMap(imgList.get(j).toString());
//			String imgName = row.get("FILE_DESC").toString();
			String imgName = (j+1)+".jpg";		//图片名称
			String imgUrl = row.get("FILE_URL") + "";
			JSONObject photo_url = new JSONObject();
			photo_url.put("photo_url", WXTools.getWeixinImg(imgName, imgUrl, "appid"));
			photo_list.add(photo_url);
		}
		
		//准备门店类型
		List categoriesList = new ArrayList();
		categoriesList.add("购物,数码家电");
		
		//准备接口字段
		JSONObject data = new JSONObject();
		//门店基础信息字段
		data.put("sid", storeMap.get("storeId").toString());	//商户自己的门店id
		data.put("business_name", "暴风TV体验店");				//门店名称（仅为商户名，如：国美、麦当劳，不应包含地区、地址、分店名等信息，错误示例：北京国美）15个汉字或30个英文字符内
		data.put("branch_name", storeMap.get("storeName"));		//分店名称（不应包含地区信息，不应与门店名有重复，错误示例：北京王府井店）不超过10个字，不能含有括号和特殊字符
		data.put("province", province);							//不超过10个字
		data.put("city", city);									//不超过30个字
		data.put("district", district);							//门店所在地区，不超过10个字
		data.put("address", address);							//门店所在的详细街道地址（不要填写省市信息）：不超过80个字
		data.put("telephone", storeMap.get("mobile"));			//不超53个字符（区号分机号用-连接）
		data.put("categories", categoriesList);					//门店的类型["美食,小吃快餐"]
		data.put("offset_type", 1);								//1为火星坐标2为sogou经纬度3为百度经纬度4为mapbar经纬度5为GPS坐标6为sogou墨卡托坐标
		data.put("longitude", storeMap.get("longitude"));		//经度115.32375
		data.put("latitude", storeMap.get("latitude"));			//纬度25.097486
		
		//门店服务信息字段（均为非必填）
		data.put("photo_list", photo_list);						//图片列表不超过20张，[{"photo_url":"https://.."}，{"photo_url":"https://.."}]
		data.put("recommend", "");								//推荐品，不超过200字。
		data.put("special", "");								//特色服务，不超过200字。免费wifi，外卖服务
		data.put("introduction", storeMap.get("introduce"));	//商户简介，不超过300字。
		data.put("open_time", storeMap.get("openStartTime")+"-"+storeMap.get("openEndTime"));//8:00-20:00
		data.put("avg_price", "");								//35。人均价格，大于0的整数
		
		JSONObject base_info = new JSONObject();
		base_info.put("base_info", data);
		JSONObject business = new JSONObject();
		business.put("business", base_info);
		
		String appid = storeMap.get("appid").toString();
		String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
		String url = "http://api.weixin.qq.com/cgi-bin/poi/addpoi?access_token=" + access_token;
		
		Map returnMap = null;
		try {
			returnMap = HttpUtils.doPostString(url, null, null, business.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	
	/**
	 * 删除指定的微信门店
	 * @param json 参数poi_id、appid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Map removeShop(String json) throws Exception{
		Map data = Json.toMap(json);
		
		String poi_id = data.get("poi_id").toString();
		String appid = data.get("appid").toString();
		
		String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
		String url = "https://api.weixin.qq.com/cgi-bin/poi/delpoi?access_token=" + access_token;
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("poi_id", poi_id);				//微信的门店id
		
		Map resultMap = HttpUtils.doPostString(url, null, null, url);
		return resultMap;
	}
	
	
	
	
	
	
	
	
	
}
