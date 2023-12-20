package com.tequila.geocode;

import java.io.FileReader;
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
import com.shatam.utils.Path;
import com.shatam.utils.U;

/**
 * This class is used to generate csv from taking address, city, state and zip fields from database.<br>
 * This fields taken only on basis of its missing lat-long at database.
 * @author glady
 *
 */
public class DatasetCSVGenerator {
	private static final String FIND_QUERY ="select id,address,city,state,zip from dataset where (longitude is null or longitude='') and (latitude is null or latitude = '')";
//	private static final String FIND_QUERY ="select address,city,state,zip from dataset where (longitude is null or longitude='') and (latitude is null or latitude = '');";
	
	private static final String DB_PATH = "/home/glady/MexicoCache/database/";
	Connection conn = null;
	
	public DatasetCSVGenerator() {
		conn = DBConnection.getConnection(DB_PATH, "tequila.db");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = Path.CACHE_PATH + "Tequila_Without_LatLng_dataset_5.csv";
		
		DatasetCSVGenerator generate = new DatasetCSVGenerator();
		generate.generateDatasetCSV(fileName);
		
		generate.disconnect();
	}

	Set<String> uniqueSet = new HashSet<>();
	
	List<String[]> storedData = new ArrayList<String[]>();
	
	public void generateDatasetCSV(String fileName){
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(FIND_QUERY);
			int i = 0;
			while(rs.next()){
				String address = rs.getString("address");
				String city = rs.getString("city");
				String state = rs.getString("state");
				String zip = rs.getString("zip");

				if(address == null) address = "";
				if(city == null) city = "";
				if(state == null) state = "";
				if(zip == null) zip ="";
				
				String uniqueKey = (address.trim() + city.trim()+ state.trim()+ zip.trim()).toLowerCase();
				
//				U.log(rs.getString("id")+rs.getString("address")+rs.getString("city")+rs.getString("state")+rs.getString("zip"));
				if(uniqueSet.add(uniqueKey)){
					storedData.add(new String[]{
						rs.getString("id").trim(),
						address.trim(),
						city.trim(),
						state.trim(),
						zip.trim()
					});
					i++;
				}//eof if
			}//eof while
			
			U.log("count::::"+i);
			rs.close();
			stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(storedData.size() > 0){
			writeCSV(fileName);
		}else{
			U.log("File is not generated....");
		}
	}
	
	private void writeCSV(String fileName){
		U.log("File is generating....");
		try {
			CSVWriter writer =  new CSVWriter(new FileWriter(fileName),',');
			writer.writeAll(storedData);
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("writing is done..");
	}
	
	public void disconnect(){
		try {
			if(conn != null){
				conn.close();
			}
			if(conn.isClosed())
				U.log("Disconnect from database...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//eof disconnect()
}
