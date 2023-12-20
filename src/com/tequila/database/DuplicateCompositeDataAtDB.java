package com.tequila.database;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.U;

public class DuplicateCompositeDataAtDB {

	Connection conn = null;

	private static final String insertQuery = "INSERT INTO datasetdup(INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"			
	+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "			
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	private static final String[] shortNames = { "ID","INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD","CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };

	
	private static final String DUP_COMPOSITE_KEY_DATA_FILE_PATH = "/home/glady/testdemo/Tequila/database/duplicate_composite_data.txt";
	
	public static void main(String[] args) {
		DuplicateCompositeDataAtDB process = new DuplicateCompositeDataAtDB();
		process.insertDuplicateCompositeData(DUP_COMPOSITE_KEY_DATA_FILE_PATH);
	}

	/**
	 * This method is used to insert data at duplicate tequila db.<br> 
	 * Duplicate composite key data with 24 column field will be insert at duplicate tequila db. 
	 */
	private final void insertDuplicateCompositeData(String filePath){
		
		conn =  DBConnection.getConnection(TestPath.DB_PATH, TestPath.DUP_DB_NAME);
		
		if(conn == null){
			U.log("Database connection is not established..");
			System.exit(0);
		}
		U.log("Database connection is established..");
		PreparedStatement pstmt = null;
		
		String[] nextLine = null;
		List<String> listEntries = null;
		// read csv file
		try {
			listEntries = Files.readAllLines(Paths.get(filePath));
			System.out.println(listEntries.size());

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// System.exit(1);

		int updateCount[];
		int i = 0;
		try {
			pstmt = conn.prepareStatement(insertQuery);
			Iterator<String> itr = listEntries.iterator();
			conn.setAutoCommit(false);
			// i
			while (itr.hasNext()) {

				String line = itr.next();
				nextLine = line.split("\t");
				if (nextLine.length != 24)
					continue;

			
				if(nextLine[4].trim().length() == 3)
					nextLine[4] = "0"+nextLine[4].trim();
						
				pstmt.setString(1, U.toTitleCase(nextLine[1])); //INDUSTRY_SECTOR
				pstmt.setString(2, U.toTitleCase(nextLine[2])); //SPANISH_INDUSTRY_SECTOR
				pstmt.setString(3, U.toTitleCase(nextLine[3])); //SIC_MAJOR
				pstmt.setString(4, U.toTitleCase(nextLine[4])); //SIC_SUB
				pstmt.setString(5, U.toTitleCase(nextLine[5])); //PRODUCT_DESC
				pstmt.setString(6, U.toTitleCase(nextLine[6])); //SPANISH_PRODUCT_DESC
				pstmt.setString(7, U.toTitleCase(nextLine[7])); //COMPANY_NAME
				
				pstmt.setString(8, U.toTitleCase(nextLine[8])); //ADDRESS
				pstmt.setString(9, U.toTitleCase(nextLine[9])); //NEIGHBORHOOD
				
				pstmt.setString(10, U.toTitleCase(nextLine[10])); //CITY
				
				pstmt.setString(11, U.toTitleCase(nextLine[11])); //STATE
				
				String zip = nextLine[12].trim();
				if(zip.length() == 4){
					zip = "0"+zip;
				}
				pstmt.setString(12, zip); //ZIP
				
				pstmt.setString(13, U.formatNumbersAsCode(nextLine[13])); //PHONE
				
				pstmt.setString(14, U.formatNumbersAsCode(nextLine[14])); //FAX
				
				pstmt.setString(15, nextLine[15].toLowerCase()); //URL //web url no need to format
				
				if (nextLine[16].trim().length()!=0 && nextLine[16].endsWith(";")) {
					nextLine[16]=nextLine[16].toLowerCase().substring(0, nextLine[16].length()-1);  //EMAIL
				}
				pstmt.setString(16, nextLine[16]); //EMAIL //Email no need to format
				
				if (nextLine[17].trim().length()!=0 && nextLine[17].endsWith(";")) {
					nextLine[17]=nextLine[17].toLowerCase().substring(0, nextLine[17].length()-1); //CONTACT_PERSON
				}
				pstmt.setString(17, U.toTitleCase(nextLine[17])); //CONTACT_PERSON
				
				if (nextLine[18].trim().length()!=0 && nextLine[18].endsWith(";")) {
					nextLine[18]=nextLine[18].toLowerCase().substring(0, nextLine[18].length()-1); //TITLE
				}
			
				pstmt.setString(18, U.toTitleCase(nextLine[18])); //TITLE
				
				
				pstmt.setString(19, U.toTitleCase(nextLine[19])); //ANNUAL_SALES_VOL
				
				
				pstmt.setString(20, U.toTitleCase(nextLine[20])); //EMP_COUNT
				pstmt.setString(21, U.toTitleCase(nextLine[21])); //YEARS_IN_BIZ
				
				
				pstmt.setString(22, U.formatLongitude(nextLine[22])); //LONGITUDE
				
				pstmt.setString(23, nextLine[23].trim());  //LATITUDE
				
				pstmt.setFloat(24, calculateScore(shortNames,nextLine));  //SCORE
				pstmt.setString(25, "");   //_SOURCE_URL
				pstmt.setString(26, "");   //STATUS
				pstmt.setString(27, "");   //_LAST_UPDATED_BY
				pstmt.setString(28, "");   //_LAST_UPDATED
				pstmt.addBatch();

				if ((++i % 10000) == 0) {

					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");

				}
			}// end while

			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		System.out.println("Total insert queries :" + i);

		try {
			conn.setAutoCommit(true);
			pstmt.close();
			conn.close();

		} catch (Exception e) {
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
