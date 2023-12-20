package com.tequila.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class TequilaAtDB {
	
	private static final String createTableQuery = "CREATE TABLE dataset (ID Integer primary key autoincrement,INDUSTRY_SECTOR text,SPANISH_INDUSTRY_SECTOR text,SIC_MAJOR int,SIC_SUB int,PRODUCT_DESC text,SPANISH_PRODUCT_DESC text,COMPANY_NAME text,ADDRESS text,NEIGHBORHOOD text,CITY text,STATE text,ZIP text,PHONE text,FAX text,URL text,EMAIL text,CONTACT_PERSON text,TITLE text,ANNUAL_SALES_VOL_MIN int,ANNUAL_SALES_VOL_MAX int,EMP_COUNT_MIN int,EMP_COUNT_MAX int,YEARS_IN_BIZ datetime,LONGITUDE double,LATITUDE double,_SCORE int,_SOURCE_URL text,_STATUS text,_LAST_UPDATED_BY text,_LAST_UPDATED text)";
	
	private static final String indexTableQuery = "CREATE UNIQUE INDEX SHATAM_INDEX ON dataset (SIC_SUB,COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON)";
	
	private static final String DB_PATH = "/home/glady/MexicoCache/database/"; //"/home/glady/MexicoCache/database/data/";
	private static final String DB_NAME = "tequila.db";
	
	private static String tableName = "dataset";
	private static String indexName = "SHATAM_INDEX";
	
	Connection conn = null;
	private TequilaAtDB(){
		conn = DBConnection.getConnection(DB_PATH, DB_NAME);
	}
	
	public static void main(String[] args) {
		TequilaAtDB tequila = new TequilaAtDB();
//		tequila.validateTequila();
		
//		tequila.executeQuery("update dataset set ANNUAL_SALES_VOL_MIN=ANNUAL_SALES_VOL_MAX,ANNUAL_SALES_VOL_MAX=ANNUAL_SALES_VOL_MIN  where ANNUAL_SALES_VOL_MIN=1000000 and ANNUAL_SALES_VOL_MAX=500000");
		tequila.disconnect();
	}
	
	public void executeQuery(String query){
		try(Statement stmt = conn.createStatement();)
		{
			boolean status = stmt.execute(query);
			U.log("execute status ::"+status);
			U.log("Total update count ::"+stmt.getUpdateCount());
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void validateTequila(){
		try(
		Statement stmt = conn.createStatement();)
		{
			if(!checkTable()){
				stmt.execute(createTableQuery);
				U.log(tableName+" is created.");
			}else{
				U.log(tableName+" is found.");
			}
			if(!checkIndex()){
				stmt.execute(indexTableQuery);
				U.log(indexName+"is created.");
			}else{
				U.log(indexName+"is found.");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	private boolean checkTable(){
		
		String query = "select type,name from sqlite_master";
		boolean found = false;
		try(
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				String type = rs.getString("type").trim();
				String name = rs.getString("name").trim();
				if(type.equals("table") && name.equals(tableName)){
					found = true;
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}
	
	private boolean checkIndex(){
		
		String query = "select type,name from sqlite_master";
		boolean found = false;
		try(
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{	
			while(rs.next()){
				String type = rs.getString("type").trim();
				String name = rs.getString("name").trim();
				if(type.equals("index") && name.equals(indexName)){
					found = true;
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}
	
	private void disconnect(){
		try{
			if(!conn.isClosed() && conn != null){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
}
