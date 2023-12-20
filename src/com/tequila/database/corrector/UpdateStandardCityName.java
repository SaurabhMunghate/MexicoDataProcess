package com.tequila.database.corrector;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import java.util.Map.Entry;
import java.util.Set;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.geoboundary.Boundary;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.tequila.database.CreateSerializofCompositeKey;

public class UpdateStandardCityName {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		UpdateStandardCityName update = new UpdateStandardCityName();
		
//		update.correctStateAtBoundaryMexDB();
		update.start();
		update.disconnect();
		long end = System.currentTimeMillis();
		U.log("Total time to execuate ::"+(end-start));
	}
	private static final String EMPTY = "EMPTY";
	private static final String MAIN_DB_NAME = "tequila.db";
	
	private static final String BOUNDARY_MEX_DB_PATH = "/home/glady/mexicoProject/";
	private static final String BOUNDARY_MEX_DB_NAME = "boundarymex.db";
	
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/glady/MexicoCache/database/uniqueKeyTequila.ser";

	private static final String DELETED_ID_RECORD_PATH = "/home/glady/MexicoCache/database/DELECTED_ID_5_APR_9.csv";
	
	int start = 1, end = 1000;

	List<Integer> idList = new ArrayList<>();
	
	Connection conn = null;
	public UpdateStandardCityName() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	
	/*
	 * load composite key from serialize file
	 */
	CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
	HashSet<String> uniqueKeyHashSet = chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
	
	private void start(){
//		U.log("Loading..... composite key here...");
	
		U.log("Composite key set size:::"+uniqueKeyHashSet.size());
		
		/*
		 *load data from database 
		 */
		Map<String, String[]> boundaryMexRows = loadBoundaryMexicoData();
		MultiMap<String, String[]> allRows = getInformationFromDB(boundaryMexRows);


		Map<String,String[]> idForCorrectCityStateMap = new HashMap<>();
		Map<String,String> idForCorrectCityMap = new HashMap<>();
		Map<String,String> idForCorrectStateMap = new HashMap<>();
		Map<String,String> idForEmptyNeighbourhoodMap = new HashMap<>();	
		Set<String>	idForFalseBoundary = new HashSet<>();
		
		Map<String,String[]> idForDeleteRecord = new HashMap<>();
		
		U.log("Start comparing data here..");
		for(Map.Entry<String, String[]> entry : boundaryMexRows.entrySet()){
			if(allRows.containsKey(entry.getKey())){
				String [] boundaryRows = entry.getValue();
				List<String[]> rows = (List<String[]>) allRows.get(entry.getKey()); // data of Tequila
				
				for(String[] data : rows){ // data of Tequila

					//if zip is equal
					if(data[12].equals(boundaryRows[7])){
				
						/**
						 * for check, state is equal
						 */
						if(data[11].equalsIgnoreCase(boundaryRows[6])){
							//for check lat-long is not null 
							if(data[24] != null && data[25] != null){
								if(!validateBoundary(data, boundaryRows)){  //for not validated boundary
									idForFalseBoundary.add(data[0]); //id
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}
							}
							//neighbourhood is empty
							if(data[9].isEmpty()){
								if(boundaryRows[3] != null){								
									idForEmptyNeighbourhoodMap.put(data[0], U.toTitleCase(boundaryRows[3])); //id,neighbourhood
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}else if(boundaryRows[4] != null){
									idForEmptyNeighbourhoodMap.put(data[0], U.toTitleCase(boundaryRows[4])); //id,sublocality
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}
							}
							// city is not equal
							if(!data[10].equalsIgnoreCase(boundaryRows[5])){
								//check changing correct city may create duplicate composite key
								if(!isDuplicateCompositeKey(data, boundaryRows[5],EMPTY)){
									idForCorrectCityMap.put(data[0], U.toTitleCase(boundaryRows[5])); //id, correctCity
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}else{
									idForDeleteRecord.put(data[0].trim(), getDeletedRecordCompositeKey(data, U.toTitleCase(boundaryRows[5]), EMPTY));
								}
							}//eof if
						}else{
							/**
							 * state is not equal
							 */
							if(data[24] != null && data[25] != null){
								if(!validateBoundary(data, boundaryRows)){  //for not validated boundary
									idForFalseBoundary.add(data[0]); //id
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}
							}
							if(data[9].isEmpty()){
								if(boundaryRows[3] != null){								
									idForEmptyNeighbourhoodMap.put(data[0], U.toTitleCase(boundaryRows[3])); //id,neighbourhood
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}else if(boundaryRows[4] != null){
									idForEmptyNeighbourhoodMap.put(data[0], U.toTitleCase(boundaryRows[4])); //id,sublocality
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}
							}
							// city is equal
							if(data[10].equalsIgnoreCase(boundaryRows[5])){
								if(!isDuplicateCompositeKey(data, EMPTY, boundaryRows[6])){
									idForCorrectStateMap.put(data[0], U.toTitleCase(boundaryRows[6])); //id, ,correctState
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}else{
									idForDeleteRecord.put(data[0].trim(), getDeletedRecordCompositeKey(data, EMPTY,U.toTitleCase(boundaryRows[6])));
								}
							}else{
								// city is not equal
								if(!isDuplicateCompositeKey(data, boundaryRows[5],boundaryRows[6])){
									idForCorrectCityStateMap.put(data[0], new String[]{U.toTitleCase(boundaryRows[5]),U.toTitleCase(boundaryRows[6])}); //id, [correctCity,correctState]
									idList.add(Integer.parseInt(data[0])); //add Id at list here..
								}else{
									idForDeleteRecord.put(data[0].trim(), getDeletedRecordCompositeKey(data, U.toTitleCase(boundaryRows[5]),U.toTitleCase(boundaryRows[6])));
								}
							}
						}//eof else

					}//eof if
				}//eof for
			}//eof if
		}//eof for
		
		U.log("Count of correct city & state in map ::"+idForCorrectCityStateMap.size());
		U.log("Count of correct city in map ::"+idForCorrectCityMap.size());
		U.log("Count of correct state in map ::"+idForCorrectStateMap.size());
		U.log("Count of update neighbourhood in map ::"+idForEmptyNeighbourhoodMap.size());
		U.log("Count of invalid boundary in map ::"+idForFalseBoundary.size());
		U.log("Count of duplicate data in map ::"+idForDeleteRecord.size());
//		U.log("Count of duplicate data in map ::"+Arrays.asList(idForDeleteRecord.values()));
		/*
		 * Write Deleted record here
		 */
		writeDeletedRecordCSV(idForDeleteRecord);
		
		/*
		 * Update at database here
		 */
		int modify = 0;
		if(idForCorrectCityStateMap.size() > 0){
			updateCorrectCityState(idForCorrectCityStateMap);
			modify++;
		}
		if(idForCorrectCityMap.size() > 0){
			updateCorrectCity(idForCorrectCityMap);
			modify++;
		}
		if(idForCorrectStateMap.size() > 0){
			updateCorrectState(idForCorrectStateMap);
			modify++;
		}
		if(idForEmptyNeighbourhoodMap.size() > 0){
			updateNeighbourhood(idForEmptyNeighbourhoodMap);
			modify++;
		}
		if(idForFalseBoundary.size() > 0){
			updateBoundaryInValid(idForFalseBoundary);
			modify++;
		}
		
		/*
		 * Delete record at database here 
		 */
		if(idForDeleteRecord.size() > 0)
			deleteRecordAtDB(idForDeleteRecord);
		
		U.log("Start modified record inserting at updateDataset table here..");
		U.log("Size of modified records at database ::"+idList.size());
		
		if(idList.size() > 0){
			Set<Integer> idSet = new HashSet<>(idList);
			idList.clear();
			idList.addAll(idSet);
			Collections.sort(idList);
			idSet.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(idList);
		}
/*		
		 * Update composite key
		 
		if(modify > 0)
			InsertNewRecordAtDB.updateCompositeSer(uniqueKeyHashSet);	
*/	
	}//eof start()
	/**
	 * This method is load the data from boundarymex database for Mexico country.
	 * @return
	 * The collection of data that hold the correct zip and state and geo boundaries.
	 */
	private Map<String,String[]> loadBoundaryMexicoData(){
		
		Map<String,String[]> boundaryMexRows = new HashMap<>();
		U.log("Load boundarymex database here ..");
//		List<String[]> boundaryMexRows = new ArrayList<>();
		String uniqueKey = null;
		
//		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null";
//1 	String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and rowid <= 500"; //nothing update

//		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and rowid <= 1000"; //nothing update
		
//2		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 501 and rowid <=2000)"; //nothing update
//3		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 2001 and rowid <=3000)"; //nothing update
//4		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 3001 and rowid <=4000)"; //nothing update
//5		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 4001 and rowid <=5000)"; //nothing update
//6		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 5001 and rowid <=6000)";
//7		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 6001 and rowid <=7000)";
//8		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and (rowid >= 7001 and rowid <=8000)";
		String sql = "select * from boundaryset where country=\"Mexico\" and input_zip=zip and city is not null and rowid >= 8001";
		Connection conn1 = DBConnection.getConnection(BOUNDARY_MEX_DB_PATH, BOUNDARY_MEX_DB_NAME);
		try(Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(sql);)
		{
//			boundaryMexRows.addAll(resultSetToList(rs));
			List<String[]> rows = resultSetToList(rs);
			for(String[] row : rows){
				uniqueKey = row[0]+row[1]+row[2];
				boundaryMexRows.put(uniqueKey.toLowerCase(), row);
			}
		
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				if(conn1 != null)
					conn1.close();
				U.log("Database is closed.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		U.log("Size of boundary database is ::"+boundaryMexRows.size());	
		return boundaryMexRows;
	}
	/**
	 * This method is used to load the data from Tequila database for city,state and zip of BoundaryMex database.
	 * @param boundaryMexRows
	 * :- The collection of data that hold the correct zip and state and geo boundaries.
	 * @return
	 * The collection of data that hold the entire row details for city,state and zip of BoundaryMex database.
	 */
	private MultiMap<String, String[]> getInformationFromDB(Map<String, String[]> boundaryMexRows){
		MultiMap<String, String[]> dataset = new MultiValueMap<>();
		U.log("Start initialize data from tequila database here ....");
//		List<String[]> allRows = new ArrayList<>();
		String uniqueKey = null;
		
		String query = "select * from dataset where city=? and state=? and zip=?";

		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement(query);
			for(String[] boundaryMex : boundaryMexRows.values()){
				stmt.setString(1, boundaryMex[0]);
				stmt.setString(2, boundaryMex[1]);
				stmt.setString(3, boundaryMex[2]);
//				U.log(Arrays.toString(boundaryMex));
				uniqueKey = boundaryMex[0]+boundaryMex[1]+boundaryMex[2];
//				ResultSet rs = stmt.executeQuery("select * from dataset where city=\""+boundaryMex[0]+"\" and state=\""+boundaryMex[1]+"\" and zip=\""+boundaryMex[2]+"\"");
				ResultSet rs = stmt.executeQuery();

//				allRows.addAll(resultSetToList(rs));
				List<String[]> rows = resultSetToList(rs);
				rs.close();
				for(String[] row : rows){
					dataset.put(uniqueKey.toLowerCase(), row);
				}
//				if(start++ == end)break;
			}//eof for
			stmt.close();			
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Count of records from tequila db for correction city name ::"+dataset.size());
		return dataset;
	}
	
	private String[] getDeletedRecordCompositeKey(String[] data, String correctCity, String correctState){
		String[] vals = null;
		
		if(!correctCity.equals(EMPTY) && !correctState.equals(EMPTY)){
			vals = new String[]{
					U.toTitleCase(data[4].trim()), U.toTitleCase(data[7].trim()), U.toTitleCase(data[8].trim()), U.toTitleCase(correctCity.trim()),  U.toTitleCase(correctState.trim()),
					U.toTitleCase(data[17].trim()),U.toTitleCase(data[10].trim()),U.toTitleCase(data[11].trim()),correctCity,correctState};
		}
		else if(!correctCity.equals(EMPTY) && correctState.equals(EMPTY)){
			vals =  new String[]{
					U.toTitleCase(data[4].trim()), U.toTitleCase(data[7].trim()), U.toTitleCase(data[8].trim()), U.toTitleCase(correctCity.trim()), U.toTitleCase(data[11].trim()),
					U.toTitleCase(data[17].trim()),U.toTitleCase(data[10].trim()),"",correctCity,""};
		}else if(correctCity.equals(EMPTY) && !correctState.equals(EMPTY)){
			vals =  new String[]{
					U.toTitleCase(data[4].trim()), U.toTitleCase(data[7].trim()), U.toTitleCase(data[8].trim()), U.toTitleCase(correctCity.trim()), U.toTitleCase(data[11].trim()),
					U.toTitleCase(data[17].trim()),"",U.toTitleCase(data[11].trim()),"",correctState};
		}
		return vals;
	}
	

	
	private boolean isDuplicateCompositeKey(String[] data, String correctCity, String correctState){
		String sicSub = data[4].trim();
		if(sicSub.length() == 3)sicSub="0"+sicSub;
		
		String uniqueKey = null;
		if(!correctCity.equals(EMPTY) && !correctState.equals(EMPTY)){
			uniqueKey =	U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(data[8].trim()) + U.toTitleCase(correctCity.trim()) + U.toTitleCase(correctState.trim())+ U.toTitleCase(data[17].trim());
		}
		if(!correctCity.equals(EMPTY) && correctState.equals(EMPTY)){
			uniqueKey = U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(data[8].trim()) + U.toTitleCase(correctCity.trim()) + U.toTitleCase(data[11].trim())+ U.toTitleCase(data[17].trim());
		}
		if(correctCity.equals(EMPTY) && !correctState.equals(EMPTY)){
			uniqueKey = U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(data[8].trim()) + U.toTitleCase(data[10].trim()) + U.toTitleCase(correctState.trim())+ U.toTitleCase(data[17].trim());
		}
		
		
		if (uniqueKeyHashSet.contains(uniqueKey.toLowerCase())){
			return true;
		}else{
			uniqueKeyHashSet.add(uniqueKey.toLowerCase());
		}
		return false;
	}
	
	
	private boolean validateBoundary(String[] data,String [] boundaryRows){
		return Boundary.boundaryCheckForCityZipState(data[25], data[24], boundaryRows[9], boundaryRows[10], boundaryRows[11], boundaryRows[12]);
	}
	/**
	 * This method is used to correct state format at boundarymex database.
	 */
	private void correctStateAtBoundaryMexDB(){
		Map<String, String[]> boundaryMexRows = loadBoundaryMexicoData();
		
		Set<String> stateSet = new HashSet<>();
		
		for(String[] vals : boundaryMexRows.values()){
			
			stateSet.add(vals[6]); //state
		}
		U.log("Total count of state at boundarymex db ::"+stateSet.size());
		
		Map<String, String> correctStateMap = new HashMap<>();
		
		for(String state : stateSet){
			correctStateMap.put(state, U.matchState(state));
		}
		U.log("Total Size of correct state map is  ::"+correctStateMap.size());
		
		for(Map.Entry<String, String> entry : correctStateMap.entrySet()){
			if(!entry.getKey().equals(entry.getValue())){
				U.errLog(entry.getKey()+"\t"+entry.getValue());
			}else
			U.log(entry.getKey()+"\t"+entry.getValue());
		}
		
		String updateQuery = "Update boundaryset set state=? where state=?";
		
		Connection conn1 = DBConnection.getConnection(BOUNDARY_MEX_DB_PATH, BOUNDARY_MEX_DB_NAME);
		int updateCount = 0;
		PreparedStatement pstmt = null;
		try{
			conn1.setAutoCommit(false);

			pstmt = conn1.prepareStatement(updateQuery);
			
			for(Map.Entry<String, String> entry : correctStateMap.entrySet()){
				if(!entry.getKey().equals(entry.getValue())){
					pstmt.setString(1, entry.getValue());
					pstmt.setString(2, entry.getKey());
					int i = pstmt.executeUpdate();
					U.log("Update state \'"+entry.getKey()+"\' into \""+entry.getValue()+"\"\t update count ::"+i);
					updateCount += i;
				}
			}
			pstmt.close();
		}catch(SQLException e){
			try {
				conn1.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				if(conn1 != null){
					conn1.commit();
					conn1.setAutoCommit(true);
					conn1.close();
				}
				U.log("Database is closed.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		U.log("Total updated state at db is ::"+updateCount);
	}
	

		

	public static List<String[]> resultSetToList(ResultSet rs) throws SQLException {
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
	
	private void writeDeletedRecordCSV(Map<String,String[]> idForDeleteRecord){
		String[] header = {"SIC_SUB","COMPANY_NAME","ADDRESS","CITY","STATE","CONTACT_PERSON","DELECTED_ID","WRONG_CITY","WRONG_STATE","CORRECT_CITY","CORRECT_STATE"};
		List<String[]> writeLines = new ArrayList<>();
		
		for(Map.Entry<String, String[]> entry : idForDeleteRecord.entrySet()){
			writeLines.add(new String[]{
					entry.getValue()[0],entry.getValue()[1],entry.getValue()[2],entry.getValue()[3],entry.getValue()[4],entry.getValue()[5],
					entry.getKey(),entry.getValue()[6],entry.getValue()[7],entry.getValue()[8],entry.getValue()[9],
			});
		}
		try(CSVWriter writer = new CSVWriter(new FileWriter(DELETED_ID_RECORD_PATH),',');){
			writer.writeNext(header);
			writer.writeAll(writeLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();			
		}
		U.log("Deleted file is written..");
	}
	
	private void updateNeighbourhood(Map<String,String> idForEmptyNeighbourhoodMap){
		U.log("Start updating neighbourhood at database here ....");
		
		String updateQuery = "update dataset set NEIGHBORHOOD=?,_LAST_UPDATED=? where id=? and NEIGHBORHOOD=\"\"";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(Map.Entry<String, String> entry : idForEmptyNeighbourhoodMap.entrySet()){

				pstmt.setString(1, U.toTitleCase(entry.getValue())); //NEIGHBORHOOD
				pstmt.setString(2, getTodayDate()); 
				pstmt.setString(3, entry.getKey()); //ID
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
		U.log("Total count of updated neighbourhood at database ::"+x);
	}
	
	private void updateCorrectCity(Map<String,String> idForCorrectCityMap){
		U.log("Start updating corrected city at database here ....");
		
		String updateQuery = "update dataset set CITY=?,_LAST_UPDATED=? where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(Map.Entry<String, String> entry : idForCorrectCityMap.entrySet()){

				pstmt.setString(1, U.toTitleCase(entry.getValue())); //correct CITY
				pstmt.setString(2, getTodayDate());
				pstmt.setString(3, entry.getKey()); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
		U.log("Total count of updated correct city at database ::"+x);
	}
	
	private void updateCorrectState(Map<String,String> idForCorrectStateMap){
		U.log("Start updating corrected state at database here ....");
		
		String updateQuery = "update dataset set STATE=?,_LAST_UPDATED=? where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(Map.Entry<String, String> entry : idForCorrectStateMap.entrySet()){
	//			U.log(entry.getKey()+"\t\t"+U.toTitleCase(entry.getValue()));
				pstmt.setString(1, U.toTitleCase(entry.getValue())); //correct STATE
				pstmt.setString(2, getTodayDate());
				pstmt.setString(3, entry.getKey()); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
		U.log("Total count of updated correct state at database ::"+x);
	}
	
	private void updateCorrectCityState(Map<String,String[]> idForCorrectCityStateMap){
		U.log("Start updating corrected city & state at database here ....");
		
		String updateQuery = "update dataset set CITY=?,STATE=?,_LAST_UPDATED=? where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(Map.Entry<String, String[]> entry : idForCorrectCityStateMap.entrySet()){

				pstmt.setString(1, U.toTitleCase(entry.getValue()[0])); //correct CITY
				pstmt.setString(2, U.toTitleCase(entry.getValue()[1])); //correct STATE
				pstmt.setString(3, getTodayDate());
				pstmt.setString(4, entry.getKey()); //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
		U.log("Total count of updated correct city & state at database ::"+x);
	}
	
	private void updateBoundaryInValid(Set<String> idForFalseBoundary){
		U.log("Start updating invalid lat-long at database here ....");
		
		String updateQuery = "update dataset set LONGITUDE=?,LATITUDE=?,_LAST_UPDATED=? where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(String id : idForFalseBoundary){

				pstmt.setString(1, null);
				pstmt.setString(2, null);
				pstmt.setString(3, getTodayDate());
				pstmt.setString(4, id);
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
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
		U.log("Total count of updated invalid lat-long at database ::"+x);
	}
	
	private void deleteRecordAtDB(Map<String,String[]> idForDeleteRecord){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		insertAtDeletedTable(idForDeleteRecord);
		
		U.log("Start deleting duplicate composite key record at database here ....");
		
		String updateQuery = "delete from dataset where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(String id : idForDeleteRecord.keySet()){
				pstmt.setString(1, id);
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total count of deleted duplicated composite key record at database ::"+x);
	}
	

	
	private void insertAtDeletedTable(Map<String,String[]> idForDeleteRecord){
		U.log("Loading records from database ...");
		
		List<String[]> allRows = getAllRecords(idForDeleteRecord.keySet());

		U.log("All Records Size ::"+allRows.size());
		
		Iterator<String[]> it = allRows.iterator();
		String [] row = null;
		while(it.hasNext()){
			row = it.next();
			
			if(idForDeleteRecord.containsKey(row[0])){ //ID
				
				if(!idForDeleteRecord.get(row[0])[8].isEmpty()){ 
					row[10] =  idForDeleteRecord.get(row[0])[8]; //correct city
				}
				if(!idForDeleteRecord.get(row[0])[9].isEmpty()){ 
					row[11] =  idForDeleteRecord.get(row[0])[9]; //correct state
				}
				row[32] = getTodayDate();
			}
		}//eof while
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(String[] rows : allRows){
			
				for(int i = 0; i < rows.length; i++){
					pstmt.setString(i+1, rows[i]);
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
	
	private List<String[]> getAllRecords(Set<String> idSet){
		
		List<String[]> allRows = new ArrayList<>();
		String query = "select * from dataset where ID=";
		PreparedStatement pstmt = null;
		try{
			
			for(String id : idSet){
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
}
