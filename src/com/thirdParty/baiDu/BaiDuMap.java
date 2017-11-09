package com.thirdParty.baiDu;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 百度地图
 * @author minqi 2017-11-09 10:39:22
 *
 */
public class BaiDuMap {
	
	private static final String ak = "";
	
	
	/**
	 * 转换坐标（默认由GCJ02转为百度坐标系）
	 * http://lbsyun.baidu.com/index.php?title=webapi/guide/changeposition
	 * @param json 参数：longitude、latitude
	 * @return 通过MSGID、flag判断
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map transCoordinate(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			/**
			 * 需转换的源坐标，多组坐标以;分隔
			 * 114.21892734521,29.575429778924（经度，纬度）
			 */
			String coords = map.get("longitude").toString() + "," + map.get("latitude").toString();
			
			/**
			 * 	源坐标类型：
			 * 	1：GPS设备获取的角度坐标，wgs84坐标;
			 *	2：GPS获取的米制坐标、sogou地图所用坐标;
			 *	3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局（gcj02）坐标;
			 *	4：3中列表地图坐标对应的米制坐标;
			 *	5：百度地图采用的经纬度坐标;
			 *	6：百度地图采用的米制坐标;
			 *	7：mapbar地图坐标;
			 *	8：51地图坐标
			 */
			String from = DataUtils.isNull(map.get("from")) ? "3" : map.get("from").toString();
			
			/**
			 * 目标坐标类型：
			 * 5：bd09ll(百度经纬度坐标)
			 * 6：bd09mc(百度米制经纬度坐标)
			 */
			String to = DataUtils.isNull(map.get("to")) ? "5" : map.get("to").toString();

			String url = "http://api.map.baidu.com/geoconv/v1/?coords=" + coords + "&from=" + from + "&to=" + to + "&ak=" + this.ak;
			returnMap = HttpUtils.doGet(url, null, null);
			
			if(returnMap.get("status").toString().equals("0")){
				returnMap.put("flag", "1");
			}else{
				returnMap.put("flag", "0");
			}
			returnMap.put("status", "S");
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "必填字段未填写");
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "请百度服务器求失败");
		}
		HttpUtils.printString(response, returnMap);
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
