package com.tequila.modification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import com.database.connection.DBConnection;
import com.shatam.utils.U;

public class FindDuplicateState {
	public static final String ALLOW_BLANK = "-";
	private static final String PATH = "/home/glady/MexicoCache/data/";
	
	private static final String WRONG_STATE_FILE_NAME ="wrongStateList.txt";
	private static final String COMPOSITE_KEY_FILE_NAME = "compostekey.txt";
	
	Connection conn = null;

	public FindDuplicateState() {
		conn = DBConnection.getConnection("/home/glady/MexicoCache/data/", "tequila.db");
	}
	
	
	public static void main(String[] args) {
		FindDuplicateState dup = new FindDuplicateState();
		dup.loadStateFromDB();
		dup.findDuplicateState();
		
		try{
			dup.conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	
	ArrayList<String> stateList = new ArrayList<String>();
	HashSet<String> compositeKey = new HashSet<String>();
	
	public void checkFileIsExist(String absFilePath){
		if(new File(absFilePath).exists()){
			U.log("FILE FOUND....");
			try (BufferedReader br = new BufferedReader(new FileReader(absFilePath));){
				String line = null;
				while((line = br.readLine()) != null){
					stateList.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void checkCompositeKeyIsExist(String absFilePath){
		if(new File(absFilePath).exists()){
			U.log("FILE FOUND....");
			try (BufferedReader br = new BufferedReader(new FileReader(absFilePath));){
				String line = null;
				while((line = br.readLine()) != null){
					compositeKey.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadStateFromDB(){
		//check in file
		checkFileIsExist(PATH+WRONG_STATE_FILE_NAME);
		//check composite key
		checkCompositeKeyIsExist(PATH+COMPOSITE_KEY_FILE_NAME); ///------------------comment for now
		
		if(stateList.isEmpty()){
			String query1 = "select state from dataset group by state order by state asc";
			Statement stmt = null;
			ResultSet rs = null;
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query1);
				while(rs.next()){
					stateList.add(rs.getString(1).trim());
				}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}catch(SQLException ex){	
				ex.printStackTrace();
			}
			if(stateList.size() > 0){
				try {
					FileWriter writer = new FileWriter(PATH+WRONG_STATE_FILE_NAME);
					for(String state : stateList){
						writer.write(state+"\n");
					}
					writer.flush();
					writer.close();
					writer = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}//eof if
//--------------- Comment for now		
		if(compositeKey.isEmpty()){
			int count = 0;
			/*
			 * CREATE UNIQUE INDEX SHATAM_INDEX ON dataset (COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON)
			 */
			String query = "SELECT COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON FROM dataset";
			Statement stmt = null;
			ResultSet rs = null;
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while(rs.next()){
					String uniqueKey = rs.getString(1).trim()+rs.getString(2).trim()+rs.getString(3).trim()+rs.getString(4).trim()+rs.getString(5).trim();
					if(!compositeKey.add(uniqueKey.toLowerCase())){
						count++;	
					}
				}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}catch(SQLException ex){	
				ex.printStackTrace();
			}
			if(compositeKey.size() > 0){
				try {
					FileWriter writer = new FileWriter(PATH+COMPOSITE_KEY_FILE_NAME);
					for(String key : compositeKey){
						writer.write(key+"\n");
					}
					writer.flush();
					writer.close();
					writer = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			U.log("duplicate key ::"+count);
		}//eof if
	
	}
	
	private void findDuplicateState(){

		
		ArrayList<String> wrongStateList =  new ArrayList<>();
		ArrayList<String> zipList = new ArrayList<String>();
		ArrayList<String[]> correctStateList = new ArrayList<String[]>();
		ArrayList<String> cityList = new ArrayList<String>();
		
		for(String state : stateList){
			boolean found = U.isState(state);
			if(!found){
				//for not found state
				U.log("***********"+state);
				//zip
				if(state.trim().matches("\\d{5}")){
					zipList.add(state.trim());
				}else{
					cityList.add(state.trim());
				}
//				wrongStateList.add(state.trim());
			}else{
				// for found state 
				String correctState = U.matchState(state);
				
				if(!state.equals(correctState) && correctState != ALLOW_BLANK){
					U.log(state+"\t\t>>"+correctState);
					correctStateList.add(new String[]{state.trim(),correctState.trim()});
//					updateState(state, correctState);
				}else if(!state.equals(correctState) && correctState == ALLOW_BLANK){
					U.log("######"+state);
					cityList.add(state.trim());
//					wrongStateList.add(state.trim());
				}else{
					U.log(">>"+state);
				}
			}
		}
		
//		Collections.sort(wrongStateList);
		U.log("================WrongState Correct============");
		for(String[] states : correctStateList)
			U.log(states[0] +"\t"+states[1]);

//		findCorrectedState(correctStateList);  //---------- comment for now
		
		U.log("================WrongState city============");
		for(String city : cityList)
			U.log(city);
//		findCityState(cityList);  //---------- comment for now
		
		U.log("================WrongState Zip ============");
		for(String zip : zipList)
			U.log(zip);
//		findZipState(zipList);  //---------- comment for now
		

	}//eof findDuplicateState()
	
	private void findCityState(ArrayList<String> cityList){
		ArrayList<String[]> storedCityStateCorrection = new ArrayList<String[]>();
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("SELECT ID,COMPANY_NAME,ADDRESS,CITY,STATE FROM dataset WHERE STATE=?");
			ResultSet rs = null;
			for(String city : cityList){
				stmt.setString(1, city);
				rs=stmt.executeQuery();
				
				while(rs.next()){
					storedCityStateCorrection.add(new String[]{
							rs.getString(1).trim(),
							rs.getString(2).trim(),
							rs.getString(3).trim(),
							rs.getString(4).trim(),
							rs.getString(5).trim()});
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Size of stored wrong state with correction ::"+storedCityStateCorrection.size());
		
		HashSet<String> storedID = new HashSet<String>();
		
		ArrayList<String[]> storedCityStateCorrectionData = new ArrayList<String[]>();
		
		for(String[] vals : storedCityStateCorrection){
			//------ comment for now
			
			String uniqueKey  = vals[1].trim()+vals[2].trim()+vals[4].trim()+vals[3].trim();
			
			if(!compositeKey.add(uniqueKey)){
				System.err.println(vals[0]+"\t"+vals[1]+"\t"+vals[2]+"\t"+vals[3]+"\t"+vals[4]);
				storedID.add(vals[0].trim());
			}else{
				U.log(vals[0]+"\t"+vals[1]+"\t"+vals[2]+"\t"+vals[3]+"\t"+vals[4]);
				storedCityStateCorrectionData.add(new String[]{vals[0],vals[3],vals[4]});
			}
		}
		//called method for stored duplicate data
		storedDuplicateCompositeKeyData(storedID);  //-------- comment for now

		//called method for swapping state & city
		updateCityState(storedCityStateCorrectionData);

	}
	/**
	 * This method is used to stored the duplicate composite key data
	 * @param storedID
	 */
	private void storedDuplicateCompositeKeyData(HashSet<String> storedID){
		
		PreparedStatement stmt = null;
		ArrayList<String[]> storedDataRows = new ArrayList<String[]>();
		try{
			stmt = conn.prepareStatement("SELECT * FROM dataset WHERE ID=?");
			ResultSet rs = null;
			for(String id : storedID){
				stmt.setString(1, id);
				rs=stmt.executeQuery();				
				while(rs.next()){
					storedDataRows.add(new String[]{
					rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
					rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
					rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
					rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),
					rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),
					rs.getString(26),rs.getString(27),rs.getString(28),rs.getString(29),rs.getString(30),
					});
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Size of Stored rows::"+storedDataRows.size());
		for(String[] rows : storedDataRows){
			U.log(Arrays.toString(rows));
		}
		// called method of insert data at tables
		insertNewDataAtNewTable(storedDataRows,storedID);
	}
	
	/**
	 * This method is used to insert data into another table
	 * @param storedDataRows
	 * @param storedID
	 */
	private void insertNewDataAtNewTable(ArrayList<String[]> storedDataRows,HashSet<String> storedID){
		PreparedStatement pstmt = null;
		U.log(".......start inserting into datasetdup");
		String insertQuery = "INSERT INTO datasetdup VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(insertQuery);
			conn.setAutoCommit(false);
			int updateCount[];
			int i = 0;
			
			for(String[] fields : storedDataRows){
				
				pstmt.setInt(1, Integer.parseInt(fields[0]));
				int k = 2;
				for(int j = 1; j< fields.length; j++){
					pstmt.setString(k, fields[j]);
					k++;
				}
				pstmt.addBatch();
				
				if ((++i % 5000) == 0)
				{
					System.out.println("Commit the batch");
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
				}
				
			}//eof for
				
			updateCount = pstmt.executeBatch();
			System.out.println("Update count =" + updateCount.length);
			
			//called method for deleting rows from the table 
			deleteDuplicateComposteKeyDataFromTable(storedID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.commit();
			conn.setAutoCommit(true);
			pstmt.close();
			pstmt = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method is used to delete the row from table
	 * @param storedID
	 */
	private void deleteDuplicateComposteKeyDataFromTable(HashSet<String> storedID){
		U.log("........  start deleting duplicate composite key at dataset");
		String query = "DELETE FROM dataset where ID=?";
		PreparedStatement pstmt = null;
		int deletedCount[];
		int i = 0;
		try{
			pstmt = conn.prepareStatement(query);
			for(String id : storedID){
				pstmt.setString(1, id);
				pstmt.addBatch();
				
				if ((++i % 5000) == 0)
				{
					System.out.println("Commit the batch");
					deletedCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ deletedCount.length + "\t" + i);
				}
				
			}//eof if
			
			deletedCount = pstmt.executeBatch();
			System.out.println("Deleted count =" + deletedCount.length);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateCityState(ArrayList<String[]> storedCityStateCorrection){
		U.log("......start updating...... swap city & state");
		PreparedStatement stmt = null;
		String query = "UPDATE dataset SET CITY = ?,STATE = ? where ID = ?";
		try{
			stmt = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			for(String[] vals: storedCityStateCorrection){
				stmt.setString(1, vals[2]);
				stmt.setString(2, vals[1]);
				stmt.setString(3, vals[0]);
				stmt.addBatch();
			}
			int[] updateRows = stmt.executeBatch();
			U.log("Updated Rows count=="+updateRows.length);
			conn.commit();
			conn.setAutoCommit(true);
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param correctStateList
	 * This is collection of array of string.This array of string size is 2. 
	 * This array of string is contain the wrong state and its correct format state
	 * That is str[0] = wrong state, str[1] = corrected state
	 */
	private void findCorrectedState(ArrayList<String[]> correctStateList){
		/**
		 * ArrayList<String[]> storedWrongStateCorrection
		 * Array of string is stored the ID of wrong state and with its correction format of state
		 * Size of array is 3.
		 * str[0] = ID, str[1] = wrong state, str[2]= corrected state format
		 */
		ArrayList<String[]> storedWrongStateCorrection = new ArrayList<String[]>();
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("SELECT ID,COMPANY_NAME,ADDRESS,CITY,STATE FROM dataset WHERE STATE=?");
			ResultSet rs = null;
			
			for(String[] states : correctStateList){
				stmt.setString(1, states[0]);
				rs=stmt.executeQuery();
				
				while(rs.next()){
					storedWrongStateCorrection.add(new String[]{
							rs.getString(1).trim(), //ID
							rs.getString(2).trim(),
							rs.getString(3).trim(), 
							rs.getString(4).trim(), //city
							rs.getString(5).trim(), 
							states[1]}); //correct state format
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}catch(SQLException e){
			e.printStackTrace();
		}
	
		HashSet<String> storedID = new HashSet<String>();
		
		ArrayList<String[]> storedCityStateCorrectionData = new ArrayList<String[]>();
		
		for(String[] vals : storedWrongStateCorrection){
		//-------------- comment for now
			
			String uniqueKey  = vals[1].trim()+vals[2].trim()+vals[3].trim()+vals[5].trim();
			
			if(!compositeKey.add(uniqueKey)){
				System.err.println(vals[0]+"\t"+vals[1]+"\t"+vals[2]+"\t"+vals[3]+"\t"+vals[4]);
				storedID.add(vals[0].trim());
			}else{
//				U.log(vals[0]+"\t"+vals[1]+"\t"+vals[2]+"\t"+vals[3]+"\t"+vals[4]);
				storedCityStateCorrectionData.add(new String[]{vals[0],vals[4],vals[5]});
			}
		}
		
		U.log("Size of stored state with correction format ::"+storedWrongStateCorrection.size());
		U.log("Size of stored wrong state with correction ::"+storedCityStateCorrectionData.size());
		U.log("Count of duplicate composite key ::"+storedID.size());
		
		//called method for stored duplicate data
		storedDuplicateCompositeKeyData(storedID);
		//called method for update correct state at state field
		updateCorrectedState(storedCityStateCorrectionData);
	}
	
	private void updateCorrectedState(ArrayList<String[]> storedWrongStateCorrection){
		U.log("start updating...... corrected state format at dataset");
		PreparedStatement stmt = null;
		int updateCount[];
		int i = 0;
		String query = "UPDATE dataset SET STATE = ? where ID = ?";
		try{
			stmt = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			for(String[] vals: storedWrongStateCorrection){
				stmt.setString(1, vals[2]);
				stmt.setString(2, vals[0]);
				stmt.addBatch();
				
				if ((++i % 10000) == 0)
				{
					System.out.println("Commit the batch");
					updateCount = stmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn.commit();
				}
			}
			updateCount = stmt.executeBatch();
			U.log("Updated Rows count=="+updateCount.length);
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void findZipState(ArrayList<String> zipList){
		/**
		 * ArrayList<String[]> storedZipStateCorrection
		 * Array of String stored the value ID,CITY,STATE,ZIP i.e size is 4
		 * for str[0] = ID , str[1] = CITY, str[2] = STATE, str[3] = ZIP
		 */
		ArrayList<String[]> storedZipStateCorrection = new ArrayList<String[]>();
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("SELECT ID,CITY,STATE,ZIP FROM dataset WHERE STATE=?");
			ResultSet rs = null;
			for(String zip : zipList){
				stmt.setString(1, zip.trim());
				rs=stmt.executeQuery();
				
				while(rs.next()){  
					storedZipStateCorrection.add(new String[]{rs.getString(1).trim(),rs.getString(2).trim(),rs.getString(3).trim(),rs.getString(4).trim()});
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}catch(SQLException e){
			e.printStackTrace();
		}
		//Correction state with zip
		
		for(String[] vals: storedZipStateCorrection){
			U.log(vals[0]+"\t"+vals[1]+"\t"+vals[2]+"\t"+vals[3]+"\t");
		}
		updateZipState(storedZipStateCorrection);
	}
	
	public void updateZipState(ArrayList<String[]> storedZipStateCorrection){
		U.log("start updating......");
		PreparedStatement stmt = null;
		String query = "UPDATE dataset SET ZIP = ?,STATE = ? where ID = ?";
		try{
			stmt = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			for(String[] vals: storedZipStateCorrection){
				stmt.setString(1, vals[2]);
				stmt.setString(2, vals[3]);
				stmt.setString(3, vals[0]);
				stmt.addBatch();
			}
			int[] updateRows = stmt.executeBatch();
			U.log("Updated Rows count=="+updateRows.length);
			conn.commit();
			conn.setAutoCommit(true);
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
