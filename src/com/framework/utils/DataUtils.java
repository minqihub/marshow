package com.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.codec.binary.Base64;

/**
 * 普通字符数据处理类
 * @author minqi
 * 2016-11-21
 */
public class DataUtils {
	
	/**
	 * 是否为空
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isNull(Object obj){
		if(obj == "" || obj == null || obj.toString().equals("") || obj.toString().equals("null")){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否是纯数字
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isNumber(Object obj){
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(obj.toString());
		if(isNum.matches()){
			return true; 
		}
		return false;
	}
	
	/**
	 * 是否是小数
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isDecimal(Object obj){
		if(isNull(obj)){
			return false;
		}
		Pattern pattern2 = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");//小数
		Matcher temp2 = pattern2.matcher(obj.toString());
		if(temp2.matches()){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否是金额（是否是整数或小数）
	 * @param money
	 * @return
	 * 2016-11-19
	 */
	public static boolean isMoney(String money){
		if(isNull(money)){
			return false;
		}else{
			Pattern pattern1 = Pattern.compile("[0-9]*"); //整数
			Matcher temp1 = pattern1.matcher(money);
			if(temp1.matches()){
				return true; 
			}else {
				Pattern pattern2 = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");//小数
				Matcher temp2 = pattern2.matcher(money);
				if(!temp2.matches()){
					return false;
				}
				return true; 
			}
		}
	}
	
	
	
	/**
	 * 设置小数精度
	 * @param obj
	 * @param point 保留几位小数
	 * @return
	 * 2016-11-19
	 */
	public static String setDecimal(Object obj, int point){
//		TODO
		
		
		
		
		return "";
	}
	
	/**
	 * 是否全中文
	 * @param obj
	 * @return
	 */
	public static boolean isChinese(Object obj){
//		TODO
		
		
		
		return true;
	}
	
	/**
	 * 是否包含中文
	 * @param obj
	 * @return
	 */
	public static boolean containChinese(Object obj){
//		TODO
		
		
		
		
		return true;
	}
	
	
	/**
	 * 是否是邮箱
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isEmail(Object obj){
		if(isNull(obj)){
			return false;
		}
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(obj.toString());
		return m.matches();
	}
	
	/**
	 * 判断是否是身份证号
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isIDCode(Object obj){
		if(isNull(obj) || obj.toString().length() != 15 || obj.toString().length() != 18){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 判断是否是手机号
	 * @param obj
	 * @return
	 * 2016-11-19
	 */
	public static boolean isPhone(Object obj){
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(obj.toString());

		return matcher.matches();
	}
	
	/**
	 * 自动补0
	 * @param obj
	 * @param length 要求补零后的字符长度
	 * @param isBack true从后面补零，false从前面补零
	 * @return
	 * 2016-11-19
	 */
	public static String addZero(Object obj, int length, boolean isBack){
		String str = obj.toString();
		int addLength = length - obj.toString().length();

		for(int i = 0; i < addLength; i++){
			str = str + "0";
		}
		return str;
	}
	
	/**
	 * 替换null为""
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map replaceJsonNull(Map map) throws Exception{
		String json = Json.toJson(map).replaceAll("null", "\"\"");
		return Json.toMap(json);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map replaceMapNull(Map map) throws Exception{
		for(Iterator it = map.keySet().iterator(); it.hasNext();){
			String key = it.next().toString();
			if(isNull(map.get(key))){
				map.put(key, "");
			}
		}
		return map;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static List replaceListNull(List<Map> list) throws Exception{
		if (list != null) {
			for(int i=0;i<list.size();i++){
		        list.set(i, replaceMapNull(list.get(i)));
			}
		}
		return list;
	}
	
	/**
	 * 获得当前时间
	 * @param format yyyy-MM-dd 、 HH:mm:ss 、 HH:mm
	 * @return
	 * 2016-11-19
	 */
	public static String getSysTime(String format){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		String date = simpleDateFormat.format(calendar.getTime());
		return date;
	}
	
	/**
	 * 获得当前时间，例：2017-07-28 17:16:58
	 * @return
	 */
	public static String getSysTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String date = simpleDateFormat.format(calendar.getTime());
		return date;
	}
	
	/**
	 * 获得随机数字
	 * @param length 指定长度
	 * @return
	 * 2016-11-19
	 */
	public static String getRandomNum(int length){
		Random rand = new Random();
		String resultNum = "";
		for (int i = 0; i < length; i++) {
			String temp = rand.nextInt(10) + "";
			resultNum = resultNum + temp;
		}
		return resultNum;
	}

	/**
	 * 通过UUID获得32位长度字符串
	 * @return
	 */
	public static String getUUID() {
        return UUID.randomUUID().toString();
    }
	
	/**
	 * 获得随机数字、字母组合
	 * @param length
	 * @return
	 * 2016-11-19
	 */
	public static String getRandomStr(int length){
		int[] num = {1,2,3,4,5,6,7,8,9,0};
		char[] str = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		String resultStr = "";
		Random rand = new Random();				//[0,1)
		for (int i = 0; i < length; i++) {
			int choose  = rand.nextInt();
			int numIndex = rand.nextInt(num.length);
			int strIndex = rand.nextInt(str.length);
			if( choose % 2 ==0){
				resultStr = resultStr + num[numIndex];
			}else{
				resultStr = resultStr + str[strIndex];
			}
		}
		return resultStr;
	}
	
	/**
	 * 加密字符（可逆）
	 * @param str 待加密字符
	 * @return
	 * 2016-11-19
	 */
	public static String getEncode(String str){
		Base64 bs = new Base64();
		return bs.encodeToString(str.getBytes());
	}
	
	/**
	 * 解密字符（可逆）
	 * @param str 待解密字符
	 * @return
	 * 2016-11-19
	 */
	public static String getDecode(String str){
		Base64 bs = new Base64();
		return new String(bs.decodeBase64(str));
	}
	
	
	/**
	 * MD5加密，不可逆（commons-codec-1.10.jar）
	 * @param str 待加密字符
	 * @return
	 * @throws NoSuchAlgorithmException
	 * 2016-11-21
	 */
	public static String getMD5code(String str) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("md5");			//使用MD5算法，不可逆
		byte[] bytes = md.digest(str.getBytes());						//用md5算法，获得str的摘要。这段摘要在任何码表中可能都没有对应的字符
		
		Base64 bs = new Base64();
		return bs.encodeToString(bytes);								//把二进制转换成可见字符：base64
	}
	
	/**
	 * 超级加密（先循环多次可逆加密，再MD5加密）
	 * @param str
	 * @return
	 */
	public static String getSuperMD5(String str){
		//TODO
		return "";
	}
	
	
	/**
	 * 获得字符编码
	 * @param str
	 * @return
	 * 2016-11-19
	 */
	public static String getEncoding(String str) {
        String[] encode = {"GB2312","ISO-8859-1","UTF-8","GBK"};
        for (int i = 0; i < encode.length; i++) {
            try {
                if (str.equals(new String(str.getBytes(encode[i]), encode[i]))) {
                    String result = encode[i];
                    return result;
                }
            } catch (Exception exception) {
    		   return "undifine";
            }
		}
       return "unfind";
	}
	
	/**
	 * 过滤特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String   str)   throws   PatternSyntaxException   {      
        String regEx="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";   
        Pattern p = Pattern.compile(regEx);      
        Matcher m = p.matcher(str);      
        return m.replaceAll("").trim();      
    }
	
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    public static void copyLarge(InputStream input, OutputStream output) throws
            IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
	
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copyLarge(input, output);
        output.close();
        input.close();
        return output.toByteArray();
    }
    
	
}
