package com.thirdParty.weChat.wxInterface;

import java.util.Map;

/**
 * 微信推送事件
 * @author minqi 2017-11-04 14:11:32
 *
 */
public class WXEventPush {
	
	//微信门店审核结果推送
	private static final String poi_check_notify = "poi_check_notify";

	//扫码推事件的事件推送
	private static final String scancode_push = "scancode_push";

	//成功连接Wi-Fi事件推送
	private static final String WifiConnected = "WifiConnected";
	
	
	
	
	
	
	
	/**
	 * 微信事件推送路由
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map wxPushRoute(Map receiveData){
		
		// TODO 记录推送的消息
		
		String Event = receiveData.get("Event").toString();
		
		if (Event.equals(poi_check_notify)) {
			wxShopPush(receiveData);
			
		} else if(Event.equals(scancode_push)) {
			wxScanPush(receiveData);
			
		} else if(Event.equals(WifiConnected)) {
			wxWifiPush(receiveData);
			
		} else {
			System.out.println("其他推送");
		}
		
		
		return null;
	}
	
	
	
	
	/**
	 * 门店审核结果推送
	 * ToUserName		发送方帐号（一个OpenID）
	 * FromUserName		错误信息
	 * CreateTime		消息创建时间（整型）
	 * MsgType			消息类型，event
	 * Event			事件类型，poi_check_notify
	 * UniqId			商户自己内部ID，即字段中的sid
	 * PoiId			微信的门店ID，微信内门店唯一标示ID
	 * Result			审核结果，成功succ 或失败fail
	 * msg				成功的通知信息，或审核失败的驳回理由
	 * @param receiveData	
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static Map wxShopPush(Map receiveData){
		
		return null;
	}
	
	
	/**
	 * 扫码推事件的事件推送
	 * ToUserName		开发者微信号
	 * FromUserName		发送方帐号（一个OpenID）
	 * CreateTime		消息创建时间（整型）
	 * MsgType			消息类型，event
	 * Event			事件类型，scancode_push
	 * EventKey			事件KEY值，由开发者在创建菜单时设定
	 * ScanCodeInfo		扫描信息
	 * ScanType			扫描类型，一般是qrcode
	 * ScanResult		扫描结果，即二维码对应的字符串信息
	 * @param receiveData
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static Map wxScanPush(Map receiveData){
		
		return null;
	}
	
	/**
	 * 成功连接Wi-Fi事件推送
	 * ToUserName		开发者微信号
	 * FromUserName		连网的用户帐号（一个OpenID）
	 * CreateTime		消息创建时间 （整型）
	 * MsgType			消息类型，event
	 * Event			事件类型，WifiConnected (Wi-Fi连网成功)
	 * ConnectTime		连网时间（整型）
	 * ExpireTime		系统保留字段，固定值
	 * VendorId			系统保留字段，固定值
	 * ShopId			门店ID，即shop_id
	 * DeviceNo			连网的设备无线mac地址，对应bssid
	 * @param receiveData
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static Map wxWifiPush(Map receiveData){
		
		return null;
	}
	
	
	
}
