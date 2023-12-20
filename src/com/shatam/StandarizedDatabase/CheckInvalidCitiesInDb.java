package com.shatam.StandarizedDatabase;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import com.database.connection.DBConnection;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import com.opencsv.CSVWriter;

public class CheckInvalidCitiesInDb {
	private static String outPutFolderPath="/home/mypremserver/MexicoStandarised/CityData";
	private static String expectedOutputFolder="/home/mypremserver/MexicoStandarised/CityData/ExpectedOutput";
	private static String dbPath="/home/mypremserver/MexicoDataFiles/Db/";
	private static String dbName="tequila.db";
	private static String cityStateDbPath="/home/mypremserver/MexicoData/chinmay/";
	private static String cityStateDbName="CityState.db";
	private static Connection connMainDb,connCityState;
	public static void main(String[] args) {
		_createFolders();
//		new CheckInvalidCitiesInDb().processDatabase();
//		new CheckInvalidCitiesInDb().SearchforExpectedCity();
//		mergeAllFiles();
		//U.log(new CheckInvalidCitiesInDb().getTentativeUpdateCount("/home/mypremserver/MexicoStandarised/CityData/ExpectedOutput/WrongCities_With_Expected_Jan_8_.csv"));
//		new CheckInvalidCitiesInDb().formatDataToUpdateFile("/home/mypremserver/MexicoStandarised/CityData/CitiesForCorrection.csv");
		close();
	}
	String updateHeaders[]= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","Status",
			"CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE","Year_In_Biz","Emp_Count_Min","Emp_Count_Max","Annual_Sales_Vol_Min","Annual_Sales_Vol_Max"};
	private void formatDataToUpdateFile(String fileName){
		try {
			try {
				StringWriter sw=new StringWriter();
				CSVWriter writer=new CSVWriter(sw);
				List<String[]> cityData=U.readCsvFile(fileName);
				writer.writeNext(updateHeaders);
				Statement stmt=connMainDb.createStatement();
				for (String[] cityDat : cityData) {
					String query="SELECT ID FROM dataset WHERE CITY=\""+cityDat[0]+"\" and STATE=\""+cityDat[1]+"\"";
//					U.log(query);
					ResultSet rs=stmt.executeQuery(query);
					while (rs.next()) {
						String id=rs.getString(1);
						String out[]= {id,"","","","",U.toTitleCase(cityDat[2])};
						writer.writeNext(out);
					}
//					break;
				}
				FileUtil.writeAllText(fileName.replace(".csv","_Update.csv"), sw.toString());
				writer.close();
				sw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int getTentativeUpdateCount(String fileName) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			List<String[]> cityData=U.readCsvFile(fileName);
			Statement stmt=connMainDb.createStatement();
			int totalCount=0;
			for (String[] cityDat : cityData) {
				String query="SELECT COUNT(*) FROM dataset WHERE CITY=\""+cityDat[0]+"\" and STATE=\""+cityDat[1]+"\"";
//				U.log(query);
				ResultSet rs=stmt.executeQuery(query);
				if (rs.next()) {
					int count=rs.getInt(1);
					totalCount+=count;
					String out[]= {cityDat[0],cityDat[1],cityDat[2],""+count};
					writer.writeNext(out);
				}
//				break;
			}
//			U.log(totalCount);
			FileUtil.writeAllText(fileName.replace(".csv","_WitCount.csv"), sw.toString());
			writer.close();
			sw.close();
			return totalCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	private static void mergeAllFiles() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			HashSet<String>states=MXStates.getAllStates();
			for (String state : states) {
				String fileName=outPutFolderPath+"/WrongCities_"+state.toLowerCase()+"_.csv";
				List<String[]>cityFile=U.readCsvFile(fileName);
				writer.writeAll(cityFile);
			}
			FileUtil.writeAllText(outPutFolderPath+"/WrongCities_With_Jan_8_.csv", sw.toString());
			writer.close();
			sw.close();
			sw=new StringWriter();
			writer=new CSVWriter(sw);
			for (String state : states) {
				String fileName=expectedOutputFolder+"/WrongCities_Expected_"+state.toLowerCase()+"_.csv";
				List<String[]>cityFile=U.readCsvFile(fileName);
				writer.writeAll(cityFile);
			}
			FileUtil.writeAllText(expectedOutputFolder+"/WrongCities_With_Expected_Jan_8_.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void SearchforExpectedCity() {
		try {
			
			MultiValueMap refDbRecords=loadReferenceDBRecords();
			HashSet<String>states=MXStates.getAllStates();
			for (String state : states) {
				StringWriter sw=new StringWriter();
				CSVWriter writer=new CSVWriter(sw);
				String fileName=outPutFolderPath+"/WrongCities_"+state.toLowerCase()+"_.csv";
				List<String[]>cityFile=U.readCsvFile(fileName);
				@SuppressWarnings("unchecked")
				Collection<String> cityValue=refDbRecords.getCollection(state.toLowerCase());
				for (String[] cityData : cityFile) {
					String city=cityData[0];
					String expectedCity="";
					for (String filecity : cityValue) {
						int ratio=FuzzySearch.tokenSortRatio(city, filecity);
						int tempratio=0;
						if (ratio==100) {
							expectedCity=filecity;
							break;
						}else if (ratio>80) {
							if (tempratio<ratio) {
								expectedCity=filecity;
								tempratio=ratio;
							}
						}
						
					}
					String out[]= {U.toTitleCase(cityData[0]),U.toTitleCase(cityData[1]),U.toTitleCase(expectedCity)};
					writer.writeNext(out);
				}
				FileUtil.writeAllText(expectedOutputFolder+"/WrongCities_Expected_"+state.toLowerCase()+"_.csv", sw.toString());
				writer.close();
				sw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void close() {
		try {
			connMainDb.close();
			connCityState.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public CheckInvalidCitiesInDb() {
		try {
			connMainDb=DBConnection.getConnection(dbPath, dbName);
			connCityState=DBConnection.getConnection(cityStateDbPath, cityStateDbName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void processDatabase() {
		try {
			MultiValueMap dbRecords=loadDBRecords();
			MultiValueMap refDbRecords=loadReferenceDBRecords();
			Set<String>states=dbRecords.keySet();
			U.log(states.size());
			for (String state : states) {
				int count=0;
				U.log("=====================================");
				U.log("Comparing "+state+" State Data");
				StringWriter sw=new StringWriter();
				CSVWriter writer=new CSVWriter(sw);
				Collection<String> cities=dbRecords.getCollection(state);
				Collection<String> citiesReferenceDb=refDbRecords.getCollection(state);
				for (String city : cities) {
					if (!citiesReferenceDb.contains(city)) {
						count++;
						String out[]= {U.toTitleCase(city),U.toTitleCase(state)};
						writer.writeNext(out);
					}
				}
				U.log("Wrong Cities in "+state+" "+count);
				FileUtil.writeAllText(outPutFolderPath+"/WrongCities_"+state+"_.csv", sw.toString());
				writer.close();
				sw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static MultiValueMap loadDBRecords() {
		MultiValueMap dbRecords=new MultiValueMap();
		try {
			U.log("Loading Data From Database");
//			String loadQuery="SELECT CITY,STATE from dataset where CITY is not null";
			String loadQuery="SELECT CITY,STATE from dataset where CITY is not null group by CITY,STATE";
			Statement stmt=connMainDb.createStatement();
			ResultSet rs=stmt.executeQuery(loadQuery);
			while (rs.next()) {
				String city=rs.getString("CITY").toLowerCase();
				String state=rs.getString("STATE").toLowerCase();
				if (!dbRecords.containsValue(state, city)) {
					dbRecords.put(state, city);
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbRecords; 
	}
	private static MultiValueMap loadReferenceDBRecords() {
		MultiValueMap dbRecords=new MultiValueMap();
		try {
			U.log("Loading Data From City State Database");
			String loadQuery="SELECT CITY,STATE,municipality from mexicoTable";
			Statement stmt=connCityState.createStatement();
			ResultSet rs=stmt.executeQuery(loadQuery);
			while (rs.next()) {
				String city=rs.getString("CITY").toLowerCase();
				String municipality=rs.getString("municipality").toLowerCase();
				String state=rs.getString("STATE").toLowerCase();
				
				if (!dbRecords.containsValue(state, city)) {
					dbRecords.put(state, city);
				}
				if (!dbRecords.containsValue(state, municipality)) {
					dbRecords.put(state, municipality);
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbRecords; 
	}
	private static void _createFolders() {
		File outFolder=new File(outPutFolderPath);
		if (!outFolder.exists()) {
			outFolder.mkdir();
		}
		File outFolder2=new File(expectedOutputFolder);
		if (!outFolder2.exists()) {
			outFolder2.mkdir();
		}
	}
	
}
