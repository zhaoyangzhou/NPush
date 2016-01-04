package com.push.m.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.push.m.utils.PropertyUtil;

public class DBHelper {  
	
	private static final Logger logger = Logger.getLogger(DBHelper.class); 
	
	public static Connection getConnection() throws SQLException {
		String dbDriver = PropertyUtil.getValue("config", "dbDriver");
        String dbUrl = PropertyUtil.getValue("config", "dbUrl");
        String dbUserName = PropertyUtil.getValue("config", "dbUserName");
        String dbPassword = PropertyUtil.getValue("config", "dbPassword");
        
        Connection conn = null;
		
        try {  
        	//1.注册驱动  
            Class.forName(dbDriver);  
            //2.创建数据库的连接  
            conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword); 
        } catch (ClassNotFoundException e) {  
        	logger.error(e.getMessage());
        }  
        return conn;
	}
	
	public static int saveOrUpdate(Connection conn, String sql, Object[] params) {
		PreparedStatement preStmt = null;
		int count = 0;
		
		try {
			preStmt = conn.prepareStatement(sql);
			for(int i = 0, len = params.length; i < len; i++) {
				if(params[i] instanceof String) {
					preStmt.setString(i+1, (String)params[i]);
				} else if(params[i] instanceof Long) {
					preStmt.setLong(i+1, (Long)params[i]);
				} else if(params[i] instanceof Float) {
					preStmt.setFloat(i+1, (Float)params[i]);
				}
			}
			count = preStmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if(preStmt != null)
					preStmt.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return count;
	}
	
	public static List<Map> query(Connection conn, String sql, Object[] params) {
		PreparedStatement preStmt = null;
		ResultSet result = null;
		List<Map> list =  new ArrayList<Map>();
		
		try {
			preStmt = conn.prepareStatement(sql);
			if(params != null) {
				for(int i = 0, len = params.length; i < len; i++) {
					if(params[i] instanceof String) {
						preStmt.setString(i+1, (String)params[i]);
					} else if(params[i] instanceof Long) {
						preStmt.setLong(i+1, (Long)params[i]);
					}
				}
			}
			
			result = preStmt.executeQuery();
			ResultSetMetaData rsmd = result.getMetaData(); //获得列集
			int count = rsmd.getColumnCount(); //获得列的个数
		    while(result.next()) {
		    	Map map = new HashMap();
		    	for(int i = 0; i < count; i++) {
		    		String columnName = rsmd.getColumnName(i+1);
		    		map.put(columnName, result.getObject(columnName));
			    }
		    	list.add(map);
		    }
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if(preStmt != null)
					preStmt.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return list;
	}
	  
}  
