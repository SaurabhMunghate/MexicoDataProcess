package com.tequila.database.corrector;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class UpdateUrlAtDB {

	public static void main(String[] args) {
		UpdateUrlAtDB update = new UpdateUrlAtDB();
//		update.start();
		update.findMissingUrl();
		update.disconnect();

	}
	private static final String MAIN_DB_NAME = "tequila.db";
	
	private static final String URL_CORRECTOR_FILE = "/home/glady/Downloads/MexicoProject/rakeshsir/26_June/Update_Urls_80_records.csv";
	
	private static final String URL_CORRECTOR_FILE_1 = "/home/glady/Downloads/MexicoProject/rakeshsir/26_June/Update_Urls_86_records.csv";
	
	private Connection conn = null;
	
	public UpdateUrlAtDB() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}

	private void findMissingUrl(){
		List<String[]> dataset = new ArrayList<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(URL_CORRECTOR_FILE);		
		readLines.addAll(U.readCsvFileWithoutHeader(URL_CORRECTOR_FILE_1));
		U.log("************** Url's ***************");
		String lines[] = null;
		Iterator<String[]> it = readLines.iterator();
		int x = 0;
		while(it.hasNext()){
			lines = it.next();
//			if(x++ == 0)continue;
			if(lines[15].trim().isEmpty() || lines[15].trim().length()<15){
				continue;
			}
			
			if(!lines[15].trim().isEmpty()){
				U.log(lines[15].trim());
				dataset.add(new String[]{
					lines[0].trim(), lines[4].trim(), lines[7].trim(), lines[15].trim()					
				}); 
			}
		}
		U.log("Size of dataset ::"+dataset.size());
		
		Map<String,String[]> map = new HashMap<>();
		
		for(String[] vals : dataset){
//			U.log(Arrays.toString(vals));
			map.put(vals[0], vals);
		}
		U.log("Unique size of map ::"+map.size());
		dataset.clear();
		dataset.addAll(map.values());
		U.log("Unique size of dataset ::"+dataset.size());
		
		if(dataset.size()>0){
			List<Integer> idList = updateUrlAtDB(dataset);
			updateAtDataset(idList);
		}
	}
	
	private void updateAtDataset(List<Integer> idList){
		if(idList.size() > 0){
			Set<Integer> set = new HashSet<>(idList);
			idList.clear();
			idList.addAll(set);
			Collections.sort(idList);
			set.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(idList);
			report.disconnect();
		}
	}
	
	private void start(){
		List<Integer> idList = new ArrayList<>();
		Set<String> idSet = loadIDs();
		List<String[]> readLines = loadUrlCorrectorFile(URL_CORRECTOR_FILE);
		U.log("Total record size for update url ::"+readLines.size());
		
		String lines[] = null;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			if(!idSet.contains(lines[0])){
				U.log(Arrays.toString(lines));
				it.remove();
			}else{
				idList.add(Integer.parseInt(lines[0].trim()));
			}
		}
		U.log("Total records id present at database ::"+readLines.size());
		/*
		 * Start updating url at database
		 */
		updateUrlAtDB(readLines);
		
		
		U.log("Size of modified records at database ::"+idList.size());
		
		if(idList.size() > 0){
			Set<Integer> set = new HashSet<>(idList);
			idList.clear();
			idList.addAll(set);
			Collections.sort(idList);
			set.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(idList);
		}
		
		
	}
	
	
	private List<Integer> updateUrlAtDB(List<String[]> readLines){
		List<Integer> idList = new ArrayList<>();
		
		U.log("Start updating url at database here ....");
		
		String updateQuery = "update dataset set URL=?,UPDATED_DATE=? where ID=? AND COMPANY_NAME=? AND URL is null";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(String[] lines : readLines){
				pstmt.setString(1, lines[3].trim().toLowerCase()); //URL
				pstmt.setString(2, getTodayDate());
				pstmt.setString(3, lines[0].trim()); //ID
				pstmt.setString(4, lines[2].trim()); //COMPANY_NAME
				
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				idList.add(Integer.parseInt(lines[0].trim())); //add id's here
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total count of updated url at database ::"+x);
		
		return idList;
	}

	
	List<String[]> loadUrlCorrectorFile(String fileName){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName),',');){
			readLines = reader.readAll();
			readLines.remove(0);
		}catch(IOException e){
			e.printStackTrace();
		}
		return readLines;
	}
	
	Set<String> loadIDs(){
		Set<String> idSet = new HashSet<>();
		
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select id from dataset where url=\"\"");)
		{
			while(rs.next()){
				idSet.add(rs.getString("id"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return idSet;
	}
	
	private void disconnect(){
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
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final String getTodayDate(){
		return dateFormat.format(new Date());
	}
}
