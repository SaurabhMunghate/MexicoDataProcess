package com.tequila.database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.geoboundary.Boundary;
import com.shatam.utils.U;

public class ExtractInvalidGeoBoundary {

	private static final String COUNTRY = "Mexico";

	private static final String LAT_LONG_DATABASE_PATH = "/home/glady/GeoCode/database/upendra_latlng_db/database/";
	private static final String LAT_LONG_DATABASE_NAME = "TequilaLatLong.db";
	private static final String LAT_LONG_TABLE_NAME = "latlongdata";
	
	private static final String CSV_FILE_PATH = LAT_LONG_DATABASE_PATH;
	private static final String CSV_FILE_NAME = "InvaildBoundariedData.csv";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExtractInvalidGeoBoundary process = new ExtractInvalidGeoBoundary();
		process.loadDataFromLatLongDatabaseAndValid();
	}
	
	int validLatLong = 0;
	public void loadDataFromLatLongDatabaseAndValid(){
		
		List<String[]> dataset = new ArrayList<>();
		U.log("start process...");
		String query = "SELECT ID,ADDRESS,CITY,STATE,ZIP,LATITUDE,LONGITUDE,MAP_USED FROM "+LAT_LONG_TABLE_NAME +" LIMIT 200000 OFFSET 0";

		try(
			Connection conn = DBConnection.getConnection(LAT_LONG_DATABASE_PATH, LAT_LONG_DATABASE_NAME);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			while(rs.next()){
				String address = rs.getString("ADDRESS");
				
				String state = rs.getString("STATE");
			
				String latitude = rs.getString("LATITUDE");
				String longitude = rs.getString("LONGITUDE");
				
				if(!state.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()){
					//check boundary condition for state in Specified Country
					if(!Boundary.boundaryCheckForState(state, latitude, longitude, COUNTRY)){
						dataset.add(new String[]{
								String.valueOf(validLatLong++),
								rs.getString("ID"),
								address,
								rs.getString("CITY"),
								state,
								rs.getString("ZIP"),
								latitude,
								longitude,
								rs.getString("MAP_USED")
						});
					}
				}
			}	
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Valid Latlong ::: "+validLatLong);
		U.log("Write csv invalid file here...");
		writeFile(dataset);
		U.log("Done.");
	}

	private final static String header[] = {"INDEX","ID","ADDRESS","CITY","STATE","ZIP","LATITUDE","LONGITUDE","MAP_USED"};
	
	private void writeFile(List<String[]> dataset){
		
		try(CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH+CSV_FILE_NAME),',');){
			writer.writeNext(header);
			writer.writeAll(dataset);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
