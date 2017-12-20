package com.shopSystem;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 交易商品（实体商品、虚拟商品、服务等）
 * @author minqi
 *
 */
@Controller
@RequestMapping("/trust/product")
public class Product {
	
	/**
	 * 查找单个商品详情
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/queryProduct.do")
	public static Map queryProduct(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		String PRODUCT_ID = data.get("PRODUCT_ID").toString();
		
		String sql = "SELECT * FROM C_PRODUCT WHERE PRODUCT_ID = '" + PRODUCT_ID + "'";
		Map productData = MySQLUtils.sqlQueryForMap(DataSource.comm, sql);
		
		HttpUtils.printString(response, productData);
		return productData;
	}
	
	
	
	
}
