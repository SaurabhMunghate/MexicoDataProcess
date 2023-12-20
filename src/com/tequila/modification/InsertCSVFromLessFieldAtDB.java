package com.tequila.modification;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class InsertCSVFromLessFieldAtDB {
	
	Connection conn = null;
	
	public InsertCSVFromLessFieldAtDB(){
//		conn = DBConnection.getConnection("/home/glady/Tequila/wrong_record/", "sample.db");
		conn = DBConnection.getConnection("/home/glady/MexicoCache/data/", "tequila.db");
	}


	private static String insertQuery = "INSERT INTO dataset (OLD_ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE,SCORE,_SOURCE_URL,STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static String[] shortNames = { "OLD_ID", "INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD", "CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };

	DecimalFormat df = new DecimalFormat("#.0000");
	
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
	
	HashSet<String> hashset = new HashSet<String>();
	
	private void loadCompositeKey(){
		List<String> listEntries = null;
		// read csv file
		try {
			listEntries = Files.readAllLines(java.nio.file.Paths.get("/home/glady/mexicoProject/octoberDataset.tsv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(listEntries.size());

		String [] nextLine = null;
		try {
			Iterator<String> itr = listEntries.iterator();
			while (itr.hasNext()) {
				String line = itr.next();
				nextLine = line.split("\t");
				if (nextLine.length != 24)
					continue;

				String uniqueKey = nextLine[7].trim() + nextLine[8].trim() + nextLine[10].trim() + nextLine[11].trim();

				if (!hashset.add(uniqueKey)){
					continue;
				}
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
//	private void insertAtDB(String path, String fileName, String insertQuery, String [] shortNames){
	private void insertAtDB(String path, String fileName){
		
		List<String> listEntries = null;
		FileWriter writer= null, writer1= null;
		PreparedStatement pstmt = null;

		
		try {
			listEntries = Files.readAllLines(java.nio.file.Paths.get(path+fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(listEntries.size());
		try {
			writer = new FileWriter("/home/glady/MexicoCache/data/" + "wrong_composite_data_tequila_db.txt",true);
			writer1 = new FileWriter("/home/glady/MexicoCache/data/" + "wrong_lenght_at_tequila_db.txt",true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		U.log(">>"+shortNames.length);
		int updateCount[];
		int i = 0;
		String [] nextLine = null;
		try {
			pstmt = conn.prepareStatement(insertQuery);
			Iterator<String> itr = listEntries.iterator();
			conn.setAutoCommit(false);
			// i
			while (itr.hasNext()) {
				String line = itr.next();
				nextLine = line.split("\t");
				
//				U.log(">>"+nextLine.length);
				
/*				if(shortNames.length != nextLine.length){
					writer1.write(line+"\n");
					writer1.write(">>"+nextLine.length+"\n");
					continue;
				}
*/				
				String uniqueKey = nextLine[7].trim() + nextLine[8].trim() + nextLine[10].trim() + nextLine[11].trim();

				if (!hashset.add(uniqueKey)){
					writer.write(line+"\n");
					continue;
				}
/*				int k = 1;
				for(int j = 0; j <shortNames.length; j++){
					pstmt.setString(k++, nextLine[j]);
				}
				pstmt.setFloat(shortNames.length, calculateScore(shortNames,nextLine));
*/				
				{
					int k = 1;
					for(int j = 0; j< nextLine.length; j++){
						pstmt.setString(k, nextLine[j]);
						k++;
					}
					for(int j = nextLine.length; j< 24; j++){
						pstmt.setString(k, "");
						k++;
					}
//					U.log(k);
					pstmt.setFloat(25, calculateScore(shortNames,nextLine));
					pstmt.setString(26, "");
					pstmt.setString(27, "");
					pstmt.setString(28, "");
					pstmt.setString(29, "");
					pstmt.addBatch();

					nextLine = null;
				}
				
				// updateQuery(pstmt, nextLine);

				if ((++i % 100) == 0)
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
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			conn.commit();
			conn.setAutoCommit(true);
			writer.flush();
			writer.close();
			writer1.flush();
			writer1.close();
			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public float calculateScore(final String[] shortNames, final String[] vals) {  
		//int score = 0;
		int score = 100;  //Score of generated ID is always 100, In vals array we don't have ID
//		for (int i = 0; i < shortNames.length; i++){
		for (int i = 0; i < vals.length; i++){
			if (U.isEmpty(vals[i]))    
				continue;  
			
//			if(i == vals.length)break;
			
			String shortName = shortNames[i];
			
			if (U.isEmpty(shortName))
				continue;
			
			score += scoreMap.get(shortName); 
			
		}//for i
		
	 	float avgScore = (100 * score) / 1060;
	 	
		return Float.parseFloat(df.format(avgScore));
	}
	
	public static void main(String[] args) {

		String path = "/home/glady/Tequila/wrong_record/";
		InsertCSVFromLessFieldAtDB insert = new InsertCSVFromLessFieldAtDB();
		
		insert.loadCompositeKey();
		
/*		insert.insertAtDB(path, "23_wrongList.txt", insertQueryFor_22_23_Field, shortNamesFor_22_23_Field);
		insert.insertAtDB(path, "22_wrongList.txt", insertQueryFor_22_23_Field, shortNamesFor_22_23_Field);
		insert.insertAtDB(path, "21_wrongList.txt", insertQueryFor_21_Field, shortNamesFor_21_Field);
		insert.insertAtDB(path, "20_wrongList.txt", insertQueryFor_20_Field, shortNamesFor_20_Field);
		insert.insertAtDB(path, "19_wrongList.txt", insertQueryFor_19_Field, shortNamesFor_19_Field);
		insert.insertAtDB(path, "18_wrongList.txt", insertQueryFor_18_Field, shortNamesFor_18_Field);
		insert.insertAtDB(path, "17_wrongList.txt", insertQueryFor_17_Field, shortNamesFor_17_Field);
		insert.insertAtDB(path, "16_wrongList.txt", insertQueryFor_16_Field, shortNamesFor_16_Field);
		insert.insertAtDB(path, "15_wrongList.txt", insertQueryFor_15_Field, shortNamesFor_15_Field);
		insert.insertAtDB(path, "14_wrongList.txt", insertQueryFor_14_Field, shortNamesFor_14_Field);
		insert.insertAtDB(path, "13_wrongList.txt", insertQueryFor_13_Field, shortNamesFor_13_Field);
*/
		insert.insertAtDB(path, "23_wrongList.txt");
		insert.insertAtDB(path, "22_wrongList.txt");
		insert.insertAtDB(path, "21_wrongList.txt");
		insert.insertAtDB(path, "20_wrongList.txt");
		insert.insertAtDB(path, "19_wrongList.txt");
		insert.insertAtDB(path, "18_wrongList.txt");
		insert.insertAtDB(path, "17_wrongList.txt");
		insert.insertAtDB(path, "16_wrongList.txt");
		insert.insertAtDB(path, "15_wrongList.txt");
		insert.insertAtDB(path, "14_wrongList.txt");
		insert.insertAtDB(path, "13_wrongList.txt");
		try {
			insert.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
