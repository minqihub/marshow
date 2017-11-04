package com.thirdParty.weChat.wxInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.framework.database.DBHandler;
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

	//available_state：门店是否可用状态。1 表示系统错误、2 表示审核中、3 审核通过、4 审核驳回。1、2、4 状态不返回poi_id
	private static final String[] available_state = {"","系统错误","审核中","审核通过","审核驳回"};
	
	//update_status：扩展字段是否正在更新中。 0 表示扩展字段没有在更新中或更新已生效，可以再次更新；1 表示扩展字段正在更新中，尚未生效，不允许再次更新
	private static final String[] update_status = {"扩展字段没有在更新中或更新已生效，可以再次更新","扩展字段正在更新中，尚未生效，不允许再次更新"};
	
	
	
	/**
	 * 添加微信门店
	 * 如果公众号连接了服务器，门店审核结果会进行事件推送
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map addShop(Map storeMap){
		Map returnMap = new HashMap();
		try {
			JSONObject data = new JSONObject();
			
			//门店基础信息字段
			data.put("sid", storeMap.get("sid").toString());						//商户自己的门店id
			data.put("business_name", storeMap.get("business_name").toString());	//门店名称（仅为商户名，如：国美、麦当劳，不应包含地区、地址、分店名等信息，错误示例：北京国美）15个汉字或30个英文字符内
			data.put("branch_name", storeMap.get("branch_name").toString());		//分店名称（不应包含地区信息，不应与门店名有重复，错误示例：北京王府井店）不超过10个字，不能含有括号和特殊字符
			data.put("province", storeMap.get("province").toString());				//不超过10个字
			data.put("city", storeMap.get("city").toString());						//不超过30个字
			data.put("district", storeMap.get("district").toString());				//门店所在地区，不超过10个字
			data.put("address", storeMap.get("address").toString());				//门店所在的详细街道地址（不要填写省市信息）：不超过80个字
			data.put("telephone", storeMap.get("telephone").toString());			//不超53个字符（区号分机号用-连接）
			data.put("categories", storeMap.get("categories").toString());			//门店的类型["美食,小吃快餐"]["购物,数码家电"]
			data.put("offset_type", 1);												//1为火星坐标2为sogou经纬度3为百度经纬度4为mapbar经纬度5为GPS坐标6为sogou墨卡托坐标
			data.put("longitude", storeMap.get("longitude").toString());			//经度115.32375
			data.put("latitude", storeMap.get("latitude").toString());				//纬度25.097486

			//准备图片信息 photo_list:["http://www........","http://www........"]
			List photo_list = new ArrayList();
			JSONArray imgList = Json.toJA(storeMap.get("photo_list"));
			for (int i = 0; i < imgList.size(); i++) {
				String imgName = (i+1) + ".jpg";		//图片名称
				String imgUrl = imgList.get(i).toString();
				JSONObject photo_url = new JSONObject();
				String wxImg = WXTools.getWeixinImg(imgName, imgUrl, storeMap.get("appid").toString());
				if(!wxImg.equals("")){
					photo_url.put("photo_url", wxImg);
					photo_list.add(photo_url);
				}
			}
			
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
			
			String access_token = WXTools.getWeChatToken(storeMap.get("appid").toString()).get("access_token").toString();
			String url = "http://api.weixin.qq.com/cgi-bin/poi/addpoi?access_token=" + access_token;
			returnMap = HttpUtils.doPostStringForMap(url, null, null, business.toString());
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
			}else{
				throw new Exception();
			}
			
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必填字段");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请求微信接口失败");
		}
		return returnMap;
	}
	
	/**
	 * 修改指定门店
	 * 商户可以通过该接口，修改门店的服务信息，包括：sid、图片列表、营业时间、推荐、特色服务、简介、人均价格、电话8个字段
	 * （名称、坐标、地址等不可修改）修改后需要人工审核
	 * @param storeMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map updateShop(Map storeMap){
		Map returnMap = new HashMap();
		try {
			JSONObject base_info = new JSONObject();
			
			base_info.put("poi_id", storeMap.get("poi_id").toString());

			//以下8个字段可空，有则覆盖更新。其中photo_list为全量覆盖更新
			base_info.put("sid", storeMap.get("sid"));
			base_info.put("telephone", storeMap.get("telephone"));
			base_info.put("photo_list", storeMap.get("photo_list"));
			base_info.put("recommend", storeMap.get("recommend"));
			base_info.put("special", storeMap.get("special"));
			base_info.put("introduction", storeMap.get("introduction"));
			base_info.put("open_time", storeMap.get("open_time"));
			base_info.put("avg_price", storeMap.get("avg_price"));

			JSONObject business = new JSONObject();
			business.put("base_info", base_info);
			
			
			String appid = storeMap.get("appid").toString();
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/cgi-bin/poi/updatepoi?access_token=" + access_token;
		
			returnMap = HttpUtils.doPostStringForMap(url, null, null, business.toString());
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
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
	 * 查询指定门店
	 * 在查询时，update_status 字段为1，表明在5 个工作日内曾用update 接口修改过门店扩展字段，
	 * 该扩展字段为最新的修改字段，尚未经过审核采纳，因此不是最终结果。最终结果会在5 个工作日内，
	 * 最终确认是否采纳，并前端生效（但该扩展字段的采纳过程不影响门店的可用性，即available_state仍为审核通过状态）
	 * 注：修改扩展字段将会推送审核，但不会影响该门店的生效可用状态。
	 * @param storeMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map findShop(Map storeMap){
		Map returnMap = new HashMap();
		try {
			JSONObject poi_id = new JSONObject();
			poi_id.put("poi_id", storeMap.get("poi_id").toString());
		
			String appid = storeMap.get("appid").toString();
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "http://api.weixin.qq.com/cgi-bin/poi/getpoi?access_token=" + access_token;
		
			returnMap = HttpUtils.doPostStringForMap(url, null, null, poi_id.toString());
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
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
	 * 查询所有门店列表
	 * @param storeMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map findShopList(Map storeMap){
		Map returnMap = new HashMap();
		try {
			JSONObject range = new JSONObject();
			range.put("begin", 0);							//从第一条开始查询
			range.put("limit", 50);							//返回数据条数，最大允许50，默认为20
		
			String appid = storeMap.get("appid").toString();
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/cgi-bin/poi/getpoilist?access_token=" + access_token;
		
			returnMap = HttpUtils.doPostStringForMap(url, null, null, range.toString());
			
			if(returnMap.get("errcode").toString().equals("0")){
				returnMap.put("MSGID", "S");
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
	 * 删除指定微信门店
	 * @param json 参数poi_id、appid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map delShop(Map data){
		Map returnMap = new HashMap();
		try {
			String poi_id = data.get("poi_id").toString();
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "https://api.weixin.qq.com/cgi-bin/poi/delpoi?access_token=" + access_token;
			
			JSONObject sendJson = new JSONObject();
			sendJson.put("poi_id", poi_id);				//微信的门店id
			
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
	 * 门店类目表
	 * 类目名称接口是为商户提供自己门店类型信息的接口。门店类目定位的越规范，能够精准的吸引更多用户，提高曝光率
	 * @param json 参数appid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getCategoryList.do")
	public static Map getCategoryList(Map data, HttpServletResponse response){
		Map returnMap = new HashMap();
		try {
			String appid = data.get("appid").toString();
			
			String access_token = WXTools.getWeChatToken(appid).get("access_token").toString();
			String url = "http://api.weixin.qq.com/cgi-bin/poi/getwxcategory?access_token=" + access_token;
			
			returnMap = HttpUtils.doGet(url, null, null);
//			{"category_list":["美食,江浙菜,上海菜","美食,江浙菜,淮扬菜","美食,江浙菜,浙江菜","美食,江浙菜,南京菜 ","美食,江浙菜,苏帮菜…"]}
			returnMap.put("MSGID", "S");
			
		} catch (NullPointerException e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "缺少必要参数");
		} catch (Exception e){
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "微信接口调用失败");
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	
	
	
	
	
}
