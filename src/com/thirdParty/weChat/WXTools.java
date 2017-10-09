package com.thirdParty.weChat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.database.DataSource;
import com.framework.database.MySQLUtils;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;
import com.framework.utils.PropertiesReader;
import com.framework.utils.XmlUtils;
import com.thirdParty.weChat.wxInterface.WXServiceMsg;

/**
 * 微信工具类
 * @author minqi 2017-08-12 10:28:25
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
 */
@Controller
@RequestMapping("/trust/wxTools")
public class WXTools {

	//微信服务器token
	private static final String TOKEN = PropertiesReader.getInstance().getProperty("WECHAT_TOKEN");
	
	
	/**
	 * 绑定微信账号
	 * @param json
	 * @param response
	 * @return
	 */
	public static boolean bindWeChat(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		String code = data.get("code").toString();
		String appid = data.get("appid").toString();
		
		
		return true;
	}
	
	/**
	 * 获取存在数据库的微信全配置；注意此方法返回数据不允许返回到客户端
	 * @param appid
	 * @return
	 */
	public static Map getWeChatConfig(String appid){
		//TODO 待定取哪个表
		
		
		Map returnMap = new HashMap();
		returnMap.put("secret", "fe0a94fd03c020d7530e7f023e9e472c");		//微信提供的测试公众号的secret
		
		return returnMap;
	}
	
	
	/**
	 * 获取access_token和api_ticket，7200秒的有效期，超过7000秒就重新获取
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getWeChatToken(String appid){
		//查询已有的access_token、api_ticket是否过期
		String sql = "SELECT * FROM C_WeChatSign WHERE appid = '"+ appid +"'";
		JdbcTemplate comm = DataSource.comm;
		Map tokenMap = MySQLUtils.sqlQueryForMap(comm, sql);
		
		Map returnMap = new HashMap();
		if(System.currentTimeMillis() / 1000 - Integer.parseInt(tokenMap.get("timestamp").toString()) > 7000){
			try {
				String url1 = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + tokenMap.get("secret");
				Map access_tokenMap  = HttpUtils.doGet(url1, null, null);
				String access_token = access_tokenMap.get("access_token").toString();
				
				String url2 = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";
				Map api_ticketMap  = HttpUtils.doGet(url2, null, null);
				String api_ticket = api_ticketMap.get("ticket").toString();
				
				returnMap.put("MSGID", "S");
				returnMap.put("appid", tokenMap.get("appid"));
				returnMap.put("access_token", access_token);
				returnMap.put("api_ticket", api_ticket);
				returnMap.put("timestamp", System.currentTimeMillis() / 1000);
				returnMap.put("serviceId", tokenMap.get("serviceId"));
				
				//更新access_token、api_ticket
				String updateSql = "UPDATE C_WeChatSign SET `access_token`=?access_token, `api_ticket`=?api_ticket, `timestamp`=?timestamp WHERE `serviceId`=?serviceId";
				MySQLUtils.sqlExecuteMap(comm, updateSql, returnMap);
			} catch (SQLException e1) {
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", "写入数据库失败");
			} catch (Exception e) {
				returnMap.put("MSGID", "E");
				returnMap.put("MESSAGE", "获取token失败");
			}
		}else{
			returnMap.put("MSGID", "S");
			returnMap.put("appid", tokenMap.get("appid"));
			returnMap.put("access_token", tokenMap.get("access_token"));
			returnMap.put("api_ticket", tokenMap.get("api_ticket"));
		}
		return returnMap;
	}
	
	/**
	 * 获取网页授权access_token，同时可获取微信用户openid
	 * @param code 页面用户授权获得的code
	 * @param appid 公众号appid
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getOpenId.do")
	public static String getOpenId(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		if(DataUtils.isNull(data.get("code")) || DataUtils.isNull(data.get("appid"))){
			HttpUtils.printString(response, "");
			return "";
		}
		
		String code = data.get("code").toString();
		String appid = data.get("appid").toString();
		
		//通过appid查询数据库中的secret配置
		Map configMap = getWeChatConfig(appid);
		Object secret = configMap.get("secret");
		
	   	String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
	   	Map resultMap = null;
	   	try {
			resultMap = HttpUtils.doGet(url, null, null);
		} catch (Exception e) {
			HttpUtils.printString(response, "");
			return "";
		}
	   	HttpUtils.printString(response, "");
		return resultMap.get("openid").toString();
	}
	
	/**
	 * 获取微信用户详细信息
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Map getWeChatUserInfo(String openid, String appid){
		Map tokenMap = getWeChatToken(appid);
		String access_token = tokenMap.get("access_token").toString();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
		
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
	public static String getWeixinImg(String imgName, String imgUrl, String appid) throws Exception{
		//http://localhost:8080/jlo2o/trust/weiXinShop/getWeixinImg.do
 
		//获取access_token
		Map tokenMap = getWeChatToken(appid);
		String access_token = tokenMap.get("access_token").toString();
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
	
	/**
	 * 接收微信公众平台的推送
	 * @param request GET：开启微信服务器的验证；POST：推送消息或事件
	 * @param response GET：原样返回echostr参数即验证成功；POST：返回指定xml格式即为自动回复
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/receiveService.do")
	public void receiveService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		
		//通过验证TOKEN，开启服务
		if(request.getMethod().toLowerCase().equals("get")) {
			//验证服务器地址有效性：微信服务器将发送GET请求到填写的服务器地址URL上，GET请求携带四个参数
	        String signature = request.getParameter("signature");	//微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数
	        String timestamp = request.getParameter("timestamp");	//时间戳
	        String nonce = request.getParameter("nonce");			//随机数
	        String echostr = request.getParameter("echostr");		//随机字符串
	        
	        String prama = "signature=" + signature + "&timestamp=" + timestamp + "&nonce=" + nonce + "&echostr=" + echostr;
	        System.out.println("微信服务器发来的参数："+prama);

	        if(checkSignature(this.TOKEN, signature, timestamp, nonce)){
	        	System.out.println("验签成功，是微信发来的消息，已开启微信服务器");
	            out.print(echostr);
	            out.flush();
	            out.close();
	        }
		//其他推送
		}else {
			Enumeration enu = request.getParameterNames();  
			while(enu.hasMoreElements()){  
				String paraName = (String)enu.nextElement();  
//				System.out.println(paraName+" : "+request.getParameter(paraName));
				//打印示例
//				signature : 176420ad047b9de82c8527d059266dd689fd5620
//				timestamp : 1505874851
//				nonce : 324757106
//				openid : oz29Y0rzM_1KT1CyySU_Zh7nPJYA
			}
			
			Map receiveData = XmlUtils.xmlToMap(request);
			System.out.println("微信推送来的xml转换成map：" + receiveData);
			//{Content=123123, CreateTime=1505878925, ToUserName=gh_4cd6ce95f880, FromUserName=oz29Y0rzM_1KT1CyySU_Zh7nPJYA, MsgType=text, MsgId=6467700735044133770}
			
			String replyStr = WXServiceMsg.autoReply_test(receiveData);
			//自动回复，即对微信推送的响应
            out.print(replyStr);
            out.flush();
            out.close();
		}
	}
	
	
	/**
	 * 验证签名，验证是否是微信服务器发来的
	 * 将token、timestamp、nonce三个参数进行字典序排序
	 * 将三个参数字符串拼接成一个字符串进行sha1加密
	 * 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        String[] arr = new String[] {token, timestamp, nonce};
        Arrays.sort(arr);									//排序
        
        StringBuffer content = new StringBuffer();			//生成字符串
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
       
        String temp = getSha1(content.toString()); 			//sha1加密
        return temp.equals(signature);
    }
	
	
	
	/**
	 * 微信js-sdk签名
	 * @param json 参数appid；url
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/weChatJsSign.do")
	public Map<String, String> weChatJsSign(String json, HttpServletResponse response){
		Map data = Json.toMap(json);
		
		String appid = data.get("appid").toString();
		String url = data.get("url").toString();
		
        Map<String, String> returnMap = new HashMap<String, String>();
        
        //获取js的api_ticket
        Map tokenMap = getWeChatToken(appid);							
        
        String jsapi_ticket = tokenMap.get("api_ticket").toString();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();

        //注意这里参数名必须全部小写，且必须有序
        String preSign = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        String signature = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(preSign.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        returnMap.put("appid", tokenMap.get("appid").toString());
        returnMap.put("jsapi_ticket", jsapi_ticket);
        returnMap.put("nonceStr", nonce_str);
        returnMap.put("timestamp", timestamp);
        returnMap.put("url", url);
        returnMap.put("signature", signature);
        
        HttpUtils.printString(response, returnMap);
        return returnMap;
    }
	
	/**
	 * 使用SHA1加密
	 * @param str
	 * @return
	 */
    public static String getSha1(String str) {
        if (null == str || 0 == str.length()) {
            return null;
        }
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
	
    /**
     * 获取签名
     * @param hash
     * @return
     */
    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 获取随机字符串
     * @return
     */
    public static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取时间戳
     * @return
     */
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
}
