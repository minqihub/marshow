package com.framework.database;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.framework.utils.SysLog;

/**
 * sql语句转换器
 * @author minqi 2017-07-29 10:56:57
 *
 */
public class SQLConvertor {

	//定义分隔符
    private static String separator = "?";

    /**
     * 转换sql语句
     * @param sqlTemplate sql语句模板，变量使用?占位
     * @param map
     * @return
     * @throws Exception
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public static String format(String sqlTemplate, Map map){
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String key = it.next().toString();
            Object value = map.get(key);
            
            if(value.getClass().getName().equals("java.lang.String")){
            	sqlTemplate = sqlTemplate.replace(separator+key, "'"+value.toString()+"'");
            }else{
            	sqlTemplate = sqlTemplate.replace(separator+key, value.toString());
            } 
            
        }
        if(sqlTemplate.contains(separator)){
        	throw new RuntimeException("SQL语句转化错误：map中缺少数据" + sqlTemplate);
        }
        SysLog.log(SQLConvertor.class, "转化后的sql语句：" + sqlTemplate);
        return sqlTemplate;
    }

	
	public static void main(String[] args) {
		SQLConvertor convertor = new SQLConvertor();
		
		
		String sql = "INSERT INTO w_user ('userName','userKey')VALUES(?userName,?userKey)";
		
		Map map = new HashMap();
		map.put("userName", "小王");
//		map.put("userKey", "");
		
		
		convertor.format(sql, map);
	}
	
}

