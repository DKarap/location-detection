package com.location_detection.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.location_detection.domain.RowDataBaseResultMap;


public class MySqlHelper {
	
	
    
    /**
     * Execute the given query to the DataBase and retrieve from results only the fields in the given list
     * @param conn
     * @param query
     * @return
     * @throws Exception 
     */
    public static List<RowDataBaseResultMap> executeQuerytoDB(Connection conn, String query,List<String> fieldsToSelect) throws Exception{
    	//List with rowDataBaseResultsMap
    	List<RowDataBaseResultMap> resultsList = new ArrayList<RowDataBaseResultMap>();
    	
    	Statement stmt = null;
    	ResultSet rs = null;
		try {
					
			//execute query
			stmt = conn.createStatement();
		    rs = stmt.executeQuery(query);
		    
		    //Get results
		    while (rs.next ()){
		    	HashMap<String,String> rowResult = new HashMap<String,String>();
		    	for(String fieldName : fieldsToSelect){
		    		if(fieldName.equals("max")){
		    			//we assume that is the only field, we can check the fieldsToSelect current oindex and use that instead
		    			rowResult.put("max", Integer.toString(rs.getInt(1)));
		    		}
		    		else{
				        String fieldValue = rs.getString(fieldName);
				        rowResult.put(fieldName, fieldValue);	
		    		}
		    	}
		    	resultsList.add(new RowDataBaseResultMap(rowResult));
		    }
		    
			
			//return results
			return resultsList;
		} catch (Exception e) {
			throw e;
		}finally{
	        try {
	        	if(rs!=null)
	        		rs.close();
	        	if(stmt!=null)
	        		stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

    }

	/**
	 * Get a connection from MySql
	 * @return
	 */
	public static Connection getMySqlConnection(String database, String usr, String psw){
		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/"+database;
			myUrl += "?zeroDateTimeBehavior=convertToNull";
			Class.forName(myDriver);
			Connection connection = DriverManager.getConnection(myUrl, usr,psw);
			
			return connection;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Close given connection from MySql
	 * @return
	 */
	public static void closeMySqlConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
		}
	}	
	


}
