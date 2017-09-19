package com.thirdParty.weChat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.utils.DataUtils;
import com.framework.utils.Json;


/**
 * 发送微信消息
 */
@Controller
@RequestMapping("/trust/wXMsg")
public class WXMsg{
	
	/**
	 * 注册成功消息模板
	{{first.DATA}}
	注册账号：{{keyword1.DATA}}
	注册时间：{{keyword2.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/registMsg.do")
	public Map registMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM409413579";

//测试写死数据	
//		map.put("header", "尊敬的用户：感谢您的注册，易商网城竭诚为您服务！");
//		map.put("userId", "13911445566");
//		map.put("servicePhone", "027-82800111");
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", map.get("header"));
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", encryptMobile(map.get("userId")));
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", DataUtils.getSysTime());
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", "客服电话：" + map.get("servicePhone"));
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	
	/**
	 * 密码重置通知
	{{first.DATA}}
	会员编号：{{keyword1.DATA}}
	会员姓名：{{keyword2.DATA}}
	重置密码：{{keyword3.DATA}}
	重置时间：{{keyword4.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/resetMsg.do")
	public Map resetMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM406259604";

//测试写死数据	
//		map.put("header", "尊敬的用户：您的易商网城登录密码已修改，若不是您本人操作的，请立即联系我们！");
//		map.put("userId", "13988885555");
//		map.put("userName", "这是用户名称");
//		map.put("password", "这是重置后的密码");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", map.get("header"));
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", encryptMobile(map.get("userId")));
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("userName"));
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("password"));
		tmp.put("color", color);
		msgMap.put("keyword3", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", DataUtils.getSysTime());
		tmp.put("color", color);
		msgMap.put("keyword4", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", "请妥善保管您的密码！");
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	
	/**
	 * 物业管理通知
	{{first.DATA}}
	标题：{{keyword1.DATA}}
	发布时间：{{keyword2.DATA}}
	内容：{{keyword3.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/propertyServiceMsg.do")
	public Map propertyServiceMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM204594069";

//测试写死数据	
//		map.put("userName", "张三");
//		map.put("title", "停水通知");
//		map.put("time", "2017-10-12");
//		map.put("content", "请各位小区业主注意，因市政施工影响，本周末将会停水，为期2天，请做好提前准备工作");
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您好：您收到一份通知/公告，请查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("title"));
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("time"));
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("content"));
		tmp.put("color", color);
		msgMap.put("keyword3", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", "请您知晓，如有疑问，请您联系物业！");
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	
	/**
	 * 物业进展反馈提醒
	{{first.DATA}}
	相关房屋：{{keyword1.DATA}}
	反馈类型：{{keyword2.DATA}}
	反馈状态：{{keyword3.DATA}}
	反馈信息：{{keyword4.DATA}}
	工作人员：{{keyword5.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/progressMsg.do")
	public Map progressMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM201006704";

//测试写死数据	
//		map.put("userName", "张三");
//		map.put("address", "幸福小区 B栋2单元401");
//		map.put("type", "小区环境问题");
//		map.put("status", "您的投诉已处理，已派人进行打扫");
//		map.put("content", "A栋三单元楼道很多垃圾");
//		map.put("handle", "王管理员");
//		map.put("handleTime", "2017-08-11 11:15:20");
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您的投诉已处理，请您查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("address"));		//地址
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("type"));			//投诉类型
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("status"));		//处理情况
		tmp.put("color", color);
		msgMap.put("keyword3", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("content"));		//投诉内容
		tmp.put("color", color);
		msgMap.put("keyword4", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("handle"));		//处理人
		tmp.put("color", color);
		msgMap.put("keyword5", tmp);
		
		
		tmp = new LinkedHashMap();
		tmp.put("value", "处理时间：" + map.get("handleTime"));
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	/**
	 * 房屋报修进展通知
	{{first.DATA}}
	工单编号：{{keyword1.DATA}}
	房屋编号：{{keyword2.DATA}}
	报修主题：{{keyword3.DATA}}
	报修状态：{{keyword4.DATA}}
	处理信息：{{keyword5.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/repairMsg.do")
	public Map repairMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM202658211";

//测试写死数据	
//		map.put("userName", "张三");
//		map.put("id", "5416541616");
//		map.put("address", "幸福小区 B栋2单元401");
//		map.put("type", "小区楼道漏水");
//		map.put("status", "已处理");
//		map.put("content", "已派维修师傅维修完毕，地面水渍也处理完毕");
//		map.put("money", "120.5");
//		map.put("handleTime", "2017-08-11 11:15:20");
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您的投诉已处理，请您查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("id"));			//工单编号
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", map.get("address"));		//房屋编号(地址)
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("type"));			//报修主题(报修类型)
		tmp.put("color", color);
		msgMap.put("keyword3", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("status"));		//报修状态()
		tmp.put("color", color);
		msgMap.put("keyword4", tmp);

		tmp = new LinkedHashMap();
		tmp.put("value", map.get("content"));		//处理信息(处理情况)
		tmp.put("color", color);
		msgMap.put("keyword5", tmp);
		
		String remark = "维修费用：￥" + map.get("money") + "\n" +
						"处理时间：" + map.get("handleTime");
		tmp = new LinkedHashMap();
		tmp.put("value", remark);
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	
	/**
	 * 缴费成功通知
	{{first.DATA}}
	房号：{{keyword1.DATA}}
	费用周期：{{keyword2.DATA}}
	缴费类型：{{keyword3.DATA}}
	金额：{{keyword4.DATA}}
	缴费时间：{{keyword5.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/paySuccessMsg.do")
	public Map paySuccessMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "OPENTM411449868";

//测试写死数据	
//		map.put("userName", "张三");
//		map.put("address", "幸福小区 3栋1单元502");
//		map.put("month", "2017-10-01至2017-11-01");
//		map.put("type", "水费");
//		map.put("money", "100.00");
//		map.put("time", "2017-11-01");
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您已经缴纳"+map.get("type")+"，请查阅缴费清单！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();					//地址
		tmp.put("value", map.get("address"));
		tmp.put("color", color);
		msgMap.put("keyword1", tmp);
		
		tmp = new LinkedHashMap();					//费用周期
		tmp.put("value", map.get("month"));
		tmp.put("color", color);
		msgMap.put("keyword2", tmp);

		tmp = new LinkedHashMap();					//缴费类型
		tmp.put("value", map.get("type"));
		tmp.put("color", color);
		msgMap.put("keyword3", tmp);

		tmp = new LinkedHashMap();					//金额
		tmp.put("value", "￥"+map.get("money"));
		tmp.put("color", color);
		msgMap.put("keyword4", tmp);
		
		tmp = new LinkedHashMap();					//缴费时间
		tmp.put("value", map.get("time"));
		tmp.put("color", color);
		msgMap.put("keyword5", tmp);
		
		tmp = new LinkedHashMap();
		tmp.put("value", "请您知晓，如有疑问，请您联系物业！");
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	/**
	 * 水费缴费通知
	{{first.DATA}}
	业主姓名：{{userName.DATA}}
	用水地址：{{address.DATA}}
	计费月份：{{month.DATA}}
	计费水量：{{power.DATA}}
	水费金额：{{pay.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/waterRateMsg.do")
	public Map waterRateMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "TM00132";

//测试写死数据	
//		map.put("userName", "李四");
//		map.put("address", "花园小区 3栋4单元602");
//		map.put("month", "2017-10-01至2017-11-01");
//		map.put("power", "10");
//		map.put("pay", "100.00");
//		
//		map.put("lastMonth", "210");
//		map.put("thisMonth", "310");
//		map.put("unitPrice", "1.56");
//		map.put("service", "河南兴安物业管理有限公司");
//		
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您本月的水费清单已生成，请您查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();					//业主姓名
		tmp.put("value", map.get("userName"));
		tmp.put("color", color);
		msgMap.put("userName", tmp);
		
		tmp = new LinkedHashMap();					//用水地址
		tmp.put("value", map.get("address"));
		tmp.put("color", color);
		msgMap.put("address", tmp);

		tmp = new LinkedHashMap();					//计费月份
		tmp.put("value", map.get("month"));
		tmp.put("color", color);
		msgMap.put("month", tmp);

		tmp = new LinkedHashMap();					//计量水量
		tmp.put("value", map.get("power")+"吨");
		tmp.put("color", color);
		msgMap.put("power", tmp);
		
		tmp = new LinkedHashMap();					//水费金额
		tmp.put("value", "￥"+map.get("pay"));
		tmp.put("color", color);
		msgMap.put("pay", tmp);
		
		String remark = "上月抄表：" + map.get("lastMonth") + "吨 \n" +
						"本月抄表：" + map.get("thisMonth") + "吨 \n" +
						"实际用量：" + map.get("power") + "吨 \n" +
						"物业单价：" + map.get("unitPrice") + "吨 \n" +
						"缴费商户：" + map.get("service");
		tmp = new LinkedHashMap();
		tmp.put("value", remark);
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	/**
	 * 电费缴费提醒
	{{first.DATA}}
	业主姓名：{{userName.DATA}}
	用电地址：{{address.DATA}}
	计费月份：{{month.DATA}}
	计费电量：{{power.DATA}}
	电费金额：{{pay.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/powerRateMsg.do")
	public Map powerRateMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "TM00133";

//测试写死数据	
//		map.put("userName", "张三丰");
//		map.put("address", "临江小区 3栋6单元905");
//		map.put("month", "2017-10-02至2017-11-02");
//		map.put("power", "10");
//		map.put("pay", "100.00");
//		
//		map.put("lastMonth", "120");
//		map.put("thisMonth", "186");
//		map.put("unitPrice", "0.75");
//		map.put("service", "河南兴安物业管理有限公司");
//		
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，您本月的电费清单已生成，请您查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();					//业主姓名
		tmp.put("value", map.get("userName"));
		tmp.put("color", color);
		msgMap.put("userName", tmp);
		
		tmp = new LinkedHashMap();					//用电地址
		tmp.put("value", map.get("address"));
		tmp.put("color", color);
		msgMap.put("address", tmp);

		tmp = new LinkedHashMap();					//计费月份
		tmp.put("value", map.get("month"));
		tmp.put("color", color);
		msgMap.put("month", tmp);

		tmp = new LinkedHashMap();					//计量电量
		tmp.put("value", map.get("power")+"度");
		tmp.put("color", color);
		msgMap.put("power", tmp);
		
		tmp = new LinkedHashMap();					//电费金额
		tmp.put("value", "￥"+map.get("pay"));
		tmp.put("color", color);
		msgMap.put("pay", tmp);
		
		String remark = "上月抄表：" + map.get("lastMonth") + "度 \n" +
						"本月抄表：" + map.get("thisMonth") + "度 \n" +
						"实际用量：" + map.get("power") + "度 \n" +
						"物业单价：" +  map.get("unitPrice") + "元/度 \n" +
						"缴费商户："+  map.get("service");
		tmp = new LinkedHashMap();
		tmp.put("value", remark);
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	/**
	 * 物业费缴费提醒
	{{first.DATA}}
	业主姓名：{{userName.DATA}}
	地址：{{address.DATA}}
	物业费金额：{{pay.DATA}}
	{{remark.DATA}}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/serviceRateMsg.do")
	public Map serviceRateMsg(String json) throws Exception {
		Map map = Json.toMap(json);
		String templateId_short = "TM00131";

//测试写死数据	
//		map.put("userName", "李四光");
//		map.put("address", "幸福小区 A栋三单元506");
//		map.put("pay", "100.00");
//
//		map.put("month", "2017-10-01至2017-10-31");
//		map.put("area", "149");
//		map.put("unitPrice", "1.5");
//		map.put("service", "河南兴安物业管理有限公司");
//		
//		map.put("url", "");
//		map.put("openId", "obVLrjokoId8PFcPxBF73OUBWlRA");
//		map.put("appid", "wxd6a3cd6fd304a872");
		
		Map msgMap = new LinkedHashMap();
		Map tmp = new LinkedHashMap();
		String color = "#173177";
		
		tmp.put("value", "尊敬的"+map.get("userName")+"，已为您生成本期物业费清单，请您查阅！");
		tmp.put("color", color);
		msgMap.put("first", tmp);
		
		tmp = new LinkedHashMap();					//业主姓名
		tmp.put("value", map.get("userName"));
		tmp.put("color", color);
		msgMap.put("userName", tmp);
		
		tmp = new LinkedHashMap();					//地址
		tmp.put("value", map.get("address"));
		tmp.put("color", color);
		msgMap.put("address", tmp);

		tmp = new LinkedHashMap();					//物业费金额
		tmp.put("value", "￥"+map.get("pay"));
		tmp.put("color", color);
		msgMap.put("pay", tmp);
		
		String remark = "缴费周期：" + map.get("month") + "\n" +
						"建筑面积：" + map.get("area") + "㎡ \n" +
						"物业单价：" + map.get("unitPrice") + "元/㎡/月 \n" +
						"缴费商户：" + map.get("service");
		tmp = new LinkedHashMap();
		tmp.put("value", remark);
		tmp.put("color", color);
		msgMap.put("remark", tmp);
		
		String url = "";
		if(!DataUtils.isNull(map.get("url"))) url = map.get("url").toString();

		String template_id = getTemplateId(map.get("appid").toString(), templateId_short);
		return sendWeChatMsg(map.get("appid").toString(), template_id, map.get("openId").toString(), msgMap, url);
	}
	
	
	/**
	 * 手机号以139****1234的形式展现
	 * @return
	 */
	private String encryptMobile(Object mobile){
		String head = mobile.toString().substring(0, 3);
		String foot = mobile.toString().substring(7);
		return head + "****" + foot;
	}
	
	/**
	 * 通过templateId_short获取微信消息模板template_id
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getTemplateId(String appid, String templateId_short) throws Exception{
		Map queryMap = new HashMap();
		queryMap.put("template_bh", templateId_short);
		queryMap.put("appid", appid);
		
//		Map resultMap = findOne("W_WebCatMT", queryMap, null);
		return "";
	}
	
	
	/**
	 * 发送公众号消息
	 * @param appid 公众号appid
	 * @param template_id 微信消息模板（公众平台添加后才有）
	 * @param openid 用户openid
	 * @param msgMap 消息内容
	 * @param url 消息跳转路径（可空）
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map sendWeChatMsg(String appid, String template_id, String openid, Map msgMap, String url) throws Exception{
    	Map paramsMap = new HashMap();
		//获取access_token
        String access_token = "";

		//发送消息
		String Address = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
		paramsMap.put("touser", openid);
		paramsMap.put("template_id", template_id);
		if(!DataUtils.isNull(url)) paramsMap.put("url", url);
		
		paramsMap.put("data", msgMap);
		//TODO 换成HTTPUtils
		String str =  "";
//		String str = Http.post(Address,Json.toJson(paramsMap), "application/json", 1000, 1000);
		System.out.println("！！！！！微信发送消息返回：" + str);
		return Json.toMap(str);		
	}
	

	
	
}
