package com.framework.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 全局的配置文件类
 * 读取在WEB-INFO/V1.config文件
 * @author minqi
 *
 */
public class V1 {
	
	private static final Properties properties = new Properties();		//静态常量，只有一个实例化Properties对象
	
    /**
     * 配置文件默认放在workspace-->项目根目录下。例../workspace\MQFrame\v1.config
     * 如果部署到web服务器上，则路径变为bin目录下
     */
    private static String fileName =  V1.class.getClassLoader().getResource("/").getPath().replace("classes", "v1.config");
    //初始化的v1.config路径：/D:/apache-tomcat-7.0.81/webapps/marshow/WEB-INF/classes/
//    private static String fileName = System.getProperty("user.dir") + System.getProperty("file.separator") + "v1.config";
    //初始化的v1.config路径：D:\MyEclipse2017_CI7\v1.config
    
    /**
     * 静态代码块，只执行一次
     */
    static {
        try {
        	System.out.println("_______初始化的v1.config路径：" + fileName);
        	System.out.println("_______原有的v1.config路径："+ System.getProperty("user.dir") + System.getProperty("file.separator") + "v1.config");
			properties.load(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("严重异常：未找到配置文件");
		} catch (IOException e) {
			throw new RuntimeException("严重异常：加载配置文件失败");
		}
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static void setProperty(String key,String value) {
         properties.setProperty(key, value);
    }

}
