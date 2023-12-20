package com.tequila.database.corrector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.database.connection.DBConnection;
import com.shatam.utils.DB;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class UpdateDeleteAtTequila {

	private static Connection conn = null;
	
	public static void connect() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
		U.log("Connection established ...");
	}
	
	/**
	 * This method is used to update correct city with uncorrected with at database.
	 * @param updateCityDataset
	 * : <b>updateCityDataset</b> is Set of arrays of string <br>
	 * Array of String content the size is 3.<br>
	 * index 0 : ID<br>
	 * index 1 : Correct City<br>
	 * index 2 : Incorrect City<br>
	 * @return List of integer containing updated ID's at database.
	 */
	public static List<Integer> updateIncorrectCity(Set<String[]> updateCityDataset) {
		
		List<Integer> updatedIdList = new ArrayList<>();
		
		U.log("Start updating correct city here...");
		String updateQuery="update dataset set CITY=?, UPDATED_DATE=? where CITY=? and ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for (String[] data : updateCityDataset) {
				
				pstmt.setString(1, U.toTitleCase(data[1]).replace(" - ", "-")); //Correct City
				pstmt.setString(2, U.getTodayDate());  // Incorrect City
				pstmt.setString(3, data[2]);  // Incorrect City
				pstmt.setString(4, data[0]);  //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				updatedIdList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");
			pstmt.close();
			
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updatedIdList;
	}//eof updateIncorrectCity()
	
	/**
	 * This method is used to update correct state as well as city with uncorrected with at database.
	 * @param updateDatasetForCityState is Set of arrays of string. <br>
	 * Array of String content the size is 4.<br>
	 * index 0 : ID<br>
	 * index 1 : Correct City<br>
	 * index 2 : Incorrect City<br>
	 * index 3 : Correct State<br>
	 * @return List of integer containing updated ID's at database.
	 */
	public static List<Integer> updateIncorrectCityState(Set<String[]> updateDatasetForCityState) {
		
		List<Integer> updatedIdList = new ArrayList<>();
		
		U.log("Start updating correct city here...");
		String updateQuery="update dataset set CITY=?, STATE=?, UPDATED_DATE=? where CITY=? and ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for (String[] data : updateDatasetForCityState) {
				
				pstmt.setString(1, U.toTitleCase(data[1]).replace(" - ", "-")); //Correct City
				pstmt.setString(2, U.toTitleCase(data[3])); // Correct State
				pstmt.setString(3, U.getTodayDate());  // Incorrect City				
				pstmt.setString(4, data[2]);  // Incorrect City
				pstmt.setString(5, data[0]);  //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				updatedIdList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");
			pstmt.close();
			
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updatedIdList;
	}//eof updateIncorrectCityState()
	
	/**
	 * This method is used to update correct zip as well as city with uncorrected with at database.
	 * @param updateDatasetForCityZip is Set of arrays of string. <br>
	 * Array of String content the size is 4.<br>
	 * index 0 : ID<br>
	 * index 1 : Correct City<br>
	 * index 2 : Incorrect City<br>
	 * index 3 : Correct Zip<br>
	 * @return List of integer containing updated ID's at database.
	 */
	public static List<Integer> updateIncorrectCityZip(Set<String[]> updateDatasetForCityZip) {
		
		List<Integer> updatedIdList = new ArrayList<>();
		
		U.log("Start updating correct city here...");
		String updateQuery="update dataset set CITY=?, ZIP=?, UPDATED_DATE=? where CITY=? and ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for (String[] data : updateDatasetForCityZip) {
				
				pstmt.setString(1, U.toTitleCase(data[1]).replace(" - ", "-")); //Correct City
				pstmt.setString(2, U.toTitleCase(data[3])); // Correct Zip
				pstmt.setString(3, U.getTodayDate());  // Incorrect City				
				pstmt.setString(4, data[2]);  // Incorrect City
				pstmt.setString(5, data[0]);  //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				updatedIdList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");
			pstmt.close();
			
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updatedIdList;
	}//eof updateIncorrectCityZip()
	
	/**
	 * This method is used to update correct city, state and zip with uncorrected with at database.
	 * @param updateDatasetForCityStateZip is Set of arrays of string. <br>
	 * Array of String content the size is 5.<br>
	 * index 0 : ID<br>
	 * index 1 : Correct City<br>
	 * index 2 : Incorrect City<br>
	 * index 3 : Correct State<br>
	 * index 4 : Correct Zip<br>
	 * @return List of integer containing updated ID's at database.
	 */
	public static List<Integer> updateIncorrectCityStateZip(Set<String[]> updateDatasetForCityStateZip) {
		
		List<Integer> updatedIdList = new ArrayList<>();
		
		U.log("Start updating correct city here...");
		String updateQuery="update dataset set CITY=?, STATE=?, ZIP=?, UPDATED_DATE=? where CITY=? and ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for (String[] data : updateDatasetForCityStateZip) {
				
				pstmt.setString(1, U.toTitleCase(data[1]).replace(" - ", "-")); //Correct City
				pstmt.setString(2, U.toTitleCase(data[3])); // Correct State
				pstmt.setString(3, U.toTitleCase(data[4])); // Correct Zip
				pstmt.setString(4, U.getTodayDate());  // Incorrect City				
				pstmt.setString(5, data[2]);  // Incorrect City
				pstmt.setString(6, data[0]);  //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				updatedIdList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");
			pstmt.close();
			
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updatedIdList;
	}//eof updateIncorrectCityStateZip()
	
	/**
	 * This method is used to delete records from database.
	 * @param deleteCityDataset
	 * is Map dataset.<br>
	 * Key of map is contains the records to be deleted at table.<br>
	 * Value of map is contain corrected city. This value is given to city field while inserting at deletedDataset table.<br>
	 */
	public static void deletedIncorrectCityRecord(Map<String[],String> deleteCityDataset){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		List<Integer> deletedIdList = insertAtDeletedTable(deleteCityDataset);
		deleteFromTable(deletedIdList);
		
	}//eof deletedIncorrectCityRecord()
	/**
	 * 
	 * @param deletedDatasetForCityState
	 */
	public static void deletedIncorrectCityStateRecord(Map<String[],String> deletedDatasetForCityState){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		List<Integer> deletedIdList = insertAtDeletedTableForCityState(deletedDatasetForCityState);
		deleteFromTable(deletedIdList);
		
	}//eof deletedIncorrectCityRecord()
	/**
	 * 
	 * @param deletedDatasetForCityZip
	 */
	public static void deletedIncorrectCityZipRecord(Map<String[],String> deletedDatasetForCityZip){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		List<Integer> deletedIdList = insertAtDeletedTableForCityZip(deletedDatasetForCityZip);
		deleteFromTable(deletedIdList);
		
	}//eof deletedIncorrectCityRecord()
	/**
	 * 
	 * @param deletedDatasetForCityStateZip
	 */
	public static void deletedIncorrectCityStateZipRecord(Map<String[],String> deletedDatasetForCityStateZip){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		List<Integer> deletedIdList = insertAtDeletedTableForCityStateZip(deletedDatasetForCityStateZip);
		deleteFromTable(deletedIdList);
		
	}//eof deletedIncorrectCityRecord()
	
	private static void deleteFromTable(List<Integer> deletedIdList){
		U.log("Start deleting duplicate composite key record at database here ....");
		
		String updateQuery = "delete from dataset where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int deleteCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : deletedIdList){
				pstmt.setInt(1, id);
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					deleteCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			deleteCount = pstmt.executeBatch();
			System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total count of deleted duplicated composite key record at database ::"+x);
	}//eof deleteFromTable()
	
	/**
	 * This method is used to insert the records at deletedDataset table. 
	 * @param deleteCityDataset
	 * is Map dataset.<br>
	 * Key of map is contains the records to be inserted at table.<br>
	 * Value of map is contain corrected city. This value is given to city field while inserting at deletedDataset table.
	 * @return
	 * List of integer containing ID's that contains the duplicate unique constraint key for deleted from database.
	 */
	private static List<Integer> insertAtDeletedTable(Map<String[],String> deleteCityDataset){
		
		List<Integer> deletedIdList = new ArrayList<>();
		
		U.log("All Records Size ::"+deleteCityDataset.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(Map.Entry<String[], String> entry : deleteCityDataset.entrySet()){
				String rows[] = entry.getKey();
				for(int i = 0; i < rows.length; i++){
					if(i == 10){
						pstmt.setString(i+1, entry.getValue());  //Corrected City
					}else if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				deletedIdList.add(Integer.parseInt(rows[0])); //add Id at list here..
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
		
		return deletedIdList;
	}//eof insertAtDeletedTable()
	
	/**
	 * 
	 * @param deletedDatasetForCityState
	 * @return
	 */
	private static List<Integer> insertAtDeletedTableForCityState(Map<String[],String> deletedDatasetForCityState){
		
		List<Integer> deletedIdList = new ArrayList<>();
		
		U.log("All Records Size ::"+deletedDatasetForCityState.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(Map.Entry<String[], String> entry : deletedDatasetForCityState.entrySet()){
				String rows[] = entry.getKey();
				String vals[] = entry.getValue().split(DB.SEPARATOR_CITY_STATE);
				
				for(int i = 0; i < rows.length; i++){
					if(i == 10){
						pstmt.setString(i+1, U.toTitleCase(vals[0]));  //Corrected City
					}else if(i == 11){
						pstmt.setString(i+1, U.toTitleCase(vals[1]));  //Corrected State
					}else if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				deletedIdList.add(Integer.parseInt(rows[0])); //add Id at list here..
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
		
		return deletedIdList;
	}//eof insertAtDeletedTableForCityState()
	
	/**
	 * 
	 * @param deletedDatasetForCityZip
	 * @return
	 */
	private static List<Integer> insertAtDeletedTableForCityZip(Map<String[],String> deletedDatasetForCityZip){
		
		List<Integer> deletedIdList = new ArrayList<>();
		
		U.log("All Records Size ::"+deletedDatasetForCityZip.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(Map.Entry<String[], String> entry : deletedDatasetForCityZip.entrySet()){
				String rows[] = entry.getKey();
				String vals[] = entry.getValue().split(DB.SEPARATOR_CITY_STATE);
				
				for(int i = 0; i < rows.length; i++){
					if(i == 10){
						pstmt.setString(i+1, U.toTitleCase(vals[0]));  //Corrected City
					}else if(i == 12){
						pstmt.setString(i+1, U.toTitleCase(vals[1]));  //Corrected Zip
					}else if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				deletedIdList.add(Integer.parseInt(rows[0])); //add Id at list here..
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
		
		return deletedIdList;
	}//eof insertAtDeletedTableForCityZip()
	
	/**
	 * 
	 * @param deletedDatasetForCityStateZip
	 * @return
	 */
	private static List<Integer> insertAtDeletedTableForCityStateZip(Map<String[],String> deletedDatasetForCityStateZip){
		
		List<Integer> deletedIdList = new ArrayList<>();
		
		U.log("All Records Size ::"+deletedDatasetForCityStateZip.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(Map.Entry<String[], String> entry : deletedDatasetForCityStateZip.entrySet()){
				String rows[] = entry.getKey();
				String vals[] = entry.getValue().split(DB.SEPARATOR_CITY_STATE);
				
				for(int i = 0; i < rows.length; i++){
					if(i == 10){
						pstmt.setString(i+1, U.toTitleCase(vals[0]));  //Corrected City
					}else if(i == 11){
						pstmt.setString(i+1, U.toTitleCase(vals[1]));  //Corrected State
					}else if(i == 12){
						pstmt.setString(i+1, U.toTitleCase(vals[2]));  //Corrected Zip
					}else if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				deletedIdList.add(Integer.parseInt(rows[0])); //add Id at list here..
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
		
		return deletedIdList;
	}//eof insertAtDeletedTableForCityStateZip()
	
	public static void disconnect(){
		try{
			if(conn != null ||!conn.isClosed()){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
}
