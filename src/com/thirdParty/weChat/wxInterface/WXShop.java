package com.thirdParty.weChat.wxInterface;

import java.awt.image.DataBufferUShort;
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

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.framework.database.DBHandler;
import com.framework.utils.DataUtils;
import com.framework.utils.HttpUtils;
import com.framework.utils.Json;

/**
 * 微信门店
 * @author minqi 2017-07-31 17:25:48
 *
 */
@Controller
@RequestMapping("/trust/wXShop")
public class WXShop extends DBHandler {
	
	private static String[] available_state = {"","系统错误","审核中","审核通过","审核驳回"};
	//available_state：门店是否可用状态。1 表示系统错误、2 表示审核中、3 审核通过、4 审核驳回。1、2、4 状态不返回poi_id
	
	private static String[] update_status = {"扩展字段没有在更新中或更新已生效，可以再次更新","扩展字段正在更新中，尚未生效，不允许再次更新"};
	//update_status：扩展字段是否正在更新中。 0 表示扩展字段没有在更新中或更新已生效，可以再次更新；1 表示扩展字段正在更新中，尚未生效，不允许再次更新


	
}
