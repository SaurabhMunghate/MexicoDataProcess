package com.tequila.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.U;

public class CopyDBToDB {

	private static final String insertQuery = "INSERT INTO dataset(INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"			
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "			
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String[] shortNames = { "ID","INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD","CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };

	private static final String MAIN_DB_PATH = "/home/glady/MexicoCache/database/trial_db/";
	private static final String MAIN_DB_NAME = "tequilaFinal.db";
	
	private static final String COPY_DB_PATH = "/home/glady/testdemo/Tequila/database/trail_db/";
	private static final String COPY_DB_NAME = "tequiladup.db"; //"tequila.db";
	
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/glady/MexicoCache/database/trial_db/uniqueKeyTequila.ser";
	
	Connection conn1 = null,conn2 = null;
	private CopyDBToDB(){
		conn1 = DBConnection.getConnection(MAIN_DB_PATH, MAIN_DB_NAME);
		conn2 = DBConnection.getConnection(COPY_DB_PATH, COPY_DB_NAME);
	}
	
	public static void main(String[] args) {
		CopyDBToDB copy = new CopyDBToDB();
		copy.copyDBToDB();
		copy.disconnect();
	}
	
	private void copyDBToDB(){
		
		int newRecordCount = 0;
		List<String[]> dataset = new ArrayList<>();
		
		U.log("Loading..... composite key here...");
		CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();

		/*
		 * load composite key from serialize file
		 */
		HashSet<String> uniqueKeyHashSet =chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
		U.log("Composite key set size:::"+uniqueKeyHashSet.size());
		

		U.log("Start reading copy db here...");
		//String query = "select * from dataset"; //tequila.db
		String query = "select * from datasetdup"; //tequiladup.db
		/*
		 * Read db which we want to copy.
		 */
		try(Statement stmt = conn2.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				String uniqueKey = rs.getString("SIC_SUB").trim() +
						rs.getString("COMPANY_NAME").trim().trim() + 
						rs.getString("ADDRESS").trim().trim() + 
						rs.getString("CITY").trim().trim() +
						rs.getString("STATE").trim().trim() +
						rs.getString("CONTACT_PERSON").trim().trim();
				if (!uniqueKeyHashSet.add(uniqueKey.toLowerCase())) {
					continue;
				}
				newRecordCount++;
				
				dataset.add(new String[]{
					rs.getString("ID"),
					rs.getString("INDUSTRY_SECTOR"),
					rs.getString("SPANISH_INDUSTRY_SECTOR"),
					rs.getString("SIC_MAJOR"),
					rs.getString("SIC_SUB"),
					rs.getString("PRODUCT_DESC"),
					rs.getString("SPANISH_PRODUCT_DESC"),
					rs.getString("COMPANY_NAME"),
					rs.getString("ADDRESS"),
					rs.getString("NEIGHBORHOOD"),
					rs.getString("CITY"),
					rs.getString("STATE"),
					rs.getString("ZIP"),
					rs.getString("PHONE"),
					rs.getString("FAX"),
					rs.getString("URL"),
					rs.getString("EMAIL"),
					rs.getString("CONTACT_PERSON"),
					rs.getString("TITLE"),
					rs.getString("ANNUAL_SALES_VOL"),
					rs.getString("EMP_COUNT"),
					rs.getString("YEARS_IN_BIZ"),
					rs.getString("LONGITUDE"),
					rs.getString("LATITUDE"),
					rs.getString("_SCORE"),
					rs.getString("_SOURCE_URL"),
					rs.getString("_STATUS"),
					rs.getString("_LAST_UPDATED_BY"),
					rs.getString("_LAST_UPDATED")
				});
			}//eof while
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Reading done..");
		U.log("Newly added record ::"+newRecordCount);
		U.log("Size of newly data record :"+dataset.size());
		
		U.log("Sending dataset record for insert at main database here.....");
		insertRecordAtDB(dataset, uniqueKeyHashSet);
		
	}
	
	private final void insertRecordAtDB(List<String[]> dataset, HashSet<String> uniqueKeyHashSet){
		
		U.log("Start inserting record here.........");
		PreparedStatement pstmt = null;		
		String[] nextLine = null;
		int updateCount[];
		int i = 0;
		try {
			pstmt = conn1.prepareStatement(insertQuery);
			Iterator<String[]> itr = dataset.iterator();
			conn1.setAutoCommit(false);
			
			while (itr.hasNext()) {
				nextLine = itr.next();
				
				pstmt.setString(1, nextLine[1].trim()); //INDUSTRY_SECTOR
				pstmt.setString(2, nextLine[2].trim()); //SPANISH_INDUSTRY_SECTOR
				pstmt.setString(3, nextLine[3].trim()); //SIC_MAJOR
				pstmt.setString(4, nextLine[4].trim()); //SIC_SUB
				pstmt.setString(5, nextLine[5].trim()); //PRODUCT_DESC
				pstmt.setString(6, nextLine[6].trim()); //SPANISH_PRODUCT_DESC
				pstmt.setString(7, nextLine[7].trim()); //COMPANY_NAME
				
				pstmt.setString(8, nextLine[8].trim()); //ADDRESS
				pstmt.setString(9, nextLine[9].trim()); //NEIGHBORHOOD				
				pstmt.setString(10, nextLine[10].trim()); //CITY		
				pstmt.setString(11, nextLine[11].trim()); //STATE				
				pstmt.setString(12, nextLine[12].trim()); //ZIP
				
				pstmt.setString(13, nextLine[13].trim()); //PHONE
				pstmt.setString(14, nextLine[14].trim()); //FAX
				
				pstmt.setString(15, nextLine[15].toLowerCase()); //URL //web url no need to format
				pstmt.setString(16, nextLine[16].trim()); //EMAIL //Email no need to format
				
				pstmt.setString(17, nextLine[17].trim()); //CONTACT_PERSON
				pstmt.setString(18, nextLine[18].trim()); //TITLE
				
				pstmt.setString(19, nextLine[19].trim()); //ANNUAL_SALES_VOL
				pstmt.setString(20, nextLine[20].trim()); //EMP_COUNT
				pstmt.setString(21, nextLine[21].trim()); //YEARS_IN_BIZ
				
				
				pstmt.setString(22, nextLine[22].trim()); //LONGITUDE
				pstmt.setString(23, nextLine[23].trim());  //LATITUDE
				
				pstmt.setString(24, nextLine[24].trim());  //SCORE
				pstmt.setString(25, nextLine[25].trim());   //_SOURCE_URL
				pstmt.setString(26, nextLine[26].trim());   //STATUS
				pstmt.setString(27, nextLine[27].trim());   //_LAST_UPDATED_BY
				pstmt.setString(28, nextLine[28].trim());   //_LAST_UPDATED
				pstmt.addBatch();

				if ((++i % 10000) == 0) {

					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn1.commit();
					System.out.println("Commit the batch");

				}
			}// end while

			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn1.commit();
			System.out.println("Commit the batch");
			conn1.setAutoCommit(true);
			System.out.println("Insertion done....");

			U.log("Send to write composite key here......");
			updateCompositeSer(uniqueKeyHashSet);
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void updateCompositeSer(HashSet<String> uniqueKeyHashSet){
		try {
			U.log("Start creating new composite key ser file here...");
	        FileOutputStream fileOut = new FileOutputStream(COMPOSITE_KEY_TEQUILA_SER);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(uniqueKeyHashSet);
	        out.close();
	        fileOut.close();
	     } catch (IOException ex) {
	        ex.printStackTrace();
	     }
		U.log("Done composite key ser file.");
	}
	
	private void disconnect(){
		try{
			if(!conn1.isClosed()){
				conn1.close();
				conn1 = null;
				U.log("Connection 1 is closed.");
			}
			if(!conn2.isClosed()){
				conn2.close();
				conn2 = null;
				U.log("Connection 2 is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}

	private HashMap<String,Integer> scoreMap = new HashMap<String,Integer>() {
		{
			put("ID", 100);
			put("OLD_ID", 0);
			put("INDUSTRY_SECTOR", 0);
			put("SPANISH_INDUSTRY_SECTOR", 0);
			put("SIC_MAJOR", 0);
			put("SIC_SUB", 100);
			put("PRODUCT_DESC", 0);
			put("SPANISH_PRODUCT_DESC", 0);
			put("COMPANY_NAME", 100);
			put("ADDRESS", 100);
			put("NEIGHBORHOOD", 50);
			put("CITY", 100);
			put("STATE", 100);
			put("ZIP", 100);
			put("PHONE", 50);
			put("FAX", 50);
			put("URL", 25);
			put("EMAIL", 50);
			put("CONTACT_PERSON", 10);
			put("TITLE", 10);
			put("ANNUAL_SALES_VOL", 5);
			put("EMP_COUNT", 5);
			put("YEARS_IN_BIZ", 5);
			put("LONGITUDE", 50);
			put("LATITUDE", 50);
		}
	};
	
	DecimalFormat df = new DecimalFormat("#.0000");
	public final float calculateScore(final String[] shortNames, final String[] vals) {  
		//int score = 0;
		int score = 100;  //Score of generated ID is always 100, In vals array we don't have ID
		for (int i = 1; i < shortNames.length; i++){
			if (U.isEmpty(vals[i]))    
				continue;  
			
			String shortName = shortNames[i];
			
			if (U.isEmpty(shortName))
				continue;
			
			score += scoreMap.get(shortName);  
		}//for i
		
	 	float avgScore = (100 * score) / 1060;
	 	
		return Float.parseFloat(df.format(avgScore));
	}
}
