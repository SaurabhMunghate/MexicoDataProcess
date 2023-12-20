package com.tequila.geocode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class MexicoStateGeoBoundries implements TextFormat{
	
	private static final String STATE_QUERY = "SELECT DISTINCT STATE FROM dataset"; 
	
	Connection conn = null;
	
	
	public MexicoStateGeoBoundries() {
		conn = DBConnection.getConnection(Path.DB_PATH,"tequila.db");
	}

	
	
	public static void main(String[] args) throws MalformedURLException {
		MexicoStateGeoBoundries msgb = new MexicoStateGeoBoundries();
		String fileName = Path.CACHE_PATH + "Tequila_State_List.txt";
		
//		msgb.readMexicoStateListFromDB(fileName);

		List<String> stateList = msgb.readFile(fileName);
		for(String state : stateList){
			U.log(state);
		}
		
		msgb.disconnect();
		U.log("Done..");
	}
	
	/**
	 * This method is used to read state name from database.
	 */
	public void readMexicoStateListFromDB(String fileName){
		List<String> stateList = new ArrayList<String>();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(STATE_QUERY);
			while(rs.next()){
				stateList.add(rs.getString(1).trim().toString());
			}
			rs.close();
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
				
		//write in the file
		writeFile(stateList,fileName);
		
	}//eof readMexicoStateListFromDB()
	
	private List<String> readFile(String fileName){
		List<String> stateList = new ArrayList<String>();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = br.readLine()) != null){
				if(line.isEmpty())continue;
				
				stateList.add(line.trim());
			}
			line = null;
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}	
		return stateList;
	}
	
	/**
	 * This method is used to write state list in the file.
	 * @param stateList
	 * :- Store the list of states.
	 * @param fileName
	 * :- file name where list of states to be write.
	 */
	private void writeFile(List<String> stateList, String fileName){
		FileWriter writer = null;
		try{
			writer = new FileWriter(fileName);
			for(String state : stateList){
				writer.write(state+"\n");
			}
			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}//eof writeFile()
	
	/**
	 * It's disconnect connection from database server.
	 */
	public void disconnect(){
		try {
			if(conn != null){
				conn.close();
			}
			if(conn.isClosed())
				U.log("Disconnect from database...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//eof disconnect()
}//eof class MexicoStateGeoBoundries
