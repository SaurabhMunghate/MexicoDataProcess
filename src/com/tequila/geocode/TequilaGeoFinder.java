package com.tequila.geocode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class TequilaGeoFinder {

	private final String tableName = "latlongdata"; 
	Connection conn = null;
	public TequilaGeoFinder(){
		conn =DBConnection.getConnection(Path.DB_PATH, "TequilaLatLong.db");
	}
	
	public static void main(String[] args) {
		
		Scanner scaner = new Scanner(System.in);
		System.out.print("Enter start index ::");
		int start = scaner.nextInt();
		System.out.print("Enter end index ::");
		int end = scaner.nextInt();		
		scaner.close();
		
		U.log("start::"+start+" end::"+end);
		
		TequilaGeoFinder geoFinder = new TequilaGeoFinder();
		geoFinder.checkTableInDatabase();
		
//		String fileName = Path.CACHE_PATH + "Tequila_Without_LatLng.csv";
		
		String fileName = Path.CACHE_PATH + "Tequila_Without_LatLng_dataset_5.csv";
		
/*		GeoLocationExtraction geoLocationExtraction = new GeoLocationExtraction(fileName,"TequilaLatLong.db","latlongdata");
		geoLocationExtraction.start(start,end);
*/		
		LatLongExtraction latLongExtraction =  new LatLongExtraction(fileName,"TequilaLatLong.db","latlongdata");
		latLongExtraction.start(start, end);
	}
	
	public void checkTableInDatabase(){
		String query = "select type, name from sqlite_master";
		boolean found = false;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String type = rs.getString("type").trim();
				String name = rs.getString("name").trim();
				if(type.equals("table") && name.equals(tableName)){
					found = true;
					break;
				}
			}
			rs.close();
			
			if(!found){
				createTableInDB(stmt);
			}else{
				U.log("Table is found..");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn != null){
					conn.close();
				}
				if(conn.isClosed())
					U.log("Disconnect from database...");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createTableInDB(Statement stmt) throws SQLException{
		
		String createQuery = "CREATE TABLE latlongdata(ID INTEGER,ADDRESS TEXT,CITY TEXT,STATE TEXT,ZIP TEXT,LATITUDE TEXT,LONGITUDE TEXT,MAP_USED TEXT)";
		
		if(stmt.isClosed() || stmt == null)
			stmt = conn.createStatement();
		
		boolean status = stmt.execute(createQuery);
/*		if(status){
			U.log("Table is created.");
		}else{
			U.log("Table is either present or no operation perform..");
		}
*/	}
	


}
