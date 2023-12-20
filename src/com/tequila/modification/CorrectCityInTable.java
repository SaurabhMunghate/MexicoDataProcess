package com.tequila.modification;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;



public class CorrectCityInTable {

	public static final String ALLOW_BLANK = "-";
	
	ArrayList<String> cityList = new ArrayList<String>();
	public void loadCityFromDB(){
		String query1 = "select city from sample1 group by city order by city asc";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBConnection.getConnection();
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query1);
			while(rs.next()){
				cityList.add(rs.getString(1).trim());
			}
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException ex){	
			ex.printStackTrace();
		}
	}
	
	com.opencsv.CSVWriter writer = null;
	public void checkNonCity() throws IOException{
		writer = new com.opencsv.CSVWriter(new FileWriter(Path.CACHE_PATH+"WrongCityList.csv"), '\t');
		String header[] = {"Index","CITY","CORRECT STATE"};
		writer.writeNext(header);
		
		int i = 0;
		for(String city : cityList){
			boolean found = U.isState(city);
			if(found){
//				U.log(city);
				 String correctState = U.matchState(city);
				writer.writeNext(new String[]{String.valueOf(i++),city,correctState});
			}
		}
		U.log("Size ::"+cityList.size());
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		CorrectCityInTable correct = new CorrectCityInTable();
		correct.loadCityFromDB();
		correct.checkNonCity();
	}
}
