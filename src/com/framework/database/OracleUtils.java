package com.framework.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleTypes;

import org.junit.Test;


/**
 * Oracle数据库类
 * @author minqi 2016-11-21
 *
 */
public class OracleUtils extends MySQLUtils{
	
	public static void find(){
		
	}
	
	
	
	
	/**
	 * 调用存储过程
	 * @throws SQLException 
	 */
	@Test
	public void deployProcedure() throws SQLException{
		Connection conn = DataSource.getConnection("oracle");
		CallableStatement call = null;
		String sql = "{call queryInfo(?,?,?,?)}";
		try {
			call = conn.prepareCall(sql);
			
			//对于in参数：赋值
			call.setInt(1, 7839);
			
			//对于out参数：申明
			call.registerOutParameter(2, OracleTypes.VARCHAR);
			call.registerOutParameter(3, OracleTypes.VARCHAR);
			call.registerOutParameter(4, OracleTypes.NUMBER);
			
			//执行
			call.execute();
			
			//取出返回值
			String name = call.getString(2);
			String job = call.getString(3);
			double sal = call.getDouble(4);
			
			System.out.println(name+job+sal);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) conn.close();
		}
		
	}
	
	
	/**
	 * 调用存储过程
	 * @throws SQLException 
	 */
	@Test
	public void deployFunction() throws SQLException{
		Connection conn = DataSource.getConnection("oracle");
		CallableStatement call = null;
		String sql = "{?=call queryIncome(?)}";
		try {
			call = conn.prepareCall(sql);			
			
			//对于out参数：申明
			call.registerOutParameter(1, OracleTypes.NUMBER);

			//对于in参数：赋值
			call.setInt(2, 7839);
			
			//执行
			call.execute();
			
			//取出返回值
			double inCome = call.getDouble(1);
			
			System.out.println(inCome);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) conn.close();
		}
		
	}
	
	/**
	 * 调用包体形式的存储过程
	 */
	public void deployPackage(){
		//TODO
		
	}
	
	
}
