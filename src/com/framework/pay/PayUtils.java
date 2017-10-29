package com.framework.pay;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 支付常用的方法
 * @author minqi 2017-10-27 21:57:09
 *
 */
public class PayUtils {
	
    /**
     * 获取随机字符串 Nonce Str
     * @return String 随机字符串
     */
    public static String getNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
    
    /**
     * 获取当前时间戳，单位秒
     * @return
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 生成签名，signType支持MD5、HMAC-SHA256，默认为MD5
     * @param data 待签名数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名
     * @throws Exception 
     */
    public static String getSignature(final Map<String, String> data, String key, String signType) throws Exception{
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        
        for (String k : keyArray) {
            if (k.equals("sign")) {							//sign不参与签名
                continue;
            }
            if (data.get(k).trim().length() > 0){ 			//参数值为空，不参与签名
            	sb.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        
        sb.append("key=").append(key);
        
        if ("MD5".equals(signType) || signType == null) {
            return MD5(sb.toString()).toUpperCase();
        } else if ("HMAC-SHA256".equals(signType)) {
            return HMACSHA256(sb.toString(), key);
        } else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }
    
    
    /**
     * 生成 MD5
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    
    
    
}
