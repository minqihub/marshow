package com.framework.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.framework.utils.DataUtils;
import com.framework.utils.PropertiesReader;

/**
 * 连接池类
 * @author minqi
 *
 */
public class DataSource {
	
	//配置文件读取对象
	public static PropertiesReader property = PropertiesReader.getInstance();
	
	//MySQL数据库参数
	private static String MySQL_IP = property.getProperty("MySQL_IP");
	private static String MySQL_PORT = property.getProperty("MySQL_PORT");
	private static String MySQL_DATABASE = property.getProperty("MySQL_DATABASE");
	private static String MySQL_USERNAME = property.getProperty("MySQL_USERNAME");
	private static String MySQL_PASSWORD = property.getProperty("MySQL_PASSWORD");
	//MySQL连接池参数
	private static int MySQL_InitialSize = Integer.parseInt(property.getProperty("MySQL_InitialSize"));
	private static int MySQL_MaxActive = Integer.parseInt(property.getProperty("MySQL_MaxActive"));
	private static int MySQL_MaxIdle = Integer.parseInt(property.getProperty("MySQL_MaxIdle"));
	private static int MySQL_MinIdle = Integer.parseInt(property.getProperty("MySQL_MinIdle"));
	private static int MySQL_MaxWait = Integer.parseInt(property.getProperty("MySQL_MaxWait"));
	
	//Oracle数据库参数
	private static String Oracle_IP = property.getProperty("Oracle_IP");
	private static String Oracle_PORT = property.getProperty("Oracle_PORT");
	private static String Oracle_DATABASE = property.getProperty("Oracle_DATABASE");
	private static String Oracle_USERNAME = property.getProperty("Oracle_USERNAME");
	private static String Oracle_PASSWORD = property.getProperty("Oracle_PASSWORD");
	//Oracle连接池参数
	private static int Oracle_InitialSize = Integer.parseInt(property.getProperty("Oracle_InitialSize"));
	private static int Oracle_MaxActive = Integer.parseInt(property.getProperty("Oracle_MaxActive"));
	private static int Oracle_MaxIdle = Integer.parseInt(property.getProperty("Oracle_MaxIdle"));
	private static int Oracle_MinIdle = Integer.parseInt(property.getProperty("Oracle_MinIdle"));
	private static int Oracle_MaxWait = Integer.parseInt(property.getProperty("Oracle_MaxWait"));
	
	//连接池对象
	public static BasicDataSource dataSource_MySql;
	public static BasicDataSource dataSource_Oracle;
	
	//
	public static JdbcTemplate comm;
	public static JdbcTemplate orcl;
	
	
	//静态代码块：初始化连接池
	//顺序：静态代码块（只第一次执行）-->非静态代码块！-->默认构造方法！-->普通方法中的代码块
	static{
		if(dataSource_MySql == null){
			dataSource_MySql = new BasicDataSource();
			dataSource_MySql.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource_MySql.setUrl("jdbc:mysql://" + MySQL_IP + ":" + MySQL_PORT + "/" + MySQL_DATABASE + "?characterEncoding=UTF-8");
			dataSource_MySql.setUsername(MySQL_USERNAME);
			dataSource_MySql.setPassword(MySQL_PASSWORD);
			
			//设置池参数，代码内均为默认参数
			dataSource_MySql.setInitialSize(MySQL_InitialSize);		//初始化池大小，即一开始就会有10个链接对象，
			dataSource_MySql.setMaxActive(MySQL_MaxActive);			//最大连接数，若为非正数，即无限制。空闲+活动的
			dataSource_MySql.setMaxIdle(MySQL_MaxIdle);				//最大空闲连接。超过了就被干掉
			dataSource_MySql.setMinIdle(MySQL_MinIdle);				//最小空闲连接。“备用金”最低值
			dataSource_MySql.setMaxWait(MySQL_MaxWait);				//最大等待时间（毫秒）默认表示无限等待不抛异常
			
			comm = new JdbcTemplate(dataSource_MySql);
		}
		
		if(dataSource_Oracle == null){
			dataSource_Oracle = new BasicDataSource();
			dataSource_Oracle.setDriverClassName("oracle.jdbc.OracleDriver");
			dataSource_Oracle.setUrl("jdbc:oracle:thin:@" + Oracle_IP + ":" + Oracle_PORT + "/" + Oracle_DATABASE);
			dataSource_Oracle.setUsername(Oracle_USERNAME);
			dataSource_Oracle.setPassword(Oracle_PASSWORD);
			
			//设置池参数，代码内均为默认参数
			dataSource_Oracle.setInitialSize(Oracle_InitialSize);		//初始化池大小，即一开始就会有10个链接对象，
			dataSource_Oracle.setMaxActive(Oracle_MaxActive);			//最大连接数，若为非正数，即无限制。空闲+活动的
			dataSource_Oracle.setMaxIdle(Oracle_MaxIdle);				//最大空闲连接。超过了就被干掉
			dataSource_Oracle.setMinIdle(Oracle_MinIdle);				//最小空闲连接。“备用金”最低值
			dataSource_Oracle.setMaxWait(Oracle_MaxWait);				//最大等待时间（毫秒）默认表示无限等待不抛异常
			
			orcl = new JdbcTemplate(dataSource_Oracle);
		}
	}

	
	
	/**
	 * 从已有连接池中获取连接对象 2017-07-28 21:30:26
	 * @param dataBase oracle或mysql，默认连接oracle
	 * @return
	 */
	public static Connection getConnection(String dataBase){
		BasicDataSource source =  null;
		if(DataUtils.isNull(dataBase) || dataBase.toLowerCase().equals("oracle")){
			source = dataSource_Oracle;
		}else if(dataBase.toLowerCase().equals("mysql")){
			source = dataSource_MySql;
		}else{
			throw new RuntimeException("DataSource找不到您所选则的数据库！");
		}
		
		try {
			return source.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("DataSource获取连接对象失败！");
		}
	}
	
	
	
	/**
	 * 新建连接池，并从连接池中获取连接对象
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Connection getNewConnection(){
		//获取连接池对象，配置四大连接参数
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + MySQL_IP + ":" + MySQL_PORT + "/" + MySQL_DATABASE);
		dataSource.setUsername(MySQL_USERNAME);
		dataSource.setPassword(MySQL_PASSWORD);
		
		//设置池参数，代码内均为默认参数
		dataSource.setInitialSize(MySQL_InitialSize);		//初始化池大小，即一开始就会有10个链接对象，
		dataSource.setMaxActive(MySQL_MaxActive);			//最大连接数，若为非正数，即无限制。空闲+活动的
		dataSource.setMaxIdle(MySQL_MaxIdle);				//最大空闲连接。超过了就被干掉
		dataSource.setMinIdle(MySQL_MinIdle);				//最小空闲连接。“备用金”最低值
		dataSource.setMaxWait(MySQL_MaxWait);				//最大等待时间（毫秒）默认表示无限等待不抛异常
		
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("MySQLUtils获取连接对象失败！");
		}
	}
	
}
