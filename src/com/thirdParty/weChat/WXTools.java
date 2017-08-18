package com.thirdParty.weChat;

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

import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.framework.utils.PropertiesReader;

/**
 * 微信工具类
 * @author minqi 2017-08-12 10:28:25
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
 */
public class WXTools {

	private static PropertiesReader property = PropertiesReader.getInstance();
	
	//初始化微信配置
	private static final String WECHAT_APPID = property.getProperty("WECHAT_APPID");
	private static final String WECHAT_APPSECRET = property.getProperty("WECHAT_APPSECRET");

	/**
	 * 绑定微信账号
	 * @return
	 */
	public static boolean bindWeChat(String key, String opendid){
	
		return true;
	}
	
	
	
	
	/**
	 * 获取access_token，7200秒的有效期，超过7000秒就重新获取
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getAccessToken(){
		String access_token = "";
		
		//TODO 首先去数据库查询已有的access_token是否过期
		Map tokenMap = new HashMap();
		
		if(System.currentTimeMillis() / 1000 - Integer.parseInt(tokenMap.get("timestamp").toString()) > 7000){
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WECHAT_APPID + "&secret=" + WECHAT_APPSECRET;
			
			try {
				Map resultMap  = HttpUtils.doGet(url, null, null);
				access_token = resultMap.get("access_token").toString();
				
				//TODO 更新数据库中的access_token字段
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
			
		}else{
			access_token = tokenMap.get("access_token").toString();
		}
		return access_token;
	}

	/**
	 * 获取微信用户openid
	 * @param code 通过页面获取的微信用户的code
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getOpenId(String code){
	   	String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WECHAT_APPID + "&secret=" + WECHAT_APPSECRET + "&code=" + code + "&grant_type=authorization_code";
	   	Map resultMap = null;
	   	try {
			resultMap = HttpUtils.doGet(url, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap.get("openid").toString();
	}
	
	/**
	 * 获取微信用户详细信息
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map getWeChatUserInfo(String openId){
		String access_token = getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + openId + "&lang=zh_CN";
		
		Map resultMap = null;
		try {
			resultMap = HttpUtils.doGet(url, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 上传微信图片，返回微信服务器路径（微信图片大小限制1MB）
	 * 用于微信门店：图片内容不允许与门店不相关，不允许为二维码、员工合照（或模特肖像）、营业执照、无门店正门的街景、地图截图、公交地铁站牌、菜单截图等
	 * @param XmlData 参数：图片名称、图片路径
	 * @return 返回微信图片路径
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/getWeixinImg.do")
	public static String getWeixinImg(String imgName, String imgUrl) throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinShop/getWeixinImg.do
 
		//获取access_token
		String access_token = getAccessToken();
        URL urlObj = new URL("https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + access_token); 
        
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();  
        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式  
        con.setDoInput(true);  
        con.setDoOutput(true);  
        con.setUseCaches(false); // post方式不能使用缓存  
        // 设置请求头信息  
        con.setRequestProperty("Connection", "Keep-Alive");  
        con.setRequestProperty("Charset", "UTF-8");  
        // 设置边界  
        String BOUNDARY = "----------" + System.currentTimeMillis();  
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);  
        // 请求正文信息  
        // 第一部分：  
        StringBuilder sb = new StringBuilder();  
        sb.append("--"); // 必须多两道线  
        sb.append(BOUNDARY);  
        sb.append("\r\n"); 
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\""+ imgName + "\"\r\n"); 		//imgName必须包含.jpg等扩展名
        sb.append("Content-Type:application/octet-stream\r\n\r\n");  
        byte[] head = sb.toString().getBytes("utf-8");  
        // 获得输出流  
        OutputStream out = new DataOutputStream(con.getOutputStream());  
        // 输出表头  
        out.write(head);  
        
        // 文件正文部分  
        // 把文件已流文件的方式 推入到url中  
//	    DataInputStream in = new DataInputStream(new FileInputStream(file));  	//本地图片
		URL imgOfUrl = new URL(imgUrl);											//网络图片
		HttpURLConnection conn = (HttpURLConnection) imgOfUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inPutStream = conn.getInputStream();
		DataInputStream in = new DataInputStream(inPutStream);
        
        int bytes = 0;  
        byte[] bufferOut = new byte[1024];  
        while ((bytes = in.read(bufferOut)) != -1) {  
        	out.write(bufferOut, 0, bytes);  
        }  
        in.close();  
        
        // 结尾部分  
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线  
        out.write(foot);  
        out.flush();  
        out.close();  

    	String result = null;
        StringBuffer buffer = new StringBuffer();  
        BufferedReader reader = null;  
        try {  
        	// 定义BufferedReader输入流来读取URL的响应  
        	reader = new BufferedReader(new InputStreamReader(con.getInputStream()));  
        	String line = null;  
        	while ((line = reader.readLine()) != null) {
        		buffer.append(line);  
        	}  
        	if(result==null) result = buffer.toString();
        } catch (IOException e) {
        	System.out.println("发送POST请求出现异常！" + e);
        	e.printStackTrace();
        } finally {  
        	if(reader!=null) reader.close();
        }
	    JSONObject jsonObj = Json.toJO(result);
	    System.out.println("微信上传图片接口返回："+jsonObj);
	    
	    if(jsonObj.getIntValue("errcode") == 0){
	    	return jsonObj.getString("url");
	    }else{
	    	return "";
	    }
	}

	
	
}
