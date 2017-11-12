package com.shopSystem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.database.DBHandler;
import com.framework.database.DataSource;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 地址类
 * @author minqi 2017-11-06 21:23:17
 *
 */
@Controller
@RequestMapping("/trust/wXShop")
public class Address extends DBHandler{
	
	public String province;			//省
	public String city;				//市
	public String district;			//区
	public String detail;			//详细地址
	
	/**
	 * 获取默认地址
	 * @param response
	 * @return
	 */
	@RequestMapping("/getDefaultAddress.do")
	public List getDefaultAddress(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		
		
		return null;
	}
	
	
	/**
	 * 获取省份列表
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getProvince.do")
	public List getProvince(HttpServletResponse response){
		String sql = "SELECT * FROM S_Address WHERE Level LIKE 1";
		List list = sqlQueryForList(DataSource.comm, sql);
		
		HttpUtils.printString(response, list);
		return list;
	}
	
	/**
	 * 获取城市列表
	 * @param json 参数：ParentID上级省份的ID，可空
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getCity.do")
	public List getCity(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		String sql = "";
		if(!DataUtils.isNull(data.get("ParentID"))){	//有上级省份的id
			sql = "SELECT * FROM S_Address WHERE Level LIKE 2 AND ParentID LIKE " + data.get("ParentID");
		}else{
			sql = "SELECT * FROM S_Address WHERE Level LIKE 2";
		}
		List list = sqlQueryForList(DataSource.comm, sql);
		
		HttpUtils.printString(response, list);
		return list;
	}
	
	/**
	 * 获取区域列表
	 * @param json 参数：ParentID上级城市的ID
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getDistrict.do")
	public List getDistrict(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		String sql = "";
		if(!DataUtils.isNull(data.get("ParentID"))){	//有上级城市的id
			sql = "SELECT * FROM S_Address WHERE Level LIKE 3 AND ParentID LIKE " + data.get("ParentID");
		}else{
			sql = "SELECT * FROM S_Address WHERE Level LIKE 3";
		}
		List list = sqlQueryForList(DataSource.comm, sql);
		
		HttpUtils.printString(response, list);
		return list;
	}
	
	
	
	
	
	
	
}
