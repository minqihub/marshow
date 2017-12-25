package com.framework.aop;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.web.servlet.HandlerInterceptor;  
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.framework.service.Authorization;
import com.framework.service.Login;

/**
 * spring拦截器类
 * @author minqi 2017-08-04 15:10:19
 *
 */
public class SpringInterceptor implements HandlerInterceptor {

	/** 
     * preHandle方法是进行处理器拦截用的，该方法将在Controller处理之前进行调用；
     * SpringMVC中的Interceptor拦截器是链式的，可以同时存在多个Interceptor，SpringMVC会根据声明的前后顺序一个接一个的执行，
     * 而且所有的Interceptor中的preHandle方法都会在Controller方法调用之前调用
	 * SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返回值为false，此时整个请求就结束了。 
     */  
	@Override
    @SuppressWarnings("rawtypes")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	System.out.println("！！！！！！拦截器preHandle执行了");
    	
//    	String projectName = request.getContextPath();		// /marshow
//    	String ip = request.getLocalAddr();					// 0:0:0:0:0:0:0:1
//    	String uri = request.getRequestURI();				// /marshow/trust/login/login.do
    	String servletPath = request.getServletPath();		// /trust/login/login.do
    	
    	//对访问路径进行登陆授信判断（@Controll包含trust则无需登录）
    	if(!servletPath.startsWith("/trust")){
//    		Map flagMap = Login.checkLogin(request, response);	//checkLogin方法有response返回，从而影响后面调用方法的response返回
    		Map flagMap = Authorization.getUserInfo(request);
    		if(flagMap.get("MSGID").toString().equals("E")){
    			System.out.println(flagMap);
    			return false;
    		}
    	}
        return true;  
    }  
      
    /** 
     * 只在当前这个Interceptor的preHandle方法返回值为true的时候才会执行 
     * 在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操作。
     * 这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用 
     */  
    @Override
    @SuppressWarnings("rawtypes")
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {  
    	System.out.println("！！！！！！拦截器postHandle执行了");
    }  
  
    /** 
     * 需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。
     * 该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行， 
     * 这个方法的主要作用是用于清理资源的 
     */  
    @Override  
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {  
    	System.out.println("！！！！！！拦截器afterCompletion执行了");
    }  
      
    
}  