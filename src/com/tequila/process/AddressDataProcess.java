package com.tequila.process;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.database.connection.Connect;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class AddressDataProcess extends Connect {
	
	String outputFile = Path.CORRECTORS_DIR + "06_Sept/" + "Address_ID_For_Test_6_Sept_18.csv";

	public static void main(String[] args) {
		AddressDataProcess process = new AddressDataProcess();
//		process.searchAddressesFromDB();
//		process.searchAddressWithSN();
//		process.searchAddressesWithHouseOrRoadNumber();
		process.disconnect();
	}
	
	private void searchAddressesWithHouseOrRoadNumber(){
		FileWriter fw = null;
		Set<String> ignoredIdList = new HashSet<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader("/home/glady/MexicoCache/database/Correctors/06_Sept/Address_Process_6_Sept_18.csv");
		for(String[] lines : readLines){
			ignoredIdList.add(lines[0]);
		}
		readLines.clear();
		readLines = U.readCsvFileWithoutHeader("/home/glady/MexicoCache/database/Correctors/06_Sept/Address_Contain_S_N_6_Sept_18.csv");
		for(String[] lines : readLines){
			ignoredIdList.add(lines[1]);
		}
		readLines.clear();
		readLines = null;

		Statement stmt = null;
		int count = 0;
		String [] expression = {"esq","esq.","entry","esquina"};
		boolean flag = false;
		try{
			
			stmt = conn.createStatement();
			fw = new FileWriter(outputFile);
			
			ResultSet rs = stmt.executeQuery("select * from dataset");
			U.log("Start processing ...");
			while(rs.next()){
				
				if(ignoredIdList.contains(rs.getString("id"))) continue;
				
				flag = false;
				for(String exp : expression){
					if(rs.getString("address").toLowerCase().contains(exp)){
						flag = true;
						break;
					}
				}
				if(flag)continue;
	
				if(rs.getString("address").length() <= 50){
					count++;
					fw.write(rs.getString("ID")+"\n");
				}
			}
			fw.flush();
			fw.close();
			
			rs.close();
			stmt.close();
		}catch(SQLException|IOException e ){
			e.printStackTrace();
		}
		U.log("Count of addresses For Testing Geo ::"+count);
	}
	
	private void searchAddressesFromDB(){
		FileWriter fw = null;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			
			fw = new FileWriter(outputFile);
			fw.write("ID\tCOMPANY_NAME\tADDRESS\tNEIGHBORHOOD\tCITY\tSTATE\tZIP\tLATITUDE\tLONGITUDE\n");
			ResultSet rs = stmt.executeQuery("select * from dataset");
			U.log("Start processing ...");
			while(rs.next()){
				String num = Util.match(rs.getString("address"), "\\d+");
				
				if(num != null && num.startsWith("0")){
					U.log(rs.getString("address"));
					fw.write(rs.getString("ID")+"\t"+rs.getString("company_name")+"\t"+rs.getString("address")
								+"\t"+rs.getString("neighborhood")+"\t"+rs.getString("city")+"\t"+rs.getString("state")
								+"\t"+rs.getString("zip")+"\t"+rs.getString("latitude")+"\t"+rs.getString("longitude")+"\n");
				}
			}
			
			rs.close();
			fw.flush();
			fw.close();
			stmt.close();
		}catch(SQLException |IOException e ){
			e.printStackTrace();
		}
		U.log("Output File is done writing at pos :"+outputFile);
	}//eof searchAddressesFromDB()
	
	private void searchAddressWithSN(){
		List<String[]> writeLines = new ArrayList<>();
		
		Statement stmt = null;
		int count = 0;
		try{
			
			stmt = conn.createStatement();

			writeLines.add(new String[]{
					"INDEX","ID","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","LATITUDE","LONGITUDE"		
			});

			ResultSet rs = stmt.executeQuery("select * from dataset");
			U.log("Start processing ...");
			while(rs.next()){
				if(rs.getString("address").toLowerCase().contains(" s/n") || rs.getString("address").toLowerCase().contains(" sn")
						|| rs.getString("address").toLowerCase().contains(",s/n") ||rs.getString("address").toLowerCase().contains(",sn")){
						count++;
					writeLines.add(new String[]{
							""+count, rs.getString("ID"), rs.getString("address"), rs.getString("neighborhood"), rs.getString("city"), rs.getString("state"),
							rs.getString("zip"), rs.getString("latitude"), rs.getString("longitude")
					});
				}
			}
			
			rs.close();
			stmt.close();
		}catch(SQLException e ){
			e.printStackTrace();
		}
		U.writeCsvFile(writeLines, outputFile);
		U.log("Count of addresses that contain S/N ::"+count);
	}
	
}
