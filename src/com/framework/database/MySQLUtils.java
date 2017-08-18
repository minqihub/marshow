package com.framework.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.framework.utils.SysLog;

/**
 * MySQL数据库类
 * 	CREATE USER 'minqi'@'%' IDENTIFIED by 'asdf1234';
 *	GRANT ALL ON *.* TO 'minqi'@'%';
 * @author minqi 2017-02-17
 * 
 *	commons-pool-1.5.6.jar
 *	mysql-connector-java-5.1.7-bin.jar
 *	commons-dbcp-1.4.jar
 */
public class MySQLUtils {
	

	/**
	 * 执行update\insert语句（语句内回滚）
	 * @param sql
	 * @return 受影响的行数
	 * @throws SQLException 获取连接失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int sqlExecute(JdbcTemplate jdbcTemp, String sql) throws SQLException{
		Connection conn = jdbcTemp.getDataSource().getConnection();
		Statement sta = null;
		int result = 0;
		
		try{
			conn.setAutoCommit(false);		//开启事务，不自动提交
			sta = conn.createStatement();
			try {
				result = sta.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("执行sql语句失败！");
			}
			conn.commit();								//提交事务			
			if(sta != null) sta.close();				//避免空指针问题，所以要判断非空
			if(conn != null) conn.close();				//一定要关，不关就死，”软病”
			
			SysLog.log(MySQLUtils.class, "执行sql成功，影响行数："+result);
		}catch(Exception e){
			try {
				conn.rollback();			//出了异常就回滚
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("执行sql语句失败，且回滚失败！");
			}
		}
		return result;
	}
	
	/**
	 * 带参执行sql语句
	 * @param jdbcTemp
	 * @param sql 例：INSERT INTO student(id,name,sex,age)VALUES(?id,?name,?sex,?age)
	 * @param map {id=123, name=zhangsan, ...}
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int sqlExecuteMap(JdbcTemplate jdbcTemp, String sql, Map map) throws SQLException{
		
		sql = SQLConvertor.format(sql, map);
		return sqlExecute(jdbcTemp, sql);
	}
	
	
	/**
	 * 查询多条
	 * @param jdbcTemp
	 * @param sql
	 * @return List集合
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List sqlQueryForList(JdbcTemplate jdbcTemp, String sql){
		
		return jdbcTemp.queryForList(sql);
	}
	
	/**
	 * 查询单条
	 * @param jdbcTemp
	 * @param sql
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map sqlQueryForMap(JdbcTemplate jdbcTemp, String sql){
		
		return jdbcTemp.queryForMap(sql);
	}
	

	
	
	
	public static void main(String[] args) {
		JdbcTemplate community = DataSource.community;
		
		MySQLUtils mySql = new MySQLUtils();
		
		String sql = "INSERT INTO student(id,name,sex,age)VALUES(?id,?name,?sex,?age)";
		
		Map map = new HashMap();
		map.put("id", 10066);
		map.put("name", "王二小");
		map.put("sex", "男");
		map.put("age", 56);

		try {
			mySql.sqlExecuteMap(community, sql, map);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
