package com.shatam.corrector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class GenerateDuplicatedErrorFile {

	Connection conn = null;
	
	private GenerateDuplicatedErrorFile() {
		conn = DBConnection.getConnection("tequila.db");
	}
	
	private static String query = "select ID from octdataset where ID=?";
	
	private void writeErrorFile(){
		CSVReader reader = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String [] nextLine = null;
		
		FileWriter writer= null;
		
		U.log("Start searching in db....");

		try {
			writer = new FileWriter(Path.DB_CACHE+"Duplicate_Entries.txt",true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			reader = new CSVReader(new FileReader(Path.DB_CACHE+"NEW_DATA_OCT.csv"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		int i = 0;
		int count = 0;

		try{
			pstmt = conn.prepareStatement(query);
			
			while ((nextLine = reader.readNext()) != null) {
				i++;
				pstmt.clearParameters();
				pstmt.setInt(1, Integer.parseInt(nextLine[0]));
				rs = pstmt.executeQuery();
				if(!rs.next()){
					count++;
					U.log(count+">>>>time :"+System.currentTimeMillis());
					writer.write(Arrays.toString(nextLine)+"\n");
				}
			}
		}catch(SQLException | IOException e1){
			e1.printStackTrace();
		}
		
		try{
			rs.close();
			pstmt.close();
			writer.flush();
			writer.close();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Total Read :"+i);
		U.log("Total cound of missing entries :"+count);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		GenerateDuplicatedErrorFile error = new GenerateDuplicatedErrorFile();
		error.writeErrorFile();
		long endTime   = System.currentTimeMillis();
		U.log((endTime - startTime)+" milliSecond");
	}

}
