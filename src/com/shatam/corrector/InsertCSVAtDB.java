package com.shatam.corrector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class InsertCSVAtDB {
	Connection conn = null;
	public InsertCSVAtDB() {
		conn = DBConnection.getConnection();
	}
	
	private static String insertQuery = "INSERT INTO octdataset (ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"+
			"FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL,EMP_COUNT,YEARS_IN_BIZ,LONGITUDE,LATITUDE) "+
			"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	int count = 0;
	int j = 0;
	
	public void writeCscAtDB(){

		U.log("Start inserting in db....");
		FileWriter writer= null;
		CSVReader reader = null;
		PreparedStatement pstmt = null;
		
		String [] nextLine = null;


		try {
			writer = new FileWriter(Path.DB_CACHE+"Insert_Failed_Data.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

/*		try {
			pstmt = conn.prepareStatement(insertQuery);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
*/		
		try {
			reader = new CSVReader(new FileReader(Path.DB_CACHE+"NEW_DATA_OCT.csv"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int i = 0;	
		try {
			while ((nextLine = reader.readNext()) != null) {
				U.log(i);
				count += updateQuery(pstmt, nextLine, writer);			
//				if(i == 50)break;
				i++;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//eof while

		

		U.log("Total insert queries :"+count);
		U.log("Total failed insert quries :"+j);
		try{
			writer.flush();
			writer.close();
			reader.close();
			pstmt.close();
			conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	synchronized public int updateQuery(PreparedStatement pstmt, String [] nextLine, FileWriter writer){
		

		try {
			pstmt = conn.prepareStatement(insertQuery);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		

		int i = 0;
		try {
			i = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				U.log(Arrays.toString(nextLine));
				j++;
				writer.write(Arrays.toString(nextLine)+"\n");
			} catch (IOException iex) {
				iex.printStackTrace();
			}
		}
		
		return i;
		
	}//eof updateQuery()
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InsertCSVAtDB  insert = new InsertCSVAtDB();
		insert.writeCscAtDB();
	}

}
