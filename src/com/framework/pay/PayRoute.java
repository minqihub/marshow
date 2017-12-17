package com.framework.pay;

import java.util.HashMap;
import java.util.Map;

import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.database.SQLConvertor;
import com.framework.utils.Json;

/**
 * 支付配置与路由
 * @author minqi 2017-10-27 22:01:28
 *
 */
public class PayRoute {

	
	//支付方式PAYWAY：cashPay,weChatPay,aliPay,bankCardPay
	
	
	
	
	/**
	 * 获取支付配置
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getPayConfig(String json) {
		Map data = Json.toMap(json);
		
		Map queryMap = new HashMap();
		queryMap.put("companyId", data.get("companyId").toString());
		queryMap.put("payType", data.get("payType").toString());
		
		String sqlTemplate = "select * from";
		
		String sql = SQLConvertor.format(sqlTemplate, queryMap);
		
		Map returnMap = MySQLUtils.sqlQueryForMap(DataSource.comm, sql);
		return returnMap;
	}
	
	
	
}
