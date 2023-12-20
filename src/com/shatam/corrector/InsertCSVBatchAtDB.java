package com.shatam.corrector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class InsertCSVBatchAtDB {

	Connection conn = null;
	
	public InsertCSVBatchAtDB(){
		conn = DBConnection.getConnection("sample.db");
	}
	
	private static String insertQuery = "INSERT INTO octdataset (ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"+
			"FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE) "+
			"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	int count = 0;
	int k = 0;
	
	public void writeBatchAtDB(){

		U.log("Start inserting in db....");
		FileWriter writer= null;
		CSVReader reader = null;
		PreparedStatement pstmt = null;
		
		String [] nextLine = null;

		//write at file
		try {
			writer = new FileWriter(Path.DB_CACHE+"Insert_Failed_Data_1.txt",true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//read csv file
		try {
			reader = new CSVReader(new FileReader(Path.DB_CACHE+"NEW_DATA_OCT.csv"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		int i = 0;	
		try {
			pstmt = conn.prepareStatement(insertQuery);
			conn.setAutoCommit(false);
			//i
			while ((nextLine = reader.readNext()) != null) {
				U.log(i+"\t"+nextLine.length+"\t"+Integer.parseInt(nextLine[0]));

				pstmt.setInt(1,i );				
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
				
				pstmt.addBatch();
				pstmt.clearParameters();

//				updateQuery(pstmt, nextLine);

				if (i == 100000) {
//					i = 0;
					break;
				}
				i++;
			}//end while
			int updateCount[];
			try {
				updateCount = pstmt.executeBatch();
				U.log("Update count ="+updateCount.length);
			}catch(BatchUpdateException buex){
				int [] updateCounts = buex.getUpdateCounts();
			 
				for (int j=0; j<updateCounts.length; j++) {
					if (updateCounts[j] >= 0) {
					    // Successfully executed; the number represents number of affected rows
						writer.write("Successfully executed: number of affected rows: "+updateCounts[j]+"\n");
					} else if (updateCounts[j] == PreparedStatement.SUCCESS_NO_INFO) {
					    // Successfully executed; number of affected rows not available
						writer.write("Successfully executed: number of affected rows not available: "+updateCounts[j]+"\n");
					} else if (updateCounts[j] == PreparedStatement.EXECUTE_FAILED) {
						// Failed to execute
						writer.write("Failed to execute: "+updateCounts[j]+"\n");
					}
		        }
			}
		}catch(IOException | SQLException  e1 ) {
			e1.printStackTrace();
		}//eof while

		U.log("Total insert queries :"+count);
//		U.log("Total failed insert quries :"+k);
		try{
			conn.commit();
			conn.setAutoCommit(true);
			writer.flush();
			writer.close();
			reader.close();
			pstmt.close();
			conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/*	private void updateBatch(PreparedStatement pstmt) {
		// TODO Auto-generated method stub
		try{
			pstmt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public void updateQuery(PreparedStatement pstmt, String [] nextLine){

//		U.log(Arrays.toString(nextLine));
		
		try {
			pstmt.clearParameters();
			pstmt.setInt(1, Integer.parseInt(nextLine[0]));
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
			pstmt.addBatch();

		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}		
	}//eof updateQuery()

	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		InsertCSVBatchAtDB insert =  new InsertCSVBatchAtDB();
		insert.writeBatchAtDB();
		
		long endTime   = System.currentTimeMillis();
		U.log((endTime - startTime)+" milliSecond");
	}
}
