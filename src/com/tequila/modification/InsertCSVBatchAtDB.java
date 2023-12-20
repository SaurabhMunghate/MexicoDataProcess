package com.tequila.modification;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.language.Soundex;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class InsertCSVBatchAtDB {

	Connection conn = null;

	public InsertCSVBatchAtDB() {
//		conn = DBConnection.getConnection("/home/glady/Tequila/database/", "tequila.db");
		conn = DBConnection.getConnection("/home/glady/MexicoCache/data/", "tequila.db");
	}

/*	private static String insertQuery = "INSERT INTO dataset (OLD_ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE,SCORE) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
*/
	private static String insertQuery = "INSERT INTO dataset (OLD_ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE,SCORE,_SOURCE_URL,STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String[] shortNames = { "OLD_ID", "INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD", "CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };
	
	int count = 0;
	int k = 0;
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
	
	public void writeBatchAtDB() throws IOException {

		FileWriter writer = null, writer1 = null;
		CSVReader reader = null;
		PreparedStatement pstmt = null;
		
		Soundex soundex = new Soundex();
		
		HashSet<String> hashset = new HashSet<String>();
		String[] nextLine = null;


		// write at file
		try {
/*			writer = new FileWriter(Path.WRONG_COMPOSITE_DATA_CACHE + "Insert_Failed_Data.txt",true);
			writer1 = new FileWriter(Path.WRONG_COMPOSITE_DATA_CACHE + "wrong_composite_data_tequila_db.txt",true);
*/			
			writer = new FileWriter("/home/glady/MexicoCache/data/" + "Insert_Failed_Data_1.txt",true);
			writer1 = new FileWriter("/home/glady/MexicoCache/data/" + "wrong_composite_data_.txt",true);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<String> listEntries = null;
		// read csv file
		try {
			listEntries = Files.readAllLines(java.nio.file.Paths.get("/home/glady/mexicoProject/octoberDataset.tsv"));
			System.out.println(listEntries.size());

		} catch (FileNotFoundException e1) {
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
				// System.out.println(i + "\t" + nextLine.length + "\t"
				// + Integer.parseInt(nextLine[0]));
				String line = itr.next();
				nextLine = line.split("\t");
				if (nextLine.length != 24)
					continue;
				// System.out.println(nextLine.length);
				if (i == 10) {
					// break;
				}
				// `System.out.println(nextLine[0]);
				// pstmt.setInt(1, null);

				String uniqueKey = nextLine[7].trim() + nextLine[8].trim() + nextLine[10].trim() + nextLine[11].trim();
//				String uniqueKey = soundex.encode(nextLine[7].trim()) + nextLine[8].trim() + nextLine[10].trim() + nextLine[11].trim();
				
				if (!hashset.add(uniqueKey)){
					writer1.write(line+"\n");
					continue;
				}
				pstmt.setString(1, nextLine[0]);
				pstmt.setString(2, nextLine[1]);
				pstmt.setString(3, nextLine[2]);
				pstmt.setString(4, nextLine[3]);
				pstmt.setString(5, nextLine[4]);
				pstmt.setString(6, nextLine[5]);
				pstmt.setString(7, nextLine[6]);
				pstmt.setString(8, nextLine[7]);
				pstmt.setString(9, nextLine[8]);
				pstmt.setString(10, nextLine[9]);
				pstmt.setString(11, nextLine[10]);
				pstmt.setString(12, nextLine[11]);
				pstmt.setString(13, nextLine[12]);
				pstmt.setString(14, nextLine[13]);
				pstmt.setString(15, nextLine[14]);
				pstmt.setString(16, nextLine[15]);
				pstmt.setString(17, nextLine[16]);
				pstmt.setString(18, nextLine[17]);
				pstmt.setString(19, nextLine[18]);
				pstmt.setString(20, nextLine[19]);
				pstmt.setString(21, nextLine[20]);
				pstmt.setString(22, nextLine[21]);
				pstmt.setString(23, nextLine[22]);
				pstmt.setString(24, nextLine[23]);
				
				pstmt.setFloat(25, calculateScore(shortNames,nextLine));
				pstmt.setString(26, "");
				pstmt.setString(27, "");
				pstmt.setString(28, "");
				pstmt.setString(29, "");
				pstmt.addBatch();

				// updateQuery(pstmt, nextLine);

				if ((++i % 10000) == 0) {
					System.out.println("Commit the batch");

					updateCount = pstmt.executeBatch();

					System.out.println("Number of rows inserted: "
							+ updateCount.length + "\t" + i);

					conn.commit();

				}
			}// end while

			System.out.println("exit");

			try {
				updateCount = pstmt.executeBatch();
				System.out.println("Update count =" + updateCount.length);
			} catch (BatchUpdateException buex) {
				int[] updateCounts = buex.getUpdateCounts();

				for (int j = 0; j < updateCounts.length; j++) {
					if (updateCounts[j] >= 0) {
						// Successfully executed; the number represents number
						// of affected rows
						writer.write("Successfully executed: number of affected rows: "
								+ updateCounts[j] + "\n");
					} else if (updateCounts[j] == PreparedStatement.SUCCESS_NO_INFO) {
						// Successfully executed; number of affected rows not
						// available
						writer.write("Successfully executed: number of affected rows not available: "
								+ updateCounts[j] + "\n");
					} else if (updateCounts[j] == PreparedStatement.EXECUTE_FAILED) {
						// Failed to execute
						writer.write("Failed to execute: " + updateCounts[j]
								+ "\n");
					}
				}
			}
		} catch (IOException | SQLException e1) {
			e1.printStackTrace();
		}// eof while

		System.out.println("Total insert queries :" + count);

		try {
			conn.commit();
			conn.setAutoCommit(true);
			writer.flush();
			writer.close();
			writer1.flush();
			writer1.close();
			// reader.close();
			pstmt.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	public float calculateScore(final String[] shortNames, final String[] vals) {  
		//int score = 0;
		int score = 100;  //Score of generated ID is always 100, In vals array we don't have ID
		for (int i = 0; i < shortNames.length; i++){
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
	

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		InsertCSVBatchAtDB insert = new InsertCSVBatchAtDB();
		insert.writeBatchAtDB();

		long endTime = System.currentTimeMillis();

	}
}
