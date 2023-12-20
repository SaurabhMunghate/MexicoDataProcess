package com.priti.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import com.database.connection.Connect;
import com.opencsv.CSVWriter;
import com.shatam.utils.MXStates;
import com.shatam.utils.Util;

public class FetchInCorrectZipFromDB extends Connect{
	
	static ArrayList<String[] > dbList = new ArrayList<>();
	static ArrayList<String[] > wrongZIPindbList = new ArrayList<>();
	public static void main(String[] args) throws IOException, SQLException {
		FetchInCorrectZipFromDB fetch = new FetchInCorrectZipFromDB();
		fetch.fetchDBRecords();
		fetch.disconnect();
		if(dbList.size()>0){
			FetchWrongZip();
		}

	}
	private static void FetchWrongZip() throws IOException {
		CSVWriter zipWriter = new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_Records_Wrong_Zip_25_09_2018.csv"));
		String [] header = {"ID","COMPANY_NAME","URL","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP"};
		zipWriter.writeNext(header);
		
		for(String[] dbRow : dbList){
			System.out.println(Arrays.toString(dbRow));
			
			String dbState = dbRow[6].trim();
			int dbZip = Integer.parseInt(dbRow[7].trim().replace("-", ""));
			System.out.println("State : "+ dbState+"\t Zip : "+dbZip);
			if(dbZip>MXStates.mexicoPostalCodeRange.get(dbState)[1] || dbZip<MXStates.mexicoPostalCodeRange.get(dbState)[0]){
				wrongZIPindbList.add(dbRow);
			}
			else{
				System.out.println("Valid");
			}
			
			//break;
		}
		zipWriter.writeAll(wrongZIPindbList);
		zipWriter.close();
		System.out.println("Total No. of Records from DB where state and zip are present ::::::"+dbList.size());
		System.out.println("Wrong zip Count::::::"+wrongZIPindbList.size());
	}
	private void fetchDBRecords() throws IOException, SQLException {
		
		CSVWriter writer = new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_Records_25_09_2018.csv"));
		String [] header = {"ID","COMPANY_NAME","URL","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP"};
		writer.writeNext(header);
		int count = 0 ;
		String query = "select id,company_name,url,address,neighborhood,city,state,zip from dataset where state is not null and zip is not null";
		//String query = "select id,company_name,url,address,neighborhood,city,state,zip,phone from dataset where state = \""+myState+"\" and phone is not null and city is not null and _source_url is not null";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		System.out.println(rs);
		try{
			 while(rs.next()){
				 
				 String [] result = {rs.getString(1).trim(),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)};
				 dbList.add(result);
				 writer.writeNext(result);
				 count++;
			 }
			 System.out.println("Count of records : " + count);
			 writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
