package com.user;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 获取用户硬件信息
 * @author minqi
 */
@Controller
public class UserSecret {
	
	@RequestMapping("/getUserInfo.do")
	public void getUserInfo(HttpServletRequest request){
/*		
		System.out.println("getContextPath()：" + request.getContextPath());
		System.out.println("getRemoteAddr()：" + request.getRemoteAddr());
		System.out.println("User-Agent：" + request.getHeader("User-Agent"));

		HttpSession session = request.getSession();      
		ServletContext  application  = session.getServletContext();    
		String serverRealPath = application.getRealPath("/") ;
		System.out.println("tomcat路径：" + serverRealPath);
		
/*
		getContextPath()：/marshow
		getRemoteAddr()：0:0:0:0:0:0:0:1
		User-Agent：Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36
*/		
		
	}
	
	
	
	
	
}
