package com.tequila.modification;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import com.database.connection.DBConnection;

public class FindDuplication {
	
	Connection conn = null;
	
	public FindDuplication() {
		conn = DBConnection.getConnection("/home/glady/MexicoCache/", "duplicatesample.db");
	}

	private static String insertQuery = "INSERT INTO sampledata (OLD_ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * This method is used to insert those data in other database which have duplicate composite key and it not inserted at unique database 
	 */
	private void findDuplication(){
		List<String> listEntries = null;
		List<String> listEntries1 = null;
		String[] nextLine = null;
		int updateCount[];
		int i = 0;
		PreparedStatement pstmt = null;
		// read csv file
		try {
			listEntries = Files.readAllLines(java.nio.file.Paths.get("/home/glady/Tequila/duplicate_composite_data/wrong_composite_data_tequila_db.txt"));
			listEntries1 = Files.readAllLines(java.nio.file.Paths.get("/home/glady/Tequila/duplicate_composite_data/wrong_composite_data_tequila_db_1.txt"));
			listEntries.addAll(listEntries1);
			System.out.println(listEntries.size());
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			pstmt = conn.prepareStatement(insertQuery);
			Iterator<String> itr = listEntries.iterator();
			conn.setAutoCommit(false);

			while (itr.hasNext()) {
				String line = itr.next();
				nextLine = line.split("\t");

//				String uniqueKey = nextLine[7].trim() + nextLine[8].trim() + nextLine[10].trim() + nextLine[11].trim();
				
				if(nextLine.length == 24){
					int k = 1;
					for(int j = 0; j< nextLine.length; j++){
						pstmt.setString(k, nextLine[j]);
						k++;
					}
				}else{
					int k = 1;
					for(int j = 0; j< nextLine.length; j++){
						pstmt.setString(k, nextLine[j]);
						k++;
					}
					for(int j = nextLine.length; j< 24; j++){
						pstmt.setString(k, "");
						k++;
					}
				}
				pstmt.addBatch();
				
				if ((++i % 10000) == 0)
				{
					System.out.println("Commit the batch");
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
				}
//				System.out.println("exit");
			}
			
			updateCount = pstmt.executeBatch();
			System.out.println("Update count =" + updateCount.length);
			
		}catch(Exception e1){
			e1.printStackTrace();
		}
		try {
			conn.commit();
			conn.setAutoCommit(true);

			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) {
		FindDuplication  dup = new FindDuplication();
		dup.findDuplication();
	}

}
