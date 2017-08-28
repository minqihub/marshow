package com.framework.file;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.framework.utils.Json;

/**
 * Xml解析类
 * @author minqi
 *
 */
public class XmlUtils {
	
	
	/**
	 * @todo 将map转换成xml格式
	 * @param parameters
	 * @return
	 */
	public static String parseXML(Map parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
	
	
	
	
	
//	public static final String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?><stream><returnRecords>6</returnRecords><status>AAAAAAA</status><statusText>交易成功</statusText><list name=\"userDataList\"><row><ACCBAL>1000000000.00</ACCBAL><CDFG>C</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>C99004547001DI</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME></OPPACCNAME><OPPACCNO>7111010130900000001</OPPACCNO><OPPBANKNO></OPPBANKNO><OPPBRANCHNAME></OPPBRANCHNAME><RESUME></RESUME><TRANAMT>1000000000.00</TRANAMT><TRANDATE>20201017</TRANDATE><TRANTIME>104354</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row><row><ACCBAL>999999999.90</ACCBAL><CDFG>D</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>J0000000048141</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME>河南智云联创科技有限公司</OPPACCNAME><OPPACCNO>8110701015200020017</OPPACCNO><OPPBANKNO>711101</OPPBANKNO><OPPBRANCHNAME>中信银行总行营业部</OPPBRANCHNAME><RESUME></RESUME><TRANAMT>0.10</TRANAMT><TRANDATE>20201017</TRANDATE><TRANTIME>115438</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row><row><ACCBAL>999999999.80</ACCBAL><CDFG>D</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>J0000000036096</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME>河南智云联创科技有限公司</OPPACCNAME><OPPACCNO>8110701015200020017</OPPACCNO><OPPBANKNO>711101</OPPBANKNO><OPPBRANCHNAME>中信银行总行营业部</OPPBRANCHNAME><RESUME></RESUME><TRANAMT>0.10</TRANAMT><TRANDATE>20201019</TRANDATE><TRANTIME>100029</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row><row><ACCBAL>999999999.70</ACCBAL><CDFG>D</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>J0000000036240</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME>河南智云联创科技有限公司</OPPACCNAME><OPPACCNO>8110701015200020017</OPPACCNO><OPPBANKNO>711101</OPPBANKNO><OPPBRANCHNAME>中信银行总行营业部</OPPBRANCHNAME><RESUME></RESUME><TRANAMT>0.10</TRANAMT><TRANDATE>20201019</TRANDATE><TRANTIME>100328</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row><row><ACCBAL>999999999.50</ACCBAL><CDFG>D</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>J0000000036284</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME>河南智云联创科技有限公司</OPPACCNAME><OPPACCNO>8110701015200020017</OPPACCNO><OPPBANKNO>711101</OPPBANKNO><OPPBRANCHNAME>中信银行总行营业部</OPPBRANCHNAME><RESUME></RESUME><TRANAMT>0.20</TRANAMT><TRANDATE>20201019</TRANDATE><TRANTIME>100428</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row><row><ACCBAL>999999999.20</ACCBAL><CDFG>D</CDFG><CRYTYPE>001</CRYTYPE><HOSTFLW>J0000000038163</HOSTFLW><HOSTSEQ>1</HOSTSEQ><OPPACCNAME>河南智云联创科技有限公司</OPPACCNAME><OPPACCNO>8110701015200020017</OPPACCNO><OPPBANKNO>711101</OPPBANKNO><OPPBRANCHNAME>中信银行总行营业部</OPPBRANCHNAME><RESUME></RESUME><TRANAMT>0.30</TRANAMT><TRANDATE>20201019</TRANDATE><TRANTIME>104430</TRANTIME><TRANTYPE>23</TRANTYPE><XTSFAM>0.00</XTSFAM><subAccNo>3110710010091021276</subAccNo></row></list></stream>";

	//商户附属账户余额查询
	public static final String xml = "<stream><status>AAAAAAA</status><statusText>交易成功</statusText><list name=\"userDataList\"><row><DJAMT>0.00</DJAMT><KYAMT>0.00</KYAMT><SJAMT>0.00</SJAMT><SUBACCNM>金都瑞园</SUBACCNM><TZAMT>0.00</TZAMT><XSACVL>0.00</XSACVL><subAccNo>3111110034633603832</subAccNo></row></list></stream>";
	
//	public static final String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?><stream><status>AAAAAAE</status><statusText>交易成功</statusText><list name=\"userDataList\"><row><stt>0</stt><status>AAAAAAE</status><statusText>交易成功</statusText></row></list></stream>";
	
	public JSONObject xmlToJson(String xml) throws DocumentException{
		Document document = DocumentHelper.parseText(xml); 
		Element root = document.getRootElement();  				//取得根节点
		
		String resultStr = checkChildEle(root);
        System.out.println(resultStr);
        
        System.out.println("-----以上是初始的------以下是转换的json格式-------------------------------");

        JSONObject json = Json.toJO("{" + resultStr + "}");
        System.out.println(json);
        return json;
	}
	
	@SuppressWarnings("unchecked")
	public String checkChildEle(Element element) throws DocumentException{
		String json = "";
		List<Element> list = element.elements();		//其所有子节点
		if (list.size()>0) {							//有子节点
			for (Element ele : list) {					//循环所有子节点
				if(ele.getName().equals("list")){		//如果子节点名称是list
					json += '"' + ele.getName() +'"'+ ":" + '[' + checkChildEle(ele) +"]"; 
				}else if(ele.getName().equals("row")){	//如果子节点名称是row
					json += "{"+checkChildEle(ele)+"}";
				}else {									//其他普通子节点
					json += "\"" + ele.getName() +'"'+ ":" + '"'+ ele.getText() + "\"," + checkChildEle(ele); 
				}
			} 
		}
		return json; 
	}


	
	public static void main(String[] args) {
		
		try {
			
			XmlUtils x = new XmlUtils();
			x.xmlToJson(xml);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
}