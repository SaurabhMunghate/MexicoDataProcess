package com.tequila.modification;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.Path;
import com.shatam.utils.U;


public class CorrectStateInTable {

	public static final String ALLOW_BLANK = "-";
	
	ArrayList<String> stateList = new ArrayList<String>();
	ArrayList<String> wrongStateList =  new ArrayList<>();
	
	public void loadStateFromDB(){
		String query1 = "select state from sample1 group by state order by state asc";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DBConnection.getConnection("/home/glady/MexicoCache/data/", "tequila.db");
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query1);
			while(rs.next()){
				stateList.add(rs.getString(1).trim());
			}
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException ex){	
			ex.printStackTrace();
		}
	}
	
	CSVWriter writer = null;
	public void checkNonState() throws IOException{
/*		writer = new CSVWriter(new FileWriter(Path.CACHE+"Wrong_State_With_Correct.csv"), '\t');
		String header[] = {"ID","STATE","CITY","ZIP","NEIGHBORHOOD","CORRECTED STATE"};
		writer.writeNext(header);
*/		
		for(String state : stateList){
			boolean found = U.isState(state);
			if(!found){
				U.log("***********"+state);
				wrongStateList.add(state.trim());
			}else{
				String correctState = U.matchState(state);
				if(!state.equals(correctState) && correctState != ALLOW_BLANK){
					U.log(state+"\t\t>>"+correctState);
//					updateState(state, correctState);
				}else if(!state.equals(correctState) && correctState == ALLOW_BLANK){
					U.log("######"+state);
					wrongStateList.add(state.trim());
				}else{
					U.log(">>"+state);
				}
			}
		}
//		writer.close();
	}
	
	public void writeWrongStateList() throws Exception{
		CSVWriter writer1 =  new CSVWriter(new FileWriter(Path.CACHE_PATH+"Wrong_State_List.csv"), '\t');
		String header[] = {"ID","STATE","CITY","ZIP","NEIGHBORHOOD"};
		writer1.writeNext(header);

		U.log("Wrong state list size ::"+wrongStateList.size());
		HashSet<String> removeDuplicate = new HashSet<String>(wrongStateList);
		wrongStateList.clear();
		wrongStateList.addAll(removeDuplicate);
		Collections.sort(wrongStateList);
		
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt=conn.prepareStatement("SELECT ID,STATE,CITY,ZIP,NEIGHBORHOOD FROM sample1 WHERE STATE=?");
		
		for(String state : wrongStateList){
			stmt.setString(1, state.trim());
			ResultSet rs=stmt.executeQuery();
			U.log(state);
			while(rs.next()){  
				writer1.writeNext(new String[]{rs.getString(1).trim(),rs.getString(2).trim(),rs.getString(3).trim(),rs.getString(4).trim(),rs.getString(5).trim()});
			}
			rs.close();
		}
		stmt.close();
		conn.close();
	}
	
	public void updateState(String state, String correctState){
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt=conn.prepareStatement("select id,state,city,zip,NEIGHBORHOOD from sample1 where state=?"); 
			stmt.setString(1, state.trim());
			ResultSet rs=stmt.executeQuery();
			
			while(rs.next()){  
				System.out.println(rs.getInt(1)+" "+rs.getString(2));
				writer.writeNext(new String[]{rs.getString(1).trim(),rs.getString(2).trim(),rs.getString(3).trim(),rs.getString(4).trim(),rs.getString(5).trim(),correctState.trim()});
			}
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		CorrectStateInTable correct = new CorrectStateInTable();
		correct.loadStateFromDB();
		correct.checkNonState();
		correct.writeWrongStateList();
	}

}
