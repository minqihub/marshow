package com.framework.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.framework.utils.PropertiesReader;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB数据库类
 * @author minqi 2017-08-16 09:50:31
 *
 */
public class MongoUtils {

	
	private static PropertiesReader property = PropertiesReader.getInstance();
	
	//MongoDB数据库链接参数
	private static String MONGO_IP = property.getProperty("MONGO_IP");
	private static String MONGO_PORT = property.getProperty("MONGO_PORT");
	private static String MONGO_DATABASE = property.getProperty("MONGO_DATABASE");
	private static String MONGO_USERNAME = property.getProperty("MONGO_USERNAME");
	private static String MONGO_PASSWORD = property.getProperty("MONGO_PASSWORD");
	
	
	
	
	static MongoClient createClient(String resource){
		MongoClient mc = null;
		try{
				mc = new MongoClient(MONGO_IP, Integer.parseInt(MONGO_PORT));
				mc = new MongoClient
					boolean auth = mc.getDB(MONGO_DATABASE).authenticate(MONGO_USERNAME, MONGO_PASSWORD.toCharArray());
					if(!auth){
						throw new Exception("MONGO连接失败！用户名或者密码错误");
					}
				
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return mc; 
	}
	
	
	
	
	
	
    public void getMongoClient( ){
        try {
            //连接到MongoDB服务。ServerAddress()两个参数分别为 服务器地址 和 端口  
            ServerAddress serverAddress = new ServerAddress(MONGO_IP, Integer.parseInt(MONGO_PORT));  
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);  
              
            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码  
            MongoCredential credential = MongoCredential.createScramSha1Credential(MONGO_USERNAME, MONGO_DATABASE, MONGO_PASSWORD.toCharArray());  
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
            credentials.add(credential);  
              
            //通过连接认证获取MongoDB连接 
            MongoClient mongoClient = new MongoClient(addrs, credentials);  
              
            //连接到数据库  
            MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DATABASE);  
            System.out.println("Connect to database successfully");  
        } catch (Exception e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
        } 
    }
	
	
	
	
	
	
	
	public static void main(String[] args) {
		MongoUtils mg = new MongoUtils();
		mg.getMongoClient();
	}

}
