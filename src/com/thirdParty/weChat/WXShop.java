package com.thirdParty.weChat;

import java.awt.image.DataBufferUShort;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.framework.database.DBHandler;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 微信门店
 * @author minqi 2017-07-31 17:25:48
 *
 */
@Controller
@RequestMapping("/trust/wXShop")
public class WXShop extends DBHandler {
	
	private static String[] available_state = {"","系统错误","审核中","审核通过","审核驳回"};
	//available_state：门店是否可用状态。1 表示系统错误、2 表示审核中、3 审核通过、4 审核驳回。1、2、4 状态不返回poi_id
	
	private static String[] update_status = {"扩展字段没有在更新中或更新已生效，可以再次更新","扩展字段正在更新中，尚未生效，不允许再次更新"};
	//update_status：扩展字段是否正在更新中。 0 表示扩展字段没有在更新中或更新已生效，可以再次更新；1 表示扩展字段正在更新中，尚未生效，不允许再次更新

	
	
	/**
	 * 创建微信门店（此为轮询方法：将数据库已存在的门店数据提交到微信审核）
	 * 成功创建后，微信会返回poi_id，即微信的门店id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/insertShop.do")
	public void insertShop() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinShop/insertShop.do
		//准备待提交的门店数据
		Map queryMap = new HashMap();
		queryMap.put("state.key", "50");	//多渠审核通过
		queryMap.put("poi_id", null);		//未提交过微信审核的门店数据
		queryMap.put("yxbj", 1);
		List storeList = find("W_Store", queryMap, null, null);
		
		if(!storeList.isEmpty()){
			//获取接口路径
			String access_token = WXTools.getAccessToken();
			String url = "http://api.weixin.qq.com/cgi-bin/poi/addpoi?access_token=" + access_token;
			
			for (int i = 0; i < storeList.size(); i++) {
				//获取数据库的门店信息
				Map storeMap = Json.toMap(storeList.get(i).toString());

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
//					String imgName = row.get("FILE_DESC").toString();
					String imgName = (j+1)+".jpg";		//图片名称
					String imgUrl = row.get("FILE_URL").toString();
					JSONObject photo_url = new JSONObject();
					photo_url.put("photo_url", WXTools.getWeixinImg(imgName, imgUrl));
					photo_list.add(photo_url);
				}
				
				//准备门店类型
				List categoriesList = new ArrayList();
				categoriesList.add("购物,数码家电");
				
				//准备接口字段
				JSONObject data = new JSONObject();
				//门店基础信息字段
				data.put("sid", storeMap.get("storeId").toString());				//商户自己的id
				//最终门店名和分店名将以“business_name（branch_name）”的形式拼合显示
				data.put("business_name", storeMap.get("companyName"));	//门店名称（仅为商户名，如：国美、麦当劳，不应包含地区、地址、分店名等信息，错误示例：北京国美）15个汉字或30个英文字符内
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
				Map resultMap = sendJsonPost(url, business.toString());
				
				//若新增成功，在W_Store中对应数据中插入poi_id
				if(resultMap.get("errcode").toString().equals("0")){		
					Map queryStoreMap = new HashMap();
					queryStoreMap.put("storeId", storeMap.get("storeId"));
					Map updateMap = new HashMap();
					updateMap.put("poi_id", resultMap.get("poi_id"));
					update("W_Store", queryStoreMap, updateMap);
				}
			}
		}

	}
	
	/**
	 * 查看微信门店列表（此为轮询方法：用于更新微信门店的审核状态）
	 * 因为没有新增修改时没有获取微信的回调数据，所以只能通过查询来获取门店的状态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/queryShopList.do")
	public void queryShopList() throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinShop/queryShopList.do
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/poi/getpoilist?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("begin", 0);							//从第一条开始查询
		json.put("limit", 50);							//返回数据条数，最大允许50，默认为20
		Map resultMap = sendJsonPost(url, json.toString());

		//更新W_Store中门店的审核状态、可更新状态
		if(resultMap.get("errcode").toString().equals("0")){
			System.out.println("门店数量为："+resultMap.get("total_count"));
			JSONArray business_list = Json.toJA(resultMap.get("business_list"));
			for (int i = 0; i < business_list.size(); i++) {
				JSONObject row = Json.toJO(business_list.get(i));
				JSONObject base_info = Json.toJO(row.get("base_info"));
				
				Map queryMap = new HashMap();
				queryMap.put("storeId", Integer.parseInt(base_info.get("sid").toString()));		//商家自己的门店id
				
				Map updateMap = new HashMap();
				
				//审核状态
				JSONObject json1 = new JSONObject();
				int key1 = base_info.getInteger("available_state");
				json1.put("key", key1);
				json1.put("value", available_state[key1]);
				updateMap.put("available_state", json1);
				
				//可更新状态
				JSONObject json2 = new JSONObject();
				int key2 = base_info.getInteger("update_status");
				json2.put("key", key2);
				json2.put("value", update_status[key2]);
				updateMap.put("update_status", json2);
				
				update("W_Store", queryMap, updateMap);
			}
		}
	}
	
	/**
	 * 查看微信门店详情
	 * @param XmlData 参数：storeId
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/queryShopInfo.do")
	public Map queryShopInfo(String XmlData, HttpServletRequest request) throws Exception{
		Map map = Json.toMap(XmlData);
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "http://api.weixin.qq.com/cgi-bin/poi/getpoi?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("poi_id", map.get("poi_id").toString());		//微信的门店id
		
		Map resultMap = sendJsonPost(url, json.toString());
		
		//available_state：门店是否可用状态。1 表示系统错误、2 表示审核中、3 审核通过、4 审核驳回。1、2、4 状态不返回poi_id
		//update_status：扩展字段是否正在更新中。1 表示扩展字段正在更新中，尚未生效，不允许再次更新； 0 表示扩展字段没有在更新中或更新已生效，可以再次更新
		return resultMap;
	}
	
	/**
	 * 修改微信门店服务信息
	 * @param XmlData
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/updateShop.do")
	public Map updateShop(String XmlData, HttpServletRequest request) throws Exception{
		Map map = Json.toMap(XmlData);
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/poi/delpoi?access_token=" + access_token;
		
		//准备接口图片信息
		List photo_list = new ArrayList();
		JSONArray imgList = Json.toJA(map.get("storeImg"));
		for (int i = 0; i < imgList.size(); i++) {
			Map row = Json.toMap(imgList.get(i).toString());
			String imgName = row.get("FILE_DESC").toString();
			String imgUrl = row.get("FILE_URL").toString();
			JSONObject photo_url = new JSONObject();
			photo_url.put("photo_url", WXTools.getWeixinImg(imgName, imgUrl));
			photo_list.add(photo_url);
		}
		
		JSONObject data = new JSONObject();
		//门店基础信息字段
		data.put("poi_id ", map.get("poi_id"));											//微信的门店id
		data.put("sid", map.get("storeId"));											//商户自己的id，用于后续审核通过收到poi_id 的通知
		data.put("telephone", map.get("mobile"));										//不超53个字符（不可以出现文字）
		
		//门店服务信息字段（均为非必填）
		data.put("photo_list", photo_list);												//全量覆盖修改
		data.put("recommend", map.get("recommend"));									//推荐品，不超过200字。
		data.put("special", map.get("special"));										//特色服务，不超过200字。免费wifi，外卖服务
		data.put("introduction", map.get("introduce"));									//商户简介，不超过300字。
		data.put("open_time", map.get("openStartTime")+"-"+map.get("openEndTime"));		//8:00-20:00
		data.put("avg_price", map.get("avg_price"));									//35。人均价格，大于0的整数

		JSONObject base_info = new JSONObject();
		base_info.put("base_info", data);

		JSONObject json = new JSONObject();
		json.put("business", base_info);
		
		Map resultMap = sendJsonPost(url, json.toString());
		return resultMap;
	}
	
	/**
	 * 删除微信门店
	 * @param XmlData
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/removeShop.do")
	public Map removeShop(String XmlData, HttpServletRequest request) throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinShop/removeShop.do
		Map map = Json.toMap(XmlData);
//		map.put("poi_id", "469162681");
		
		//获取access_token
		String access_token = WXTools.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/poi/delpoi?access_token=" + access_token;
		
		JSONObject json = new JSONObject();
		json.put("poi_id", map.get("poi_id"));		//微信的门店id
		
		Map resultMap = sendJsonPost(url, json.toString());
		return resultMap;
	}
	
	//post请求方法，此方法请求数据的格式是raw，而不是form-data
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Map sendJsonPost(String url, String json) throws ParseException, IOException{
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    StringEntity postingString = new StringEntity(json,"utf-8");		//json传递  
	    post.setEntity(postingString);
	    
	    post.addHeader("Content-type","application/json; charset=utf-8");
	    post.setHeader("Accept", "application/json");
	    HttpResponse response = httpClient.execute(post);
	    
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
	    System.out.println("微信接口请求返回值："+result2);
	    return Json.toMap(result2);
	}
	

	
}
