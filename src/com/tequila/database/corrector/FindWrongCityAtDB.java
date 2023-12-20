package com.tequila.database.corrector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.database.connection.DBConnection;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DB;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.tequila.address.StateReader;

public class FindWrongCityAtDB {

	public static void main(String[] args) {
		FindWrongCityAtDB find = new FindWrongCityAtDB();
		find.startFindingWrongCities();
		find.disconnect();
	}
	
	private Connection conn = null;
	public FindWrongCityAtDB() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}
	
	private void startFindingWrongCities(){
		U.log("Start finding wrong cities here .....");
		String[] header = {"WrongCity","State","CorrectCity","Status","Count_Of_Record"};
		String[] headerForUniqueCities = {"City","State","Count_Of_Record"};
		
		List<String[]> writeLines = new ArrayList<>();
		
		List<String[]> writeUniqueCitiesLines = new ArrayList<>();
		
		List<String[]> stateList = getAllStates();
		
		int totalCorrectCity = 0, totalWrongCity = 0, totalCityForUpdate = 0;
		for(String[] states : stateList){
			
			List<String[]> citiesList = getAllCities(states[0]);
			
			Set<String> citiesSet = StateReader.getAllCities(states[0]);
			
			for(String[] cities : citiesList){
				if(!citiesSet.contains(cities[0])){

					if(citiesSet.contains(TranslateEnglish.convertToEnglish(cities[0]))){
						totalCorrectCity += Integer.parseInt(cities[1].trim());
						totalCityForUpdate += Integer.parseInt(cities[1].trim());
						writeLines.add(new String[]{
							cities[0],states[0],TranslateEnglish.convertToEnglish(cities[0]),"Found",cities[1]
						});
					}else{
						totalWrongCity += Integer.parseInt(cities[1].trim());
						totalCityForUpdate += Integer.parseInt(cities[1].trim());
						writeLines.add(new String[]{
							cities[0],states[0],"","Not Found",cities[1]
						});
					}
				}else{					
					totalCorrectCity += Integer.parseInt(cities[1].trim());
					writeUniqueCitiesLines.add(new String[]{
						cities[0],states[0],cities[1]
					});
				}
			}//eof for
				
		}//eof for
		U.log("Total Correct City Record ::"+totalCorrectCity);
		U.log("Total Incorrect City Record ::"+totalWrongCity);
		U.log("Total City Record For Update ::"+totalCityForUpdate);
		
		U.writeCsvFile(header, writeLines, "/home/glady/MexicoCache/database/Correctors/WRONG_CORRECT_CITY_FORMAT.csv");
		U.writeCsvFile(headerForUniqueCities, writeUniqueCitiesLines, "/home/glady/MexicoCache/database/Correctors/UNIQUE_CORRECT_CITY_FORMAT.csv");
	}

	private List<String[]> getAllCities(String state){
		U.log("State :"+state);
		List<String[]> cities = new ArrayList<>();
		String query = "select city, count(*) as count from dataset where state=\""+state+"\" group by city order by count desc";

//		String query = "select distinct city from dataset where state=\""+state+"\"";
		
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			cities.addAll(DB.resultSetToList(rs));
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total Cities :"+cities.size());
		return cities;
	}
	
	private List<String[]> getAllStates(){
		List<String[]> states = new ArrayList<>();
		String query = "select distinct state from dataset order by state";
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			states.addAll(DB.resultSetToList(rs));			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return states;
	}
	
	private void disconnect(){
		try{
			if(conn != null ||!conn.isClosed()){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
}
