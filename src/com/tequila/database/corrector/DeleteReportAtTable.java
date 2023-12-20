package com.tequila.database.corrector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class DeleteReportAtTable {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeleteReportAtTable delete = new DeleteReportAtTable();
//		delete.start();
//		delete.findIncorrectIdByInnerJoinOnState();
		delete.findIncorrectIdByInnerJoinOnCompanyName();
		delete.disconnect();
	}
	
	
	private static final String MAIN_DB_NAME = "tequila.db";
	
	private static final String ID_FILE_NAME ="DeletedDuplicateRecord_31_May_18.csv";

	private static final String fileName = "/home/glady/Downloads/MexicoProject/amit/InnerJoinCompany_NameAndAddress_unique_Add_Score (1).csv";
	Connection conn = null;
	private DeleteReportAtTable() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	public DeleteReportAtTable(String dbPath, String dbName) {
		conn = DBConnection.getConnection(dbPath, dbName);
	}
	
	private void findIncorrectIdByInnerJoinOnCompanyName(){
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		
		//key is contain correct id, value with incorrect id
		Map<Integer,Integer> idCorrectIncorrectMap =  new HashMap<>();
		for(String [] lines : readLines){
			if(lines[10].trim().isEmpty())continue;
			
			int correctId = Integer.parseInt(lines[10].trim());
			int id1 = Integer.parseInt(lines[4].trim());
			int id2 = Integer.parseInt(lines[5].trim());
			if(correctId == id1){
				idCorrectIncorrectMap.put(correctId, id2); //id2 is incorrect id
			}else if(correctId == id2){
				idCorrectIncorrectMap.put(correctId, id1); //id1 is incorrect id
			}
		}
		
		U.log("Total Id's for deleted record : "+idCorrectIncorrectMap.size());
		for(Entry<Integer, Integer> entry : idCorrectIncorrectMap.entrySet()){
			U.log("Correct Id :"+entry.getKey()+"\t\t\tIncorrect Id :"+entry.getValue());
		}
		
		List<Integer> idList = new ArrayList<>(idCorrectIncorrectMap.values());
		U.log("Size Of Id List ::"+idList.size());
		Set<Integer> idSet = new HashSet<>(idList);
		idList.clear();
		idList.addAll(idSet);
		Collections.sort(idList);
		idSet.clear();		
		U.log("Size of Unique List ::"+idList.size());
		/*
		 * delete records here
		 */
		deleteRecordAtDB(idList);
	}
	
	private void findIncorrectIdByInnerJoinOnState(){
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		
		//key is contain correct id, value with incorrect id
		Map<Integer,Integer> idCorrectIncorrectMap =  new HashMap<>();
		for(String [] lines : readLines){
			int correctId = Integer.parseInt(lines[6].trim());
			int id1 = Integer.parseInt(lines[4].trim());
			int id2 = Integer.parseInt(lines[5].trim());
			if(correctId == id1){
				idCorrectIncorrectMap.put(correctId, id2); //id2 is incorrect id
			}else if(correctId == id2){
				idCorrectIncorrectMap.put(correctId, id1); //id1 is incorrect id
			}
		}
		
		U.log("Total Id's for deleted record : "+idCorrectIncorrectMap.size());
		for(Entry<Integer, Integer> entry : idCorrectIncorrectMap.entrySet()){
			U.log("Correct Id :"+entry.getKey()+"\t\t\tIncorrect Id :"+entry.getValue());
		}
		
		List<Integer> idList = new ArrayList<>(idCorrectIncorrectMap.values());
		U.log("Size Of Id List ::"+idList.size());
		Set<Integer> idSet = new HashSet<>(idList);
		idList.clear();
		idList.addAll(idSet);
		Collections.sort(idList);
		idSet.clear();		
		U.log("Size of Unique List ::"+idList.size());
		/*
		 * delete records here
		 */
		deleteRecordAtDB(idList);
	}
	
	private void start(){
//		List<String[]> idAllList = U.readCsvFile(Path.CORRECTORS_DIR + ID_FILE_NAME);
		List<String[]> idAllList = U.readCsvFile(fileName);
		List<Integer> idList = new ArrayList<>();
		
		for(String[] id : idAllList){
//			U.log(id[0]);
			idList.add(Integer.parseInt(id[0]));
		}
		U.log("Size Of All List ::"+idAllList.size());
		Set<Integer> idSet = new HashSet<>(idList);
		idList.clear();
		idList.addAll(idSet);
		Collections.sort(idList);
		idSet.clear();
		
		U.log("Size of Unique List ::"+idList.size());
		
		/*
		 * delete records here
		 */
		deleteRecordAtDB(idList);

	}
	
	public void deleteRecordAtDB(List<Integer> idList){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		insertAtDeletedTable(idList);
		
		U.log("Start deleting duplicate composite key record at database here ....");
		
		String updateQuery = "delete from dataset where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int deleteCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				pstmt.setInt(1, id);
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
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
	}
	
	
	private void insertAtDeletedTable(List<Integer> idList){
		U.log("Loading records from database ...");		
		List<String[]> allRows = getAllRecords(idList);

		U.log("All Records Size ::"+allRows.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(String[] rows : allRows){
			
				for(int i = 0; i < rows.length; i++){
					if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
	}
	
	private List<String[]> getAllRecords(List<Integer> idList){
		
		List<String[]> allRows = new ArrayList<>();
		String query = "select * from dataset where ID=";
		PreparedStatement pstmt = null;
		try{
			
			for(int id : idList){
				pstmt = conn.prepareStatement(query+id);
				ResultSet rs = pstmt.executeQuery();
				allRows.addAll(resultSetToList(rs));
				rs.close();
			}//eof for
			
			pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return allRows;
	}
	
	private List<String[]> resultSetToList(ResultSet rs) throws SQLException {
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
	
	public void disconnect(){
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
