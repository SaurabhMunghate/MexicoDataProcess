package com.tequila.database.corrector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.database.connection.DBConnection;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DB;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.tequila.address.StateReader;

public class CorrectCityName {

	public static void main(String[] args) throws IOException {
		CorrectCityName correction = new CorrectCityName();
//		correction.correctCityWithIncorrectUsingTab();
//		correction.correctCityWithGoogleCity();
//		correction.correctCityWithFoundCity();
		correction.correctCityStateZip();
		correction.disconnect();
	}

	private static final String MAIN_DB_NAME = "tequila.db";
	
	private static final String CORRECT_INCORRECT_CITY_FILE = 
			"/home/glady/Downloads/MexicoProject/Pranali/07_June/Correct_City_6.csv"; 
			//"/home/glady/Downloads/MexicoProject/CityNameFromGoogleFinal.csv";
			//"/home/glady/MexicoCache/database/Correctors/differCityNames_correction.tab";
	
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/glady/MexicoCache/database/uniqueKeyTequila.ser";

	private static final String COMPOSITE_KEY_TEQUILA_SER_WTIH_SCORE_ID = "/home/glady/MexicoCache/database/uniqueKeyTequilaWithIdScore.ser";
	
	private Set<Integer> deletedLowScoredDatasetID = new HashSet<>();
	
	private Set<String[]> updateDataset = new HashSet<>();
	
	private Set<String[]> updateDatasetForCityState = new HashSet<>();
	private Set<String[]> updateDatasetForCityZip = new HashSet<>();
	private Set<String[]> updateDatasetForCityStateZip = new HashSet<>();
	
	private Map<String[], String> deletedDataset = new HashMap<>();
	private Map<String[], String> deletedDatasetForCityState = new HashMap<>();
	private Map<String[], String> deletedDatasetForCityStateZip = new HashMap<>();
	private Map<String[], String> deletedDatasetForCityZip = new HashMap<>();
	
	private List<Integer> updatedIdList = new ArrayList<>();
	private List<Integer> deletedIdList = new ArrayList<>();
	
	private Connection conn = null;
	
	public CorrectCityName() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	
	/*
	 * load composite key from serialize file
	 */
/*	CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
	HashSet<String> uniqueKeyHashSet = chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
*/	
	Map<String,Integer[]> uniqueKeyMapWithIdScoreId = getUniqueKeyMapWithIdScore();
	
	private static Map<String,Integer[]> getUniqueKeyMapWithIdScore(){
		return (Map<String, Integer[]>) U.deserialized(COMPOSITE_KEY_TEQUILA_SER_WTIH_SCORE_ID);
	}
	
	
	private void correctCityStateZip(){
		
		U.log("Total Dataset Size ::"+uniqueKeyMapWithIdScoreId.size());
		int row = 1;
		if(isKeySetEmpty()) throw new IllegalArgumentException("Kep map is empty");

		List<String[]> readLines = U.readCsvFileWithoutHeader(CORRECT_INCORRECT_CITY_FILE);
		U.log("Size ::"+readLines.size());
		
		Map<String,Set<String>> citiesMap = new HashMap<>();
		
		for(String[] lines : readLines){
			
			row++;
			
			String wrongCity = lines[0].trim();
			String state = lines[1].trim();
			String zip = lines[2].trim();
			String correctCity = lines[3].trim();
			String correctState = lines[7].trim();
			String correctZip = lines[8].trim();
			
			//contains the cities for the state
			Set<String> cities = null;
			if(!correctState.isEmpty()){
				if(citiesMap.containsKey(correctState.trim())){
					cities = citiesMap.get(correctState.trim());
				}else{
					cities = StateReader.getAllCities(correctState.trim());
					citiesMap.put(correctState.trim(), cities);
				}
			}else{
				if(citiesMap.containsKey(state.trim())){
					cities = citiesMap.get(state.trim());
				}else{
					cities = StateReader.getAllCities(state.trim());
					citiesMap.put(state.trim(), cities);
				}
			}
			
			
			//zip must be length in 5
			if(!zip.trim().isEmpty() && zip.trim().length() == 4){
				zip = "0"+zip;
			}
			if(zip.isEmpty()) zip = DB.EMPTY;
			
			if(!correctZip.trim().isEmpty() && correctZip.trim().length() == 4){
				correctZip = "0"+correctZip;
			}
			
			//to check state is in mexico country, and try to correct the format of the state
			if(!correctState.trim().isEmpty()){
				if(U.isState(state)){
					state = U.matchState(state);
				}
				if(state.equals("-"))throw new IllegalStateException("State is not the part of Mexico. Given state value is "+lines[7]+"\tRow at File is "+row);
			}
			
			//to check correct city is part of Mexico state.
			//to check is corrected city is not standard format, if not then convert into correct format
			if(cities.contains(correctCity)){
//				U.log(correctCity +" is part of State "+state);
			}else if(cities.contains(TranslateEnglish.convertToEnglish(U.toTitleCase(correctCity).replace(" - ", "-")))){
				correctCity = TranslateEnglish.convertToEnglish(U.toTitleCase(correctCity).replace(" - ", "-"));
			}else{
				throw new IllegalArgumentException("Correct city is not found at state cities list. Correct city is "+correctCity+" And State is "+state+"\tRow at File is "+row);
			}
						
			//evaluate only if correctState and correctZip are empty
			if(correctState.isEmpty() && correctZip.isEmpty()){
				compare(correctCity, wrongCity, state.trim(), zip, DB.EMPTY, DB.EMPTY);
			}
			//evaluate only if correctState is not empty and correctZip is empty
			else if(!correctState.isEmpty() && correctZip.isEmpty()){
				compare(correctCity, wrongCity, state.trim(), zip, correctState, DB.EMPTY);
			}
			//evaluate only if correctState is empty and correctZip is not empty
			else if(correctState.isEmpty() && !correctZip.isEmpty()){
				compare(correctCity, wrongCity, state.trim(), zip, DB.EMPTY, correctZip);
			}
			//evaluate only if correctState and correctZip are not empty 
			else if(!correctState.isEmpty() && !correctZip.isEmpty()){
				compare(correctCity, wrongCity, state.trim(), zip, correctState, correctZip);
			}		
		}
		//Modification at db
		modifiedAllAtDb();
		
	}//eof correctCityStateZip()

	private void modifiedAllAtDb(){
		
		/**
		 * delete those records have low scored in database as compare updated records have greater score.
		 */
		
		int totalDeletedCount = 0;
		if(deletedLowScoredDatasetID.size() > 0){
			U.log("*** Count of deleted record has low score as compare updated records ::"+deletedLowScoredDatasetID.size());
			List<Integer> lowScoreDatasetId = new ArrayList<>(deletedLowScoredDatasetID);
			Collections.sort(lowScoreDatasetId);
			DeleteReportAtTable deleteReportAtTable = new DeleteReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			deleteReportAtTable.deleteRecordAtDB(lowScoreDatasetId);
			deleteReportAtTable.disconnect();
			
			totalDeletedCount = deletedLowScoredDatasetID.size();
			lowScoreDatasetId.clear();
			deletedLowScoredDatasetID.clear();
			
			
		}
		
		UpdateDeleteAtTequila.connect();
		
		List<Integer> updatedAllIdList = new ArrayList<>();
		//Start updating city here

		U.log("*** Count of records for update city ::"+updateDataset.size());
		U.log("*** Count of records for update city & state ::"+updateDatasetForCityState.size());
		U.log("*** Count of records for update city & zip ::"+updateDatasetForCityZip.size());
		U.log("*** Count of records for update city, state & zip ::"+updateDatasetForCityStateZip.size());
		
		if(updateDataset.size()>0)
			updatedAllIdList.addAll(UpdateDeleteAtTequila.updateIncorrectCity(updateDataset));
		
		if(updateDatasetForCityState.size()>0)
			updatedAllIdList.addAll(UpdateDeleteAtTequila.updateIncorrectCityState(updateDatasetForCityState));
		
		if(updateDatasetForCityZip.size()>0)
			updatedAllIdList.addAll(UpdateDeleteAtTequila.updateIncorrectCityZip(updateDatasetForCityZip));
		
		if(updateDatasetForCityStateZip.size()>0)
			updatedAllIdList.addAll(UpdateDeleteAtTequila.updateIncorrectCityStateZip(updateDatasetForCityStateZip));
		
		
		// Start deleting duplicate record here
		 
		U.log("*** Count of records for deleted record of city ::"+deletedDataset.size());
		U.log("*** Count of records for deleted record of city & state ::"+deletedDatasetForCityState.size());
		U.log("*** Count of records for deleted record of city & zip ::"+deletedDatasetForCityZip.size());
		U.log("*** Count of records for deleted record of city & state & zip ::"+deletedDatasetForCityStateZip.size());
		
		totalDeletedCount += deletedDataset.size();
		totalDeletedCount += deletedDatasetForCityState.size();
		totalDeletedCount += deletedDatasetForCityZip.size();
		totalDeletedCount += deletedDatasetForCityStateZip.size();
				
		if(deletedDataset.size()>0)
			UpdateDeleteAtTequila.deletedIncorrectCityRecord(deletedDataset);
		
		if(deletedDatasetForCityState.size()>0)
			UpdateDeleteAtTequila.deletedIncorrectCityStateRecord(deletedDatasetForCityState);
		
		if(deletedDatasetForCityZip.size()>0)
			UpdateDeleteAtTequila.deletedIncorrectCityZipRecord(deletedDatasetForCityZip);
		
		if(deletedDatasetForCityStateZip.size()>0)
			UpdateDeleteAtTequila.deletedIncorrectCityStateZipRecord(deletedDatasetForCityStateZip);
		
		U.log("*** Size of modified records at database ::"+updatedIdList.size());		
		if(updatedAllIdList.size() > 0){
			Set<Integer> set = new HashSet<>(updatedAllIdList);
			updatedAllIdList.clear();
			updatedAllIdList.addAll(set);
			Collections.sort(updatedAllIdList);
			set.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(updatedAllIdList);
		}

		UpdateDeleteAtTequila.disconnect();
		
		U.log("*** Total deleted records :"+totalDeletedCount);
	}
	
	private void correctCityWithFoundCity(){
		
		if(isKeySetEmpty()) throw new IllegalArgumentException("Kep map is empty");
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(CORRECT_INCORRECT_CITY_FILE);
		U.log("Size ::"+readLines.size());
		
		Map<String,Set<String>> citiesMap = new HashMap<>();
		
		for(String[] lines : readLines){
			if(lines[3].trim().equals("Found")){
				if(lines[2].trim().isEmpty())continue;
				
				String incorrectedCity = lines[0].trim();
				String state = lines[1].trim();
				String correctCity = lines[2].trim();
				
				Set<String> cities = null;
				if(citiesMap.containsKey(state.trim())){
					cities = citiesMap.get(state.trim());
				}else{
					cities = StateReader.getAllCities(state.trim());
					citiesMap.put(state.trim(), cities);
				}
				
				if(cities.contains(correctCity)){
					compare(correctCity, incorrectedCity, state.trim(), DB.EMPTY, DB.EMPTY, DB.EMPTY);
				}else if(cities.contains(TranslateEnglish.convertToEnglish(correctCity))){
					compare(TranslateEnglish.convertToEnglish(correctCity), incorrectedCity, state.trim(),DB.EMPTY, DB.EMPTY, DB.EMPTY);
				}

			}//eof if
		}//eof for
		/*
		 * Modification at db
		 */
		modifiedAtDb();
	}
	
	private void correctCityWithGoogleCity(){
		
		if(isKeySetEmpty()) throw new IllegalArgumentException("Kep map is empty");
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(CORRECT_INCORRECT_CITY_FILE);
		U.log("Size ::"+readLines.size());
		
		String header[] = {"City1","City2","GoogleResult","Status"};
		List<String[]> writeLines = new ArrayList<>();
		
		Map<String,String> dataset = new HashMap<>();
		
		Map<String,Set<String>> citiesMap = new HashMap<>();
		
		for(String[] lines : readLines){
			
			if(lines[3].trim().isEmpty()){
				writeLines.add(lines);
				continue;
			}
			
			boolean flag1 = false, flag2 = false, flag3 = false;
			
			String correctCity = null;
			
			String city1 = lines[0].split(",")[0];
			String state1 = lines[0].split(",")[1];
			
			String city2 = lines[1].split(",")[0];
			String state2 = lines[1].split(",")[1];
			
			String googleCity = lines[2].trim();
			
			String status = lines[3].trim();
			
			Set<String> cities = null;
			if(citiesMap.containsKey(state1.trim())){
				cities = citiesMap.get(state1.trim());
			}else{
				citiesMap.put(state1.trim(), StateReader.getAllCities(state1.trim()));
				cities = StateReader.getAllCities(state1);
			}
			
			if(status.equals("Verified")){
				
				if(cities == null)throw new IllegalArgumentException("Cities map is null.");
				
				if(state1.equals(state2)){
					
//					Set<String> cities = StateReader.getAllCities(state1);
					if(cities.contains(city1)){
						correctCity = city1;
						flag1 = true;
					}else if(cities.contains(city2)){
						correctCity = city2;
						flag2 = true;
					}else if(cities.contains(googleCity)){
						correctCity = googleCity;
						flag3 = true;
					}else{
						googleCity = TranslateEnglish.convertToEnglish(googleCity);
						googleCity = U.toTitleCase(googleCity);
						if(cities.contains(googleCity)){
							correctCity = googleCity;
							flag3 = true;
						}else{
							writeLines.add(lines);
							U.log("wrong city-1: "+city1+"\tcity-2: "+city2+"\tGoogleCity: "+googleCity);
						}
					}
				}
			}
			
			if(status.equals("Verified") && correctCity != null){
				if(flag3 && !flag1 && !flag2){
					dataset.put(lines[0], correctCity.trim());
					dataset.put(lines[1], correctCity.trim());
				}else if(flag1 && !flag2 && !flag3){
					dataset.put(lines[1], correctCity.trim());
				}else if(flag2 && !flag1 && !flag3){
					dataset.put(lines[0], correctCity.trim());
				}
			}
			
		}//eof for
		U.log("Size of writeLines for wrong cities ::"+writeLines.size());
		U.log("Size of dataset of correct cities ::"+dataset.size());
		for(Map.Entry<String, String> entry : dataset.entrySet()){
			U.log(entry.getKey()+"\t"+entry.getValue());
			String vals[] = entry.getKey().split(",");
			compare(entry.getValue().trim(), vals[0], vals[1].trim(), DB.EMPTY, DB.EMPTY, DB.EMPTY);
		}
		
		U.writeCsvFile(header, writeLines, CORRECT_INCORRECT_CITY_FILE.replace(".csv", "_2.csv"));
		/*
		 * Modification at db
		 */
		modifiedAtDb();
		
	}//eof correctCityWithGoogleCity()
	
	private void modifiedAtDb(){
		/*
		 * Start updating city here
		 */
		U.log("Count of records for update city ::"+updateDataset.size());
		updateIncorrectCity(updateDataset);
		
		/*
		 * Start deleting duplicate record here
		 */
		U.log("Count of records for deleted record ::"+deletedDataset.size());
		deletedIncorrectCityRecord(deletedDataset);
		
		U.log("Size of modified records at database ::"+updatedIdList.size());		
		if(updatedIdList.size() > 0){
			Set<Integer> set = new HashSet<>(updatedIdList);
			updatedIdList.clear();
			updatedIdList.addAll(set);
			Collections.sort(updatedIdList);
			set.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(updatedIdList);
		}
	}
	
	private void correctCityWithIncorrectUsingTab() throws IOException{
	
		List<String> readLines = Files.readAllLines(Paths.get(CORRECT_INCORRECT_CITY_FILE));
		U.log("Count of cities for correction ::"+readLines.size());
		for(String line : readLines){
//			U.log(line);
			
			String vals[] = line.split("\t");
			readCityFromDB(vals);
		}
		
		/*
		 * Start updating city here
		 */
		U.log("Count of records for update city ::"+updateDataset.size());
		updateIncorrectCity(updateDataset);
		
		/*
		 * Start deleting duplicate record here
		 */
		U.log("Count of records for deleted record ::"+deletedDataset.size());
		deletedIncorrectCityRecord(deletedDataset);
		
		U.log("Size of modified records at database ::"+updatedIdList.size());		
		if(updatedIdList.size() > 0){
			Set<Integer> set = new HashSet<>(updatedIdList);
			updatedIdList.clear();
			updatedIdList.addAll(set);
			Collections.sort(updatedIdList);
			set.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(updatedIdList);
		}
	}

	private void readCityFromDB(String [] vals){
		String correctedCity = vals[0].split(",")[0].trim();
		String correctedState = vals[0].split(",")[1].trim();
		
		String incorrectCity = vals[1].split(",")[0].trim();
		String incorrectState =  vals[1].split(",")[1].trim();
		
		if(correctedState.equals(incorrectState)){
//			U.log("Correct ::"+correctedCity+"\t\tIncorrect ::"+incorrectCity+"\t\tState ::"+correctedState);
			compare(correctedCity, incorrectCity, correctedState, DB.EMPTY, DB.EMPTY, DB.EMPTY);
		}
		
		
	}
	
	public void compare(String correctCity, String incorrectCity, String state, String zip, String correctState, String correctZip){

		String query = "Select * from dataset where city=\""+incorrectCity+"\" and state=\""+state+"\"";
		if(!DB.isValueEmpty(zip)){
			query = "Select * from dataset where city=\""+incorrectCity+"\" and state=\""+state+"\" and zip =\""+zip+"\"";
		}
			
		//read data from database
		List<String[]> dataset = null;
		try(Statement stmt = conn.createStatement();
			ResultSet rs =  stmt.executeQuery(query);){
			dataset = DB.resultSetToList(rs);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(String[] data : dataset){
			if(isDuplicateCompositeKey(data, correctCity, correctState)){
				if(DB.isValueEmpty(correctState) && DB.isValueEmpty(correctZip)){
					deletedDataset.put(data, correctCity);
				}
				else if(!DB.isValueEmpty(correctState) && DB.isValueEmpty(correctZip)){
					deletedDatasetForCityState.put(data, correctCity+ DB.SEPARATOR_CITY_STATE +correctState);
				}
				else if(DB.isValueEmpty(correctState) && !DB.isValueEmpty(correctZip)){
					deletedDatasetForCityZip.put(data, correctCity+ DB.SEPARATOR_CITY_STATE +correctZip);
				}
				else if(!DB.isValueEmpty(correctState) && !DB.isValueEmpty(correctZip)){
					deletedDatasetForCityStateZip.put(data, correctCity+ DB.SEPARATOR_CITY_STATE +correctState+ DB.SEPARATOR_CITY_STATE+ correctZip);
				}
			}else{
				if(DB.isValueEmpty(correctState) && DB.isValueEmpty(correctZip)){
					updateDataset.add(new String[]{
						data[0].trim(), correctCity, incorrectCity
					});
				}
				else if(!DB.isValueEmpty(correctState) && DB.isValueEmpty(correctZip)){
					updateDatasetForCityState.add(new String[]{
							data[0].trim(), correctCity, incorrectCity, correctState
					});
				}
				else if(DB.isValueEmpty(correctState) && !DB.isValueEmpty(correctZip)){
					updateDatasetForCityZip.add(new String[]{
							data[0].trim(), correctCity, incorrectCity, correctZip
					});
				}
				else if(!DB.isValueEmpty(correctState) && !DB.isValueEmpty(correctZip)){
					updateDatasetForCityStateZip.add(new String[]{
							data[0].trim(), correctCity, incorrectCity, correctState, correctZip
					});
				}
			}//eof else
		}//eof for
		dataset = null;
	}
	
	void deletedIncorrectCityRecord(Map<String[],String> deleteCityDataset){
		/*
		 * Start inserted records at deletedDataset Table 
		 */
		insertAtDeletedTable(deleteCityDataset);
		
		U.log("Start deleting duplicate composite key record at database here ....");
		
		String updateQuery = "delete from dataset where id=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int deleteCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : deletedIdList){
				pstmt.setInt(1, id);
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					deleteCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			deleteCount = pstmt.executeBatch();
			System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total count of deleted duplicated composite key record at database ::"+x);
	}
	
	void updateIncorrectCity(Set<String[]> updateCityDataset) {
		U.log("Start updating correct city here...");
		String updateQuery="update dataset set CITY=?, UPDATED_DATE=? where CITY=? and ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for (String[] data : updateCityDataset) {
				
				pstmt.setString(1, data[1]); //Correct City
				pstmt.setString(2, U.getTodayDate());  // Incorrect City
				pstmt.setString(3, data[2]);  // Incorrect City
				pstmt.setString(4, data[0]);  //ID
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				updatedIdList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");
			pstmt.close();
			
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertAtDeletedTable(Map<String[],String> deleteCityDataset){

		U.log("All Records Size ::"+deleteCityDataset.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(Map.Entry<String[], String> entry : deleteCityDataset.entrySet()){
				String rows[] = entry.getKey();
				for(int i = 0; i < rows.length; i++){
					if(i == 10){
						pstmt.setString(i+1, entry.getValue());  //Corrected City
					}else if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				deletedIdList.add(Integer.parseInt(rows[0])); //add Id at list here..
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
	}
	
	private boolean isDuplicateCompositeKey(String[] data, String correctCity, String correctState){
		String sicSub = data[4].trim();
		if(sicSub.length() == 3)sicSub="0"+sicSub;
		
		String contactPerson = data[17];
		if(contactPerson == null) contactPerson = "";
		
		String uniqueKey =	U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(data[8].trim()) + U.toTitleCase(correctCity.trim()) 
		+ U.toTitleCase(data[11].trim())+ U.toTitleCase(contactPerson.trim());

		if(!DB.isValueEmpty(correctState)){
			uniqueKey =	U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(data[8].trim()) + U.toTitleCase(correctCity.trim()) 
			+ U.toTitleCase(correctState.trim())+ U.toTitleCase(contactPerson.trim());
		}
		
		/*if (uniqueKeyHashSet.contains(uniqueKey.toLowerCase())){
			return true;
		}else{
			uniqueKeyHashSet.add(uniqueKey.toLowerCase());
		}*/
		
		if(uniqueKeyMapWithIdScoreId.containsKey(uniqueKey.toLowerCase())){
			Integer[] idAndScore = uniqueKeyMapWithIdScoreId.get(uniqueKey.toLowerCase());
			if(idAndScore[1] < Integer.parseInt(data[29].trim())){
				deletedLowScoredDatasetID.add(idAndScore[0]);
				U.log("*** ID :"+idAndScore[0]+"\tLow Score :"+idAndScore[1]+"\tID :"+data[0]+"\tHigh Score :"+data[29]);
				return false;
			}else{
				U.log("*** ID :"+idAndScore[0]+"\tHigh Score :"+idAndScore[1]+"\tID :"+data[0]+"\tLow Score :"+data[29]+"\t\tDeleted this since low/equal scored");
				return true;
			}
		}
		return false;
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
	
	private boolean isKeySetEmpty(){
//		if(uniqueKeyHashSet.size() == 0)return true;
		if(uniqueKeyMapWithIdScoreId.size() == 0)return true;
		else return false;
	}
}
