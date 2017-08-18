package com.framework.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件类
 * @author minqi 2016-11-21
 * 
 */
public class PropertiesReader {

    private static final Properties properties = new Properties();		//静态常量，只有一个实例化Properties对象
    private static PropertiesReader reader = null;						//静态变量
    /**
     * 配置文件默认放在workspace-->项目根目录下。例../workspace\MQFrame\v1.config
     * 如果部署到web服务器上，则路径变为bin目录下
     */
    private static String fileName = System.getProperty("user.dir") + System.getProperty("file.separator") + "v1.config";
    
    /**
     * 构造方法，加载配置文件
     * @throws Exception
     */
    private PropertiesReader(){ 
        try {
			properties.load(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("MQFrame：未找到配置文件");
		} catch (IOException e) {
			throw new RuntimeException("MQFrame：加载配置文件失败");
		}
    }
    
    /**
     * 实例化本类对象
     * @return 本类对象
     */
    public static PropertiesReader getInstance() {
        if (reader == null) {
            try {
                reader = new PropertiesReader();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return reader;
    }
    
    /**
     * 键值对
     * @param key 键
     * @return 值
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void setProperty(String key,String value) {
         properties.setProperty(key, value);
    }

	public static void main(String[] args) {
		System.out.println(getInstance().getProperty("BaofengTV_URL"));
		System.out.println(getInstance().fileName);

	}

}
