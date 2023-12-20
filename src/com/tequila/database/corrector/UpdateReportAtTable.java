package com.tequila.database.corrector;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.U;

public class UpdateReportAtTable {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		UpdateReportAtTable report = new UpdateReportAtTable();
		report.start();
		report.disconnect();
		long end = System.currentTimeMillis();
		U.log("Execution time ::"+(end-start));	
	}
	
	private static final String DB_PATH ="/home/glady/MexicoCache/database/";
	
	private static final String DB_NAME ="tequila.db";
	
	private static final String CORRECTORS_DIR = "/home/glady/MexicoCache/database/Correctors/";
	
	private static final String ID_FILE_NAME ="Updated_ID_05_June_1.csv";
	
	
	private static final String insertQueryAtUpdatedTable = "INSERT INTO updatedDataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,CONTACT_PERSON,"
			+"TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,ADDRESS_VERIFICATION_CODE, GEO_ACCURACY_CODE, GEO_DISTANCE, QUALITY_SCORE, LOCATION_SOURCE, Hours_Of_Operation,CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
			+"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String insertQueryAtDeletedTable = "INSERT INTO deletedDataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,CONTACT_PERSON,"
			+"TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,ADDRESS_VERIFICATION_CODE, GEO_ACCURACY_CODE, GEO_DISTANCE, QUALITY_SCORE, LOCATION_SOURCE, Hours_Of_Operation,CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
			+"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	
	Connection conn = null;
	
	private UpdateReportAtTable(){
		conn = DBConnection.getConnection(DB_PATH, DB_NAME);
	}
	
	public UpdateReportAtTable(Connection conn){
		U.log("Connection  Inherited");
		this.conn = conn;
	}
	
	public UpdateReportAtTable(String dbPath, String dbName){
		conn = DBConnection.getConnection(dbPath, dbName);
	}
	
	
	private void start(){
		List<String[]> idAllList = loadFile(CORRECTORS_DIR + ID_FILE_NAME);
		List<Integer> idList = new ArrayList<>();
		
		for(String[] id : idAllList){
			idList.add(Integer.parseInt(id[0]));
		}
		U.log("Size Of All List ::"+idAllList.size());
		Set<Integer> idSet = new HashSet<>(idList);
		idList.clear();
		idList.addAll(idSet);
		Collections.sort(idList);
		idSet.clear();
		
		U.log("Size of Unique List ::"+idList.size());

		startExtractingFromDB(idList);
	}
	
	public void startExtractingFromDB(List<Integer> idList){
		U.log("Loading records from database ...");
		
		List<String[]> allRows = new ArrayList<>();
		String query = "select * from dataset where ID=";
		PreparedStatement pstmt = null;
		try{
			conn.setAutoCommit(false);
			for(int id : idList){
				pstmt = conn.prepareStatement(query+id);
				ResultSet rs = pstmt.executeQuery();
				allRows.addAll(resultSetToList(rs));
				rs.close();
			}//eof for
//			conn.commit();
			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		
		U.log("All Records Size ::"+allRows.size());
/*		for(String[] data : allRows){
			U.log(Arrays.toString(data));
		}*/
		
		if(allRows.size() > 0){
			insertDataInDB(allRows);
		}
	}
	
	private void insertDataInDB(List<String[]> allRows){
		U.log("Start inserting at updatedDataset table at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			U.log("Commit mode ::"+conn.getAutoCommit());
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertQueryAtUpdatedTable);
			
			
			for(String[] row : allRows){
			
				for(int i = 0; i < row.length; i++){
					pstmt.setString(i+1, row[i]);
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
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
		U.log("Total inserting at updatedDataset table at database ::"+x);
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
	
	private List<String[]>  loadFile(String fileName){
		CSVReader reader = null;
		List<String[]>  readLines = null;
		try{
			reader = new CSVReader(new FileReader(fileName));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		readLines.remove(0);
		U.log("Load input file...... Done");
		U.log("Size of record is ::"+readLines.size());
		return readLines;
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
