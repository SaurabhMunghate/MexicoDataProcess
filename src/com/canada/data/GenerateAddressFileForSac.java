package com.canada.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class GenerateAddressFileForSac extends Connect{

	public GenerateAddressFileForSac(String path, String dbName) {
		super(path, dbName);
	}

	public static void main(String[] args) throws SQLException, IOException {
		GenerateAddressFileForSac generate = new GenerateAddressFileForSac("/home/glady/CanadaSAC/", "RyeLatLong.db");
		
		File file =  new File("/home/glady/CanadaSAC/sac_address_files");
		if(!file.exists())file.mkdirs();
		if(file.isDirectory()){
			generate.generateAddressFile(file.getPath()+File.separator);
		}
		generate.disconnect();
	}
	

	
	private void generateAddressFile(String filePath) throws SQLException, IOException{
		List<String> states = readDatabase();
		U.log("States count :"+states.size());
		Set<String> uniqueAddressSet = new HashSet<>();
		try {
			FileWriter fw = null;
			PreparedStatement pstmt = null;
			for(String state : states){
				String abbr = CanadaStates.getStateFromAbbr(state.trim());
				if(abbr == null){
					U.log("Can't find its abbr for state :"+state);
					continue;
				}
				U.log("State :"+state);

				fw = new FileWriter(filePath + state+"_"+abbr+".txt");
				
				pstmt = conn.prepareStatement("select ID, ADDRESS, CITY, STATE, ZIP from latlongdata where STATE=?");
				pstmt.setString(1, state.trim());
				ResultSet rs = pstmt.executeQuery();
				int i=0;
				StringBuffer sb = new StringBuffer();
				while (rs.next()) {
					if(rs.getString("ADDRESS").toLowerCase().contains("P.o. Box".toLowerCase()) || 
							rs.getString("ADDRESS").toLowerCase().contains("Po Box".toLowerCase()))continue;
					
					String zip = rs.getString("ZIP");
					if(zip.length() > 3){
						zip = zip.trim().substring(0, 3);
					}
					
					if(!uniqueAddressSet.add((rs.getString("ADDRESS")+rs.getString("CITY")+abbr+zip).toLowerCase()))continue;
					
					sb.append("[\"").append(""+i++).append("\",\"")
					.append(rs.getString("ADDRESS")).append("\",\"\",\"")
					.append(rs.getString("CITY")).append("\",\"").append(abbr+"\",\"");
					

					sb.append(zip +"\"],\n");
				}//eof while
				
				rs.close();
				pstmt.close();
				
				U.log("Count :"+i);
				String address = sb.toString();
				if(!address.isEmpty()) address = address.trim().replaceAll(",$", "");
				
				fw.write(address);
				U.log("File is Generated with naming :"+state+"_"+abbr+".txt\n");
				fw.flush();
				fw.close();

			}//eof for

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private List<String> readDatabase() throws SQLException{
		List<String> states = new ArrayList<>();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select distinct state from latlongdata;");
		while(rs.next()){
			states.add(rs.getObject(1).toString());
		}
		rs.close();
		stmt.close();
		return states;
	}
}
