package com.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON 工具类
 * 阿里巴巴 fastJson 简单封装
 * fastJson 版本要求：1.2.20 或以上
 * 
 * @author XiongJian
 * @version 1.0
 */
public class Json {

	/**
	 * 日志记录对象
	 */
//	private static final Log logger = LogFactory.getLog(Json.class);

	/**
	 * 日期(时间)值转换字符串格式化样式
	 */
	private static String JSON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	

	/**
	 * Java 对象转换为 JSON 字符串
	 * 
	 * @param object
	 *            - Object
	 * @return JSON 字符串
	 */
	public static String toJson(Object object) {
		return toJson(object, false, false);
	}

	/**
	 * Java 对象转换为 JSON 字符串
	 * 
	 * @param object
	 *            - Object
	 * @param formatDate
	 *            - boolean
	 * @param removeNullValue
	 *            - boolean
	 * @return JSON 字符串
	 */
	public static String toJson(Object object, boolean formatDate,
			boolean removeNullValue) {
		if (object == null || object.equals("")) {
			return "{}";
		}
		if (object instanceof String) {
			return (String) object;
		}
		if (!formatDate) {
			if (!removeNullValue) {
				return JSON.toJSONString(object,
						SerializerFeature.WriteMapNullValue,
						SerializerFeature.DisableCircularReferenceDetect);
			} else {
				return JSON.toJSONString(object,
						SerializerFeature.DisableCircularReferenceDetect);
			}
		} else {
			if (!removeNullValue) {
				return JSON.toJSONStringWithDateFormat(object,
						JSON_DATETIME_FORMAT,
						SerializerFeature.WriteDateUseDateFormat,
						SerializerFeature.WriteMapNullValue,
						SerializerFeature.DisableCircularReferenceDetect);
			} else {
				return JSON.toJSONStringWithDateFormat(object,
						JSON_DATETIME_FORMAT,
						SerializerFeature.WriteDateUseDateFormat,
						SerializerFeature.DisableCircularReferenceDetect);
			}
		}
	}

	/**
	 * 构造空 JSONObject 对象
	 * 
	 * @return JSONObject
	 */
	public static JSONObject JO() {
		return new JSONObject(true);
	}

	/**
	 * 构造空 JSONArray 对象
	 * 
	 * @return JSONArray
	 */
	public static JSONArray JA() {
		return new JSONArray();
	}

	/**
	 * 简单判断字符串是否为 JSON 格式 (JSONObject or JSONArray)
	 * 
	 * @param json
	 *            - String
	 * @return boolean
	 */
	public static boolean isJSON(String json) {
		return isJO(json) || isJA(json);
	}

	/**
	 * 简单判断字符串是否为 JSONObject 格式
	 * 
	 * @param json
	 *            - String
	 * @return boolean
	 */
	public static boolean isJO(String json) {
		if (json == null || json.equals("")) {
			return false;
		}
		return json.startsWith("{") && json.endsWith("}");
	}

	/**
	 * 简单判断字符串是否为 JSONArray 格式
	 * 
	 * @param json
	 *            - String
	 * @return boolean
	 */
	public static boolean isJA(String json) {
		if (json == null || json.equals("")) {
			return false;
		}
		return json.startsWith("[") && json.endsWith("]");
	}

	/**
	 * Java 对象转换为 JSONObject
	 * 
	 * @param json
	 *            - String
	 * @return JSONObject
	 */
	public static JSONObject toJO(String json) {
		if (json == null || json.equals("")) {
			return new JSONObject(true);
		}
		if (!(json.startsWith("{") && json.endsWith("}"))) {
			throw new RuntimeException("NOT JSONObject string");
		}
		LinkedHashMap<String, Object> map = JSON.parseObject(json,
				new TypeReference<LinkedHashMap<String, Object>>() {
				}, Feature.OrderedField);
		if (map != null) {
			return new JSONObject(map);
		} else {
			return new JSONObject(true);
		}
	}

	/**
	 * Java 对象转换为 JSONArray
	 * 
	 * @param json
	 *            - String
	 * @return JSONArray
	 */
	public static JSONArray toJA(String json) {
		if (json == null || json.equals("")) {
			return new JSONArray();
		}
		if (!(json.startsWith("[") && json.endsWith("]"))) {
			throw new RuntimeException("NOT JSONArray string");
		}
		return toJsonObject(json);
	}

	/**
	 * JSON 字符串转换为 JSONObject 或者 JSONArray
	 * 
	 * @param json
	 *            - String
	 * @return JSONObject 或者 JSONArray
	 */
	public static <T> T toJsonObject(String json) {
		return JSON.parseObject(json, new TypeReference<T>() {
		});
	}

	/**
	 * Java 对象转换为 JSONObject
	 * 
	 * @param object
	 *            - Object
	 * @return JSONObject
	 */
	public static JSONObject toJO(Object object) {
		if (object == null || object.equals("")) {
			return new JSONObject();
		}
		if (object instanceof JSONObject) {
			return (JSONObject) object;
		}
		return toJsonObject(object);
	}

	/**
	 * Java 对象转换为 JSONArray
	 * 
	 * @param object
	 *            - Object
	 * @return JSONArray
	 */
	public static JSONArray toJA(Object object) {
		if (object == null || object.equals("")) {
			return new JSONArray();
		}
		if (object instanceof JSONArray) {
			return (JSONArray) object;
		}
		Object obj = toJsonObject(object);
		if (!(obj instanceof JSONArray)) {
//			obj = new JSONArray().fluentAdd(object);
			obj = new JSONArray().parseArray(object.toString());
		}
		return (JSONArray) obj;
	}

	/**
	 * Java 对象转换为 JSONObject 或者 JSONArray
	 * 
	 * @param object
	 *            - Object
	 * @return JSONObject 或者 JSONArray
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toJsonObject(Object object) {
		if (object instanceof String) {
			return JSON.parseObject((String) object, new TypeReference<T>() {
			});
		} else {
			return (T) JSON.toJSON(object);
		}
	}

	/**
	 * JSON 字符串转换为 JavaBean
	 * 
	 * @param json
	 *            - String
	 * @param clazz
	 *            - Class
	 * @return Java Object
	 */
	public static <T> T toBean(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * JSON 字符串转换为 HashMap
	 * 
	 * @param json
	 *            - String
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(String json) {
		if (json == null || json.equals("")) {
			return new HashMap();
		}
		return JSON.parseObject(json, HashMap.class);
	}

	/**
	 * JSON 字符串转换为 LinkedHashMap
	 * 
	 * @param json
	 *            - String
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map toLinkedMap(String json) {
		if (json == null || json.equals("")) {
			return new LinkedHashMap();
		}
		return JSON.parseObject(json, LinkedHashMap.class, Feature.OrderedField);
	}

	/**
	 * JSON 字符串转换为 List
	 * 
	 * @param json
	 *            - String
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public static List toList(String json) {
		if (json == null || json.equals("")) {
			return new ArrayList();
		}
		return JSON.parseObject(json, List.class);
	}

	/**
	 * JSON 字符串转换为 List{@code <Map>}
	 * 
	 * @param json
	 *            - String
	 * @return List{@code <Map>}
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> toListMap(String json) {
		if (json == null || json.equals("")) {
			return new ArrayList<Map>();
		}
		return JSON.parseArray(json, Map.class);
	}
}
