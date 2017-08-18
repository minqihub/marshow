package com.framework.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * http请求类
 * @author minqi 2017-08-04 16:48:16
 *
 */
public class HttpUtils {
	
	/**
	 * post请求方法，此方法请求数据的格式是raw，而不是form-data
	 * 主要用于调用微信接口
	 * @param url
	 * @param json
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Map jsonPost(String url, String json) throws ParseException, IOException{
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    StringEntity postingString = new StringEntity(json,"utf-8");		//json传递  
	    post.setEntity(postingString);
	    
	    post.addHeader("Content-type","application/json; charset=utf-8");
	    post.setHeader("Accept", "application/json");
	    HttpResponse response = httpClient.execute(post);
	    
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
	    System.out.println("微信接口请求返回值："+result2);
	    return Json.toMap(result2);
	}
	
	/**
	 * 连接外部url，获取数据
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String postFormMap(String url, Map data) throws Exception {
    	HttpRequest hr = HttpRequest.post(url);
		if (url != null) {
			if ("https://".equalsIgnoreCase(url.substring(0, 8))) {
	    		hr.trustAllCerts().trustAllHosts();
	    	}
		} else {
			throw new RuntimeException("url required.");
		}
		hr.connectTimeout(10 * 1000); // 连接服务等待 10 秒
		hr.readTimeout(60 * 1000);    // 获取数据等待 60 秒
		return hr.form(data).body();
    }
	
	
    /**
     * 因为过滤器对.do方法返回进行了包装，对于外部接口访问本项目的.do方法时，按照给定参数进行页面返回，
     * （过滤器中的该方法：包装视图，让前台访问.do方法时，返回json格式的视图，以便前台ajax方法方便获取返回值）
     * @param response
     * @param data
     * @throws Exception
     */
    public static void printString(HttpServletResponse response, Object data){
        PrintWriter pw = null;
		try {
			pw = response.getWriter();
            pw.print(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
            pw.close();
        }
    }
	
	/**
	 * GET请求
	 * @param url 请求地址
	 * @param headers 请求头
	 * @return 响应内容
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map doGet(String url, Map<String, String> headers, Map<String, String> querys) throws Exception {
    	HttpClient httpClient = wrapClient(url);
    	
    	//拼接请求地址
    	HttpGet request = new HttpGet(buildUrl(url, querys));
    	
    	//拼接头部
    	if(headers.isEmpty() || headers == null){
    		request.addHeader("Content-Type", "application/x-www-form-urlencoded");		//默认头部
    	}else{
            for (Map.Entry<String, String> e : headers.entrySet()) {
            	request.addHeader(e.getKey(), e.getValue());
            }
    	}

        //发起请求
        HttpResponse response = httpClient.execute(request);
        
        //获取响应状态
        System.out.println(response.getStatusLine()); 							//HTTP/1.1 200 OK
        
        //获取返回内容
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
        return Json.toMap(result2);
    }
	
	/**
	 * post form
	 * 
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param bodys
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map doPostForm(String url, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpPost request = new HttpPost(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }
        
        //发起请求
        HttpResponse response = httpClient.execute(request);
        
        //获取响应状态
        System.out.println(response.getStatusLine()); 							//HTTP/1.1 200 OK
        
        //获取返回内容
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
        return Json.toMap(result2);
    }	
	
	/**
	 * Post String
	 * 
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map doPostString(String url, Map<String, String> headers, Map<String, String> querys, String body) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpPost request = new HttpPost(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
        	request.setEntity(new StringEntity(body, "utf-8"));
        }
        
        //发起请求
        HttpResponse response = httpClient.execute(request);
        
        //获取响应状态
        System.out.println(response.getStatusLine()); 							//HTTP/1.1 200 OK
        
        //获取返回内容
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
        return Json.toMap(result2);
    }
	
	/**
	 * Post stream
	 * 
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map doPostStream(String url, Map<String, String> headers, Map<String, String> querys, byte[] body) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpPost request = new HttpPost(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
        	request.setEntity(new ByteArrayEntity(body));
        }
        
        //发起请求
        HttpResponse response = httpClient.execute(request);
        
        //获取响应状态
        System.out.println(response.getStatusLine()); 							//HTTP/1.1 200 OK
        
        //获取返回内容
	    String result1 = EntityUtils.toString(response.getEntity());
	    String result2 = new String(result1.getBytes("ISO8859-1"),"utf-8");		//字符乱码
        return Json.toMap(result2);
    }
	
	/**
	 * Put String
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String url, Map<String, String> headers, Map<String, String> querys, String body) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpPut request = new HttpPut(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
        	request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }
	
	/**
	 * Put stream
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String url, Map<String, String> headers, Map<String, String> querys, byte[] body) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpPut request = new HttpPut(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
        	request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }
	
	/**
	 * Delete
	 *  
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doDelete(String url, Map<String, String> headers, Map<String, String> querys) throws Exception {    	
    	HttpClient httpClient = wrapClient(url);

    	HttpDelete request = new HttpDelete(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }
        
        return httpClient.execute(request);
    }
	
	
	/**
	 * 将querys以?aaa=bbb&ccc=ddd的形式拼接到url中
	 * @param host
	 * @param path
	 * @param querys
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String buildUrl(String url, Map<String, String> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(url);
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, String> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append("&");
        		}
        		if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
        			sbQuery.append(query.getValue());
                }
        		if (!StringUtils.isBlank(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (!StringUtils.isBlank(query.getValue())) {
        				sbQuery.append("=");
        				sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
        			}        			
                }
        	}
        	if (0 < sbQuery.length()) {
        		sbUrl.append("?").append(sbQuery);
        	}
        }
    	return sbUrl.toString();
    }
	
	@SuppressWarnings("deprecation")
	private static HttpClient wrapClient(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		if (url.toLowerCase().startsWith("https://")) {
			sslClient(httpClient);
		}
		
		return httpClient;
	}
	
	private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                	
                }
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                	
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
        	throw new RuntimeException(ex);
        }
    }
}