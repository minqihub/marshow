package com.thirdParty.kdNiao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.framework.utils.PropertiesReader;

/**
 * 快递鸟
 * http://www.kdniao.com/file/%E5%BF%AB%E9%80%92%E9%B8%9F%E6%8E%A5%E5%8F%A3%E6%8A%80%E6%9C%AF%E6%96%87%E6%A1%A3v5.0.pdf
 * @author minqi
 *
 */
public class KDN {
	
	//配置文件读取对象
	private static PropertiesReader property = PropertiesReader.getInstance();
	
	private final String EBusinessID = property.getProperty("KDN_EBusinessID");		//电商ID
	private final String AppKey = property.getProperty("KDN_AppKey");				//电商加密私钥
	private final String ReqURL = "http://testapi.kdniao.cc:8081/api/dist";			//测试请求url
//	private static final String ReqURL = "http://api.kdniao.cc/api/dist";			//正式请求url
	
	
	
	/**
	 * 订阅(增值版)接口
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map tracesSubscribe(String json, HttpServletResponse response){
		Map map = Json.toMap(json);
		
		Map returnMap = new HashMap();
		try {
			JSONObject requestData = new JSONObject();
			
			requestData.put("CallBack", "");		//自定义回调信息
			requestData.put("OrderCode", "");		//订单编号
			requestData.put("Remark", "");		//备注
			requestData.put("IsSendMessage", "");		//是否订阅短信：0-不需要；1-需要
			
			requestData.put("ShipperCode", "");		//快递公司编码
			requestData.put("LogisticCode", "");		//快递单号
			
			JSONObject Receiver = new JSONObject();
			Receiver.put("Name", "");		//收件人姓名
			Receiver.put("Tel", "");		//座机/手机二选一
			Receiver.put("Mobile", "");		//座机/手机二选一
			Receiver.put("ProvinceName", "");		//收件省(如广东省，不要缺少	“省”，如果是直辖市，请直接传北京、上海等
			Receiver.put("CityName", "");		//收件市(如深圳市，不要缺少“市”)
			Receiver.put("ExpAreaName", "");		//收件区/县(如福田区，不要缺少“区”或“县”)
			Receiver.put("Address", "");		//收件人详细地址
			Receiver.put("PostCode", "");		//收件地邮编(ShipperCode为 EMS、YZPY 时必填)
			requestData.put("Receiver", Receiver);

			JSONObject Sender = new JSONObject();
			Sender.put("Name", "");		//收件人姓名
			Sender.put("Tel", "");		//座机/手机二选一
			Sender.put("Mobile", "");		//座机/手机二选一
			Sender.put("ProvinceName", "");		//收件省(如广东省，不要缺少	“省”，如果是直辖市，请直接传北京、上海等
			Sender.put("CityName", "");		//收件市(如深圳市，不要缺少“市”)
			Sender.put("ExpAreaName", "");		//收件区/县(如福田区，不要缺少“区”或“县”)
			Sender.put("Address", "");		//收件人详细地址
			Sender.put("PostCode", "");		//收件地邮编(ShipperCode为 EMS、YZPY 时必填)
			requestData.put("Sender", Sender);
			
			Map<String, String> sendData = new HashMap<String, String>();
			sendData.put("RequestData", URLEncoder.encode(requestData.toString(), "UTF-8"));
			sendData.put("EBusinessID", EBusinessID);
			sendData.put("RequestType", "8008");
			String dataSign = encrypt(requestData.toString(), AppKey, "UTF-8");
			sendData.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
			sendData.put("DataType", "2");
			
			returnMap = HttpUtils.doPostStringForMap(ReqURL, null, null, sendData.toString());

			returnMap.put("MSGID", "S");
			returnMap.put("MESSAGE", "物流信息订阅成功");
			
		} catch (NullPointerException e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "空指针异常：" + e);
		} catch (Exception e) {
			returnMap.put("MSGID", "E");
			returnMap.put("MESSAGE", "其他异常：" + e);
		}
		return returnMap;
	}
	
    private static char[] base64EncodeChars = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
            'w', 'x', 'y', 'z', '0', '1', '2', '3', 
            '4', '5', '6', '7', '8', '9', '+', '/' }; 
    	
    public static String base64Encode(byte[] data) { 
        StringBuffer sb = new StringBuffer(); 
        int len = data.length; 
        int i = 0; 
        int b1, b2, b3; 
        while (i < len) { 
            b1 = data[i++] & 0xff; 
            if (i == len) { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]); 
                sb.append("=="); 
                break; 
            } 
            b2 = data[i++] & 0xff; 
            if (i == len) { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]); 
                sb.append("="); 
                break; 
            } 
            b3 = data[i++] & 0xff; 
            sb.append(base64EncodeChars[b1 >>> 2]); 
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]); 
            sb.append(base64EncodeChars[b3 & 0x3f]); 
        } 
        return sb.toString(); 
    }
	
	/**
     * MD5加密
     * @param str 内容       
     * @param charset 编码方式
	 * @throws Exception 
     */
	@SuppressWarnings("unused")
	private String MD5(String str, String charset) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(str.getBytes(charset));
	    byte[] result = md.digest();
	    StringBuffer sb = new StringBuffer(32);
	    for (int i = 0; i < result.length; i++) {
	        int val = result[i] & 0xff;
	        if (val <= 0xf) {
	            sb.append("0");
	        }
	        sb.append(Integer.toHexString(val));
	    }
	    return sb.toString().toLowerCase();
	}
	
	/**
     * base64编码
     * @param str 内容       
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException 
     */
	private String base64(String str, String charset) throws UnsupportedEncodingException{
		String encoded = base64Encode(str.getBytes(charset));
		return encoded;    
	}
	
	/**
     * 电商Sign签名生成
     * @param content 内容   
     * @param keyValue Appkey  
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
     */
	@SuppressWarnings("unused")
	private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception{
		if (keyValue != null){
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}
	
}
