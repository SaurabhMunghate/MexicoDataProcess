package com.shatam.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.tequila.database.ScoreTequila;
import com.tequila.database.UpdateScore;

public class DB {
	private DB(){}

	public static final String EMPTY = "_EMPTY_";
	
	public static final String SEPARATOR_CITY_STATE = "#_separator_#";
	
	public static boolean isValueEmpty(String string){
		if(string.equals(EMPTY)){
			return true;
		}
		return false;
	}

	public final static List<String[]> resultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<String[]> rows = new ArrayList<>();
	    while (rs.next()){
	        String[]  row = new String[columns];
	        for(int i = 1; i <= columns; ++i){
	        	row[i-1] = rs.getString(i);
	        }
	        rows.add(row);
	    }
	    return rows;
	}
	
	public final static HashSet<String[]> resultSetToSet(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    HashSet<String[]> rows = new HashSet<>();
	    while (rs.next()){
	        String[]  row = new String[columns];
	        for(int i = 1; i <= columns; ++i){
	        	row[i-1] = rs.getString(i);
	        }
	        rows.add(row);
	    }
	    return rows;
	}
	
	public final static String[] resultSetToArray(ResultSet rs) throws SQLException {
	    int columns = rs.getMetaData().getColumnCount();
        String[] row = new String[columns];
	    while (rs.next()){
	        for(int i = 1; i <= columns; ++i){
	        	row[i-1] = rs.getString(i);
	        }
	    }
	    return row;
	}
	
	public final static String[] getTableHeader(ResultSet rs) throws SQLException{
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		String header[] = new String[columns];
		for(int i = 1; i <= columns; ++i){
			header[i-1] = md.getColumnLabel(i);
		}
		return header;
	}
	
	/**
	 * This method is used to get row data for particular ID as form of array of string.
	 * @param idList : Is a integer list that contain the ID.
	 * @param conn : Is a connection of current database.
	 * @return Map collection that contains key as ID, and value as its row detail for that ID in form of array of string.
	 */
	public static Map<Integer,String[]> getIdRecordDetails(List<Integer> idList, Connection conn){
		
		String query = "select * from dataset where ID=";

		Map<Integer,String[]> idRecordsMap = new HashMap<>();

		PreparedStatement pstmt = null;
		
		U.log("Loading records from database ...");
		try{
			for(int id : idList){
//				U.log(id);
				System.out.println(query+id);
				pstmt = conn.prepareStatement(query+id);
				ResultSet rs = pstmt.executeQuery();
				idRecordsMap.put(id, resultSetToArray(rs));
				rs.close();
				pstmt.close();
			}//eof for
			
		}catch(SQLException e){
			e.printStackTrace();
		}		
		U.log("All ID Records Count ::"+idRecordsMap.size());
		return idRecordsMap;
	}//EOF getIdRecordDetails()
public static Map<Integer,String[]> getIdRecordDetailsForUpdate(List<Integer> idList, Connection conn){
		
		String query = "select ID,SIC_SUB,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,_STATUS as STATUS,CONTACT_PERSON,TITLE,LATITUDE,LONGITUDE,Years_In_Biz,Emp_Count_Min,Emp_Count_Max,Annual_Sales_Vol_Min,Annual_Sales_Vol_Max from dataset where ID=";

		Map<Integer,String[]> idRecordsMap = new HashMap<>();

		PreparedStatement pstmt = null;
		
		U.log("Loading records from database ...");
		try{
			for(int id : idList){
//				U.log(id);
				pstmt = conn.prepareStatement(query+id);
				ResultSet rs = pstmt.executeQuery();
				idRecordsMap.put(id, resultSetToArray(rs));
				rs.close();
			}//eof for
			pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}		
		U.log("All ID Records Count ::"+idRecordsMap.size());
		return idRecordsMap;
	}//EOF getIdRecordDetails()
	
	/**
	 * This method is used to get unique constraints fields for a particular ID.
	 * @param idList : Is a integer list that contain the ID.
	 * @param conn : Is a connection of current database.
	 * @return Map collection that contains key as ID, and value as its unique constraints fields for that ID in form of array of string.
	 */
	public static Map<Integer,String[]> getIdRecordCompositeDetails(List<Integer> idList, Connection conn){
		String query = "select * from dataset where ID=";

		Map<Integer,String[]> idRecordsMap = new HashMap<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		U.log("Loading records from database ...");
		try{
			for(int id : idList){
				pstmt = conn.prepareStatement(query+id);
				rs = pstmt.executeQuery();
				
				idRecordsMap.put(id, new String[]{
						rs.getString(Field.SIC_SUB.toString()),
						rs.getString(Field.COMPANY_NAME.toString()),
						rs.getString(Field.ADDRESS.toString()),
						rs.getString(Field.CITY.toString()),
						rs.getString(Field.STATE.toString()),
						rs.getString(Field.CONTACT_PERSON.toString()),
					}
				);
				
			}//EOF for
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}		
		U.log("All ID Records Count ::"+idRecordsMap.size());
		return idRecordsMap;
	}//EOF getIdRecordCompositeDetails()
	
	
	/**
	 * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL statement that returns nothing, such as an SQL DDL statement.
	 * @param query is SQL query.
	 * @param conn is Connection object that hold the current database connection.
	 * @return
	 * either <br>(1) the row count for SQL Data Manipulation Language (DML) statements or<br> (2) 0 for SQL statements that return nothing
	 */
	public static int executeUpdate(String query, Connection conn){
		int i = 0;
		try{
			Statement stmt = conn.createStatement();
			i = stmt.executeUpdate(query);
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return i;
	}
	
	/**
	 * Executes the given SQL statement, which returns a single ResultSet object.
	 * @param query an SQL statement to be sent to the database, typically a static SQL SELECT statement
	 * @param conn is Connection object that hold the current database connection.
	 * @return
	 * a ResultSet object that contains the data produced by the given query; never null
	 */
	public static ResultSet executeQuery(String query, Connection conn){
		ResultSet rs = null;
		try{
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
//			stmt.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return rs;
	}
	
	public static void updateScore(ScoreTequila scoreTequila, List<Integer> idList){
		UpdateScore update = new UpdateScore();
		update.updateScore(scoreTequila, idList);
		update.disconnect();
	}
	
	public static void updateScore(ScoreTequila scoreTequila){
		UpdateScore update = new UpdateScore();
		update.updateScore(scoreTequila);
		update.disconnect();
	}
	
	public static void disconnect(Connection conn){
		try{
			if(conn != null && !conn.isClosed()){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
}
