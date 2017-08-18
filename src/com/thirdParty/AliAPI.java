package com.thirdParty;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.framework.utils.HttpUtils;
import com.framework.utils.PropertiesReader;


/**
 * 阿里云市场的api
 * 包含发送短信验证码、获取手机归属地、ip归属地等方法合集
 * https://market.aliyun.com/products/56928004/cmapi014125.html?spm=5176.730005.0.0.dau9k7#sku=yuncode812500000
 * @author minqi 2017-07-31 15:19:25
 *
 */
public class AliAPI {

	//配置文件读取对象
	private static PropertiesReader property = PropertiesReader.getInstance();
	
	private static final String ALI_APPCODE = property.getProperty("ALI_APPCODE");
	
	//阿里云平台短信模板编号（必须是已通过审核）
	private static final String SMSTemplateCode1 = property.getProperty("SMSTemplateCode1");
	private static final String SMSTemplateCode2 = property.getProperty("SMSTemplateCode2");
	private static final String SMSTemplateCode3 = property.getProperty("SMSTemplateCode3");
	
	
	
	
	/**
	 * 发送验证码
	 * https://market.aliyun.com/products/57002003/cmapi011900.html?spm=5176.78296.785980.2.jEMY9T
	 * @return 验证码
	 */
	@SuppressWarnings("rawtypes")
	public static String SMSIdentification() {
	    String url = "http://sms.market.alicloudapi.com/singleSendSms";
	    
	    Map<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", "APPCODE " + ALI_APPCODE);
	    
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("ParamString", "%7B%22no%22%3A%22123456%22%7D");
	    querys.put("RecNum", "RecNum");
	    querys.put("SignName", "SignName");
	    querys.put("TemplateCode", SMSTemplateCode1);

	    Map resultMap = null;
	    try {
	    	resultMap = HttpUtils.doGet(url, headers, querys);
	    	System.out.println(resultMap);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return "";
	}

	/**
	 * 获取手机号归属地信息
	 * 
	 *{"status":"0","msg":"ok","result":{"shouji":"13986147769","province":"湖北","city":"武汉","company":"中国移动","cardtype":"GSM","areacode":"027"}}
	 * @param phone
	 */
	@SuppressWarnings("rawtypes")
	public static void getPhoneInfo(String phone) {
	    String url = "http://jshmgsdmfb.market.alicloudapi.com/shouji/query";
	    
	    Map<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", "APPCODE " + ALI_APPCODE);
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("shouji", phone);

	    Map resultMap = null;
	    try {
	    	resultMap = HttpUtils.doGet(url, headers, querys);
	    	System.out.println(resultMap);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	
	/**
	 * 获取IP详细信息
	 */
	@SuppressWarnings("rawtypes")
	public static void getIPInfo() {
	    String url = "http://saip.market.alicloudapi.com/ip";

	    Map<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", "APPCODE " + ALI_APPCODE);			//需保留APPCODE后面的一个英文空格
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("ip", "223.5.5.5");

	    Map resultMap = null;
	    try {
	    	resultMap = HttpUtils.doGet(url, headers, querys);
	    	System.out.println(resultMap);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void getAllInfo(HttpServletRequest request){
		System.out.println("getContextPath()：" + request.getContextPath());
		System.out.println("getRemoteAddr()：" + request.getRemoteAddr());
		System.out.println("User-Agent：" + request.getHeader("User-Agent"));
	}
	
	/**
	 * 获得用户IP（如果是localhost访问，则会返回0:0:0:0:0:0:0:1）
	 * @param request
	 * @return
	 */
	private String getRemoteAddr(HttpServletRequest request){
		/**
		* X-Forwarded-For:简称XFF头，它代表客户端，也就是HTTP的请求端真实的IP
		* 只有在通过了HTTP 代理或者负载均衡服务器时才会添加该项
		* 标准格式如下：X-Forwarded-For: client_ip, proxy1_ip, proxy2_ip
		* 此头是可构造的，因此某些应用中应该对获取到的ip进行验证
		*/
		String ip = request.getHeader("X-Forwarded-For");

		//在多级代理网络中，直接用getHeader("x-forwarded-for")可能获取到的是unknown信息此时需要获取代理代理服务器重新包装的HTTP头信息，
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {		//Apache+WebLogic搭配下出现的头
		    ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {		//Apache+WebLogic搭配下出现的头
		    ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {		//可通过http头伪造，是由代理服务器发送的请求头
		    ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {		//可通过http头伪造，和X-Forwarded-For格式类似，以“,"分隔
		    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getRemoteAddr();
		}
		return ip;
	}
	
}
