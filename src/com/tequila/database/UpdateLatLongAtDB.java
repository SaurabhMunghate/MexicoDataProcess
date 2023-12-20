package com.tequila.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



import com.database.connection.DBConnection;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.geoboundary.Boundary;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.tequila.database.corrector.UpdateReportAtTable;

public class UpdateLatLongAtDB {

	private static final String EMPTY = "";
	
	private static final String COUNTRY = "Mexico";
	
	private static final String LAT_LONG_DATABASE_PATH = "/home/glady/GeoCode/database/";
	private static final String LAT_LONG_DATABASE_NAME = "TequilaLatLong.db";
	private static final String LAT_LONG_TABLE_NAME = "latlongdata";
	
	
	private static final String TEQUILA_DATABASE_NAME = "tequila.db";
	private static final String TEQUILA_TABLE_NAME = "dataset";
	
	private List<Integer> idList = new ArrayList<>();
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		UpdateLatLongAtDB process = new UpdateLatLongAtDB();
		
//		process.loadDataFromLatLongDatabaseAndValid();  //to find latlong & check boundary from latlong db 	
		
		process.prepareUpdationLatLongRecordAtDB();
		
		long endTime = System.currentTimeMillis();
		U.log("Total execution time in ms :"+(endTime-startTime));
	}
	
	int validLatLong = 0;
	/**
	 * This method is used to find records from  TequilaLatLong database.<br> This database has contain the records of address along with its lat-long data<br>
	 * and it map value where it extract from map apis. 
	 */
	public void loadDataFromLatLongDatabaseAndValid(){
//		Set<String[]> dataset = new HashSet<>();

		Set<String[]>  invalidBoundarySet = new HashSet<>();
		
		U.log("start process...");
		String query = "SELECT ID,ADDRESS,CITY,STATE,ZIP,LATITUDE,LONGITUDE,MAP_USED FROM "+LAT_LONG_TABLE_NAME;

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
					if(Boundary.boundaryCheckForState(state, latitude, longitude, COUNTRY)){
						/*dataset.add(new String[]{
								address,
								rs.getString("CITY"),
								state,
								rs.getString("ZIP"),
								latitude,
								longitude,
						});*/
						validLatLong++;
					}else{
						invalidBoundarySet.add(new String[]{rs.getString("ID").trim(), address.trim()});
					}
				}
			}	
			
			removeLatLongAtDB(invalidBoundarySet,conn);
			//generateCsvForValidRecord(dataset, datasetFilePath);
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Valid Latlong ::: "+validLatLong);
		//U.log("Valid Record :::: "+dataset.size());
		U.log("Invalid Record :: "+invalidBoundarySet.size());
	}
	
	/**
	 * This method is used to update those records have wrong boundary condition for specified state.
	 * @param invalidBoundarySet
	 * @param conn
	 */
	private void removeLatLongAtDB(Set<String[]> invalidBoundarySet, Connection conn){
		String updateQuery = "UPDATE "+LAT_LONG_TABLE_NAME 
				+" SET LATITUDE=?, LONGITUDE=?, MAP_USED=? WHERE ID=? AND ADDRESS=?";
		
		PreparedStatement pstmt = null;
		int updateCount[];
		int i = 0;
		try {
			U.log("Start updating at database...");
			pstmt = conn.prepareStatement(updateQuery);
			Iterator<String[]> itr = invalidBoundarySet.iterator();
			conn.setAutoCommit(false);
			
			while (itr.hasNext()) {
				String[] dataset = itr.next();
				pstmt.setString(1, EMPTY);
				pstmt.setString(2, EMPTY);
				pstmt.setString(3, EMPTY);
				pstmt.setString(4, dataset[0]);
				pstmt.setString(5, dataset[1]);
				pstmt.addBatch();

				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length +"\t" + i);
			System.out.println("Commit the batch");
			U.log("Total update records : "+i);
		}catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			conn.commit();
			conn.setAutoCommit(true);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to update lat-long at database
	 */
	private void prepareUpdationLatLongRecordAtDB(){
		int foundRecord=0;
		Map<String, String[]> validBoundaryMap = loadLatLongFromDatabase();

//		Map<String, String[]> datasetMap = loadMapRecordFromDatabase();
		
		MultiMap<String, String[]> multiMapDataset =  loadMultiMapRecordFromDatabase();
		
		Set<String[]> dataset = new HashSet<String[]>(); 
				
/*		if(validBoundaryMap.size() > 0 && datasetMap.size() > 0){
			for(Map.Entry<String, String[]> entry : validBoundaryMap.entrySet()){
				
				if(datasetMap.containsKey(entry.getKey())){
					foundRecord++;
//					U.log("Record::"+Arrays.toString(datasetMap.get(entry.getKey()))+"\t>>LatLng:::"+Arrays.toString(validBoundaryMap.get(entry.getKey())));
					dataset.add(new String[]{
						datasetMap.get(entry.getKey())[0],//ID
						validBoundaryMap.get(entry.getKey())[4], //longitude
						validBoundaryMap.get(entry.getKey())[5] //latitude
					});
				}
			}
		}
*/		
		if(validBoundaryMap.size() > 0 && multiMapDataset.size() > 0){	
			
			for(Map.Entry<String, String[]> entry : validBoundaryMap.entrySet()){
				
				if(multiMapDataset.containsKey(entry.getKey())){
					
					Collection<String[]> keys = multiMapDataset.get(entry.getKey());
		
					for(String[] key : keys){
//						U.log("Record::"+Arrays.toString(key)+"\t>>LatLng:::"+Arrays.toString(validBoundaryMap.get(entry.getKey())));
						dataset.add(new String[]{
							key[0],//ID
							validBoundaryMap.get(entry.getKey())[4], //longitude
							validBoundaryMap.get(entry.getKey())[5] //latitude
						});
						
						idList.add(Integer.parseInt(key[0].trim())); //add Id at list here..
					}
				}//eof if
			}
		}
		
//		U.log("Found record in datasetMap ::"+foundRecord);
		U.log("Size of dataset for update lat-long ::"+dataset.size());
		U.log("Sending for updation at database....");
		//send for updation
		updateLatLongAtDB(dataset);
	}


	
	private void updateLatLongAtDB(Set<String[]> latLongDataset){
		
		//String updateQuery = "UPDATE "+TEQUILA_TABLE_NAME+" SET LONGITUDE=?, LATITUDE=? WHERE ID=? AND LONGITUDE=?";
		String updateQuery = "UPDATE "+TEQUILA_TABLE_NAME+" SET LONGITUDE=?, LATITUDE=?, UPDATED_DATE=? WHERE ID=? AND LONGITUDE IS NULL";
		
		Connection conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, TEQUILA_DATABASE_NAME);
		PreparedStatement pstmt = null;
		int updateCount[];
		int i = 0;
		try{
			
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			
			Iterator<String[]> itr = latLongDataset.iterator();
			
			
			
			U.log("start updating ...........");
			while (itr.hasNext()) {
				String[] dataset = itr.next();
				
				pstmt.setString(1, dataset[1] ); //longitude
				pstmt.setString(2, dataset[2]); // latitude
				pstmt.setString(3, U.getTodayDate()); // updated_date
				pstmt.setString(4, dataset[0]); //ID
	//			pstmt.setString(4, EMPTY); //longitude  //EMPTY
				pstmt.addBatch();

//				U.log(dataset[0]);
				if ((++i % 10000) == 0) {
					
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length +"\t" + i);			
			conn.commit();
			System.out.println("Commit the batch");
			conn.setAutoCommit(true);
			U.log("Total update records : "+i);

			if(conn != null){
				if(pstmt != null)pstmt.close();
				conn.close();
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}
		/*
		 * inserted records at updatedDataset
		 */
		if(idList.size() > 0){
			Set<Integer> idSet = new HashSet<>(idList);
			idList.clear();
			idList.addAll(idSet);
			Collections.sort(idList);
			idSet.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, TEQUILA_DATABASE_NAME);
			report.startExtractingFromDB(idList);
		}
		
	}	
	
	

	
	private Map<String, String[]> loadLatLongFromDatabase(){
		
		Map<String, String[]>  validBoundaryMap = new HashMap<>();
		
		U.log("start process from "+LAT_LONG_DATABASE_NAME);
		
		String query = "SELECT ADDRESS,CITY,STATE,ZIP,LATITUDE,LONGITUDE FROM "+LAT_LONG_TABLE_NAME +" WHERE LATITUDE !=\"\" AND LONGITUDE !=\"\"";

		try(
			Connection conn = DBConnection.getConnection(LAT_LONG_DATABASE_PATH, LAT_LONG_DATABASE_NAME);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			while(rs.next()){
				
				String uniqueKey = (rs.getString("ADDRESS").trim()+rs.getString("CITY").trim()+rs.getString("STATE").trim()+rs.getString("ZIP").trim()).toLowerCase();
				
				String longitude = rs.getString("LONGITUDE");
				String latitude = rs.getString("LATITUDE");
				
				if(!latitude.isEmpty() && !longitude.isEmpty()){
					validBoundaryMap.put(uniqueKey ,
							new String[]{
								rs.getString("ADDRESS"),
								rs.getString("CITY"),
								rs.getString("STATE"),
								rs.getString("ZIP"),
								longitude,
								latitude
							}
					);
				}
			}//eof while
		
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Valid Lat-Long Record :: "+validBoundaryMap.size());
		return validBoundaryMap;
	}
	

	
	private MultiMap<String, String[]>  loadMultiMapRecordFromDatabase(){
	
		MultiMap<String, String[]> multiMapDataset =  new MultiValueMap<String, String[]>();

		U.log("start process from "+TEQUILA_DATABASE_NAME);
		
		//String query = "SELECT ID,ADDRESS,CITY,STATE,ZIP FROM "+TEQUILA_TABLE_NAME +" WHERE LATITUDE =\"\" AND LONGITUDE =\"\"";
		
		String query = "SELECT ID,ADDRESS,CITY,STATE,ZIP FROM "+TEQUILA_TABLE_NAME +" WHERE LATITUDE IS NULL AND LONGITUDE IS NULL";

		try(
			Connection conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, TEQUILA_DATABASE_NAME);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			while(rs.next()){
				String address = rs.getString("ADDRESS");
				String city = rs.getString("CITY");
				String state = rs.getString("STATE");
				String zip = rs.getString("ZIP");
				
				if(address == null) address = "";
				if(city == null) city = "";
				if(state == null) state = "";
				if(zip == null) zip ="";
				
				String uniqueKey = (address.trim() + city.trim()+ state.trim()+ zip.trim()).toLowerCase();
				
//				String uniqueKey = (rs.getString("ADDRESS").trim()+rs.getString("CITY").trim()+rs.getString("STATE").trim()+rs.getString("ZIP").trim()).toLowerCase();
				
				multiMapDataset.put(uniqueKey ,
						new String[]{
							rs.getString("ID"),
							rs.getString("ADDRESS"),
							rs.getString("CITY"),
							rs.getString("STATE"),
							rs.getString("ZIP"),
						}
				);
			}//eof while
		
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Dataset Record :: "+multiMapDataset.size());
		return multiMapDataset;
	}
	
/*	private Map<String, String[]> loadMapRecordFromDatabase(){
		
		Map<String, String[]>  datasetMap = new HashMap<>();
		
		U.log("start process from "+TEQUILA_DATABASE_NAME);
		
		String query = "SELECT ID,ADDRESS,CITY,STATE,ZIP FROM "+TEQUILA_TABLE_NAME +" WHERE LATITUDE =\"\" AND LONGITUDE =\"\"";

		try(
			Connection conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, TEQUILA_DATABASE_NAME);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			while(rs.next()){
				
				String uniqueKey = (rs.getString("ADDRESS").trim()+rs.getString("CITY").trim()+rs.getString("STATE").trim()+rs.getString("ZIP").trim()).toLowerCase();
				
				datasetMap.put(uniqueKey ,
						new String[]{
							rs.getString("ID"),
							rs.getString("ADDRESS"),
							rs.getString("CITY"),
							rs.getString("STATE"),
							rs.getString("ZIP"),
						}
				);
			}//eof while
		
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Dataset Record :: "+datasetMap.size());
		return datasetMap;
	}
*/
}
