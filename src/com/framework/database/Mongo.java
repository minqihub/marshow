package com.framework.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.framework.utils.DataUtils;
import com.framework.utils.Json;

import com.framework.utils.PropertiesReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * MongoDB数据库类
 * @author minqi 2017-02-17
 *
 */
public class Mongo {
	
	private static PropertiesReader property = PropertiesReader.getInstance();
	
	//MongoDB数据库链接参数
	private static String MONGO_IP = property.getProperty("MONGO_IP");
	private static String MONGO_PORT = property.getProperty("MONGO_PORT");
	private static String MONGO_DATABASE = property.getProperty("MONGO_DATABASE");
	private static String MONGO_USERNAME = property.getProperty("MONGO_USERNAME");
	private static String MONGO_PASSWORD = property.getProperty("MONGO_PASSWORD");
	
	
	static MongoClient mongoClient = null;
	static DB db = null;
	
	
	public static void main(String[] args) {
		Mongo mg = new Mongo();
		Map queryMap = new HashMap();
		queryMap.put("zcxx01", 658);
		Map resultMap = null;
		try {
			resultMap = mg.findOne("W_User", queryMap, null);
			System.out.println(resultMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 创建新的链接对象
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static MongoClient createClient(){
		try{
		    //连接到 mongodb 服务
	        mongoClient = new MongoClient(MONGO_IP, Integer.parseInt(MONGO_PORT));
	         
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return mongoClient; 
	} 
	
	/**
	 * 获取链接对象
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	static MongoClient getClient(){
		if(mongoClient == null){
			mongoClient = createClient();
		}
		return mongoClient;
	}
	
	/**
	 * 获取链接的数据库名
	 */
	public static DB getDB() {
		return getClient().getDB(MONGO_DATABASE);
	}

	
	/**
	 * @todo 查询总数
	 * @param collection
	 * @param queryMap
	 * @return
	 */
	public int findCount(String collection,Map queryMap){
		int count = 0;
		DBCollection dbCollection = getDB().getCollection(collection);
		DBObject query = !DataUtils.isNull(queryMap)? new BasicDBObject(queryMap): new BasicDBObject();
		count = (int) dbCollection.count(query);
		return count;
	}
	
	/**
	 * 查询一行记录
	 * @param collection 集合
	 * @param queryMap 查询条件
	 * @param KeyMap 查询字段
	 * @param orderMap 排序
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map findOne(String collection,Map queryMap,Map keysMap) throws Exception {
		DBCollection dbCollection = getDB().getCollection(collection);
		DBObject query = !DataUtils.isNull(queryMap)? new BasicDBObject(queryMap): new BasicDBObject();
		DBObject keys = !DataUtils.isNull(keysMap)? new BasicDBObject(keysMap): new BasicDBObject("_id", 0);
		DBObject result = dbCollection.findOne(query, keys);
		if(!DataUtils.isNull(result)){
			return result.toMap();
		}
		return new HashMap();
	}
	
	/**
	 * 查询记录
	 * @param collection 集合
	 * @param queryMap 查询条件，如无则查询所有
	 * @param KeyMap 查询字段
	 * @param orderMap 排序
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List find(String collection, Map queryMap, Map KeyMap, Map orderMap) throws Exception {
        DBCollection dbCollection = getDB().getCollection(collection);
		DBObject query = !DataUtils.isNull(queryMap)? new BasicDBObject(queryMap): new BasicDBObject();
        DBObject keys = !DataUtils.isNull(KeyMap)? new BasicDBObject(KeyMap): new BasicDBObject("_id", 0);
        DBCursor dbc = dbCollection.find(query, keys);
        
        if (!DataUtils.isNull(orderMap)) {
        	DBObject orderBy = new BasicDBObject(orderMap);
            dbc.sort(orderBy);
        }
        return dbc.toArray();
    }
	
	/**
	 * 查询最大记录时使用
	 * @param collection
	 * @param queryMap
	 * @param KeyMap
	 * @param orderMap
	 * @param skip
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List find(String collection, Map queryMap, Map KeyMap, Map orderMap, int skip, int limit) throws Exception {
		DBCollection dbCollection = getDB().getCollection(collection);
		DBObject query = !DataUtils.isNull(queryMap)? new BasicDBObject(queryMap): new BasicDBObject();
		DBObject keys = !DataUtils.isNull(KeyMap)? new BasicDBObject(KeyMap): new BasicDBObject("_id", 0);
		DBCursor dbc = dbCollection.find(query, keys).skip(skip).limit(limit);
		
		if (!DataUtils.isNull(orderMap)) {
			DBObject orderBy = new BasicDBObject(orderMap);
			dbc.sort(orderBy);
		}
		return dbc.toArray();
	}
	
	/**
	 * 
	 * @param collection
	 * @param key
	 * @param queryMap
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("rawtypes")
	public List distinct(String collection,String key,Map queryMap) throws Exception {
        DBCollection dbCollection = getDB().getCollection(collection);
        DBObject query = new BasicDBObject(queryMap);
        List list = DataUtils.isNull(queryMap)? dbCollection.distinct(key): dbCollection.distinct(key, query);
        return list;
    }
    
    /**
     * 
     * @param collection
     * @param insertMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public WriteResult insert(String collection, Map insertMap) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	DBObject insert = new BasicDBObject(DataUtils.replaceJsonNull(insertMap));
    	insert.put("ts", DataUtils.getSysTime());
    	return dbCollection.insert(insert);
    }
    
    /**
     * 
     * @param collection
     * @param list
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public WriteResult insert(String collection,List list) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	List<DBObject> insertList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Map dbo = Json.toJO(list.get(i));
			dbo.put("ts", DataUtils.getSysTime());
			insertList.add(new BasicDBObject(dbo));
    	}
    	return dbCollection.insert(insertList);
    }
    
    /**
     * 
     * @param collection
     * @param queryMap
     * @param updateMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public WriteResult update(String collection,Map queryMap,Map updateMap) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	DBObject query = new BasicDBObject(queryMap);
    	updateMap.put("ts", DataUtils.getSysTime());
    	DBObject update = new BasicDBObject("$set", DataUtils.replaceJsonNull(updateMap));
    	return dbCollection.update(query, update);
    }
    
    /**
     * 
     * @param collection
     * @param queryMap
     * @param updateMap
     * @param multi
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public WriteResult update(String collection,Map queryMap,Map updateMap,boolean multi) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	DBObject query = new BasicDBObject(queryMap);
    	updateMap.put("ts", DataUtils.getSysTime());
    	DBObject update = new BasicDBObject("$set", updateMap);
    	return dbCollection.update(query, update, false, multi);
    }
    
    /**
     * 
     * @param collection
     * @param removeMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes" })
    public WriteResult remove(String collection,Map removeMap) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	DBObject remove = new BasicDBObject(removeMap);
    	return dbCollection.remove(remove);
    }
    
    /**
     * 
     * @param collection
     * @param queryMap
     * @param updateMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes" })
    public WriteResult updateInc(String collection,Map queryMap,Map updateMap) throws Exception {
    	DBCollection dbCollection = getDB().getCollection(collection);
    	DBObject query = new BasicDBObject(queryMap);
    	DBObject update = new BasicDBObject("$inc", updateMap);
    	//更新时间戳
    	update(collection,queryMap,new HashMap());
    	return dbCollection.update(query, update);
    } 
    
	
//	public static void main(String[] args) {
//			try {
//				// 实例化Mongo对象，连接27017端口
//				Mongo mongo = new Mongo("localhost", 27017);
//				// 连接名为yourdb的数据库，假如数据库不存在的话，mongodb会自动建立
//				DB db = mongo.getDB("test");
//				// Get collection from MongoDB, database named "yourDB"
//				// 从Mongodb中获得名为yourColleection的数据集合，如果该数据集合不存在，Mongodb会为其新建立
//				DBCollection collection = db.getCollection("test1");
//				// 使用BasicDBObject对象创建一个mongodb的document,并给予赋值。
//				BasicDBObject document = new BasicDBObject();
//				//document.put("id", 1001);
//				//document.put("msg", "hello world mongoDB in Java");
//				// 将新建立的document保存到collection中去
//				//collection.insert(document);
//				// 创建要查询的document
//				BasicDBObject searchQuery = new BasicDBObject();
//				searchQuery.put("name", "chen");
//				// 使用collection的find方法查找document
//				DBCursor cursor = collection.find(searchQuery);
//				// 循环输出结果
//				while (cursor.hasNext()) {
//					System.out.println(cursor.next());
//				}
//				System.out.println("Hello World");
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			} catch (MongoException e) {
//				e.printStackTrace();
//			}
//	}

}
