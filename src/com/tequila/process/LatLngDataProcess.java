package com.tequila.process;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.database.connection.Connect;
import com.shatam.maps.GoogleMap;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.TequilaFactory;
import com.shatam.utils.U;

public class LatLngDataProcess extends Connect{

	public static final String TESTING_ID_FILE = "/home/glady/MexicoCache/database/Correctors/Address_Id_Testing_Set.ser";
	
	public static final String FAILED_ID_FILE = "/home/glady/MexicoCache/database/Correctors/Address_Failed_Id_Set.ser";
	
	public static final String FILE_NAME = "/home/chinmay/Downloads/findistance.csv";
			
	public static void main(String[] args) throws InterruptedException {
		LatLngDataProcess process = new LatLngDataProcess();
//		process.processLatLngForBoundary();
		/*
		 * 1] 0-3200
		 * 2] 3200-50000
		 */
	
//		process.findAddressFromDB(3200, 52000);
		process.processLatLngFromCsv();
		
//		process.disconnect();
	}
	/**
	 * 
	 * @param from it is used to give the row id of table. 
	 * @param total it is used to give the total count of records need to fetch from table. 
	 * @throws InterruptedException 
	 */
	private void findAddressFromDB(int from, int total) throws InterruptedException{
		
		Set<Integer> idSets = (Set<Integer>) U.deserialized(TESTING_ID_FILE);
//		List<String> idLists = GeoValidationAtDB.testingIdList();
		
		Statement stmt = null;
		
		List<TequilaFactory> dataset = new ArrayList<>();
		
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dataset limit "+total+" offset "+from);
			while(rs.next()){
				
				if(!idSets.contains(rs.getInt(Field.ID.toString())))continue;
				if(rs.getString(Field.LATITUDE.toString()) == null)continue;
				
				TequilaFactory factory = new TequilaFactory();
				factory.setId(rs.getString(Field.ID.toString()));
				factory.setAddress(rs.getString(Field.ADDRESS.toString()));
				factory.setNeighborhood(rs.getString(Field.NEIGHBORHOOD.toString()));
				factory.setCity(rs.getString(Field.CITY.toString()));
				factory.setState(rs.getString(Field.STATE.toString()));
				factory.setZip(rs.getString(Field.ZIP.toString()));
				factory.setLatitude(rs.getString(Field.LATITUDE.toString()));
				factory.setLongitude(rs.getString(Field.LONGITUDE.toString()));
				dataset.add(factory);
				
			}
			rs.close();
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		//calculate distance here
		calculateAddressLatLngDistance(dataset);
	}
	
	private void processLatLngForBoundary() throws InterruptedException{
		Statement stmt = null;
		List<TequilaFactory> dataset = new ArrayList<>();
		
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dataset where created_date like '2018-07%' and _source_url like 'https://www.tripadvisor.com%' limit 100");
			while(rs.next()){
				
				TequilaFactory factory = new TequilaFactory();
				factory.setId(rs.getString(Field.ID.toString()));
				factory.setAddress(rs.getString(Field.ADDRESS.toString()));
				factory.setNeighborhood(rs.getString(Field.NEIGHBORHOOD.toString()));
				factory.setCity(rs.getString(Field.CITY.toString()));
				factory.setState(rs.getString(Field.STATE.toString()));
				factory.setZip(rs.getString(Field.ZIP.toString()));
				factory.setLatitude(rs.getString(Field.LATITUDE.toString()));
				factory.setLongitude(rs.getString(Field.LONGITUDE.toString()));
				dataset.add(factory);
			}
			rs.close();
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total Records :"+dataset.size());
		
		calculateAddressLatLngDistance(dataset);
	}
	
	private void processLatLngFromCsv() throws InterruptedException{
		List<String[]> readLines = U.readCsvFileWithoutHeader(FILE_NAME);
		List<TequilaFactory> dataset = new ArrayList<>();
		
		for(String[] lines : readLines){
			TequilaFactory factory = new TequilaFactory();
			factory.setId(lines[0]);
			factory.setAddress(lines[8].replace("#", ""));
			factory.setNeighborhood(lines[9].replace("#", ""));
			factory.setCity(lines[10]);
			factory.setState(lines[11]);
			if(lines[12].trim().length() == 4)
				lines[12] = "0"+lines[12];
			factory.setZip(lines[12]);
			factory.setLatitude(lines[22]);
			factory.setLongitude(lines[23]);
			dataset.add(factory);
		}
		U.log("Total Records :"+dataset.size());
		calculateAddressLatLngDistance(dataset);
	}//EOF processLatLngFromCsv()
	
	private void calculateAddressLatLngDistance(List<TequilaFactory> dataset) throws InterruptedException{
		String[] header = {"INDEX","ID","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","LATITUDE","LONGITUDE","ORIGINAL_ADDRESS","DESTINATION_ADDRESS","DISTANCE_IN_MILES","DISTANCE_IN_METERS"};
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(header);
		
		int failedCount = 0, failedKeyCount = 0;
		int index = 0;
		for(TequilaFactory factory : dataset){
			String add[] = { factory.getAddress(),factory.getCity(), factory.getState(),factory.getZip() };
			String latLng[] = { factory.getLatitude(), factory.getLongitude() };
			try {
//				Thread.sleep(1000);
				String[] result = GoogleMap.getGoogleMatrix(add, latLng, true);
				U.log("Id : "+factory.getId()+" Resutl :"+Arrays.toString(result));
				
				if(result.length == 4){
					if(result[0].isEmpty() && result[1].isEmpty()){
						failedCount++;
					}
				}
				if(failedCount > 3){
					result = GoogleMap.getGoogleMatrix(add, latLng, true);
					failedCount = 0;
				}
				
				if(result.length == 4){
					if(result[0].isEmpty() && result[1].isEmpty()){
						failedKeyCount++;
					}
				}
//				if(failedKeyCount > 100)break;
			
				//added for csv
				writeLines.add(new String[]{
					""+index++, factory.getId(), add[0], factory.getNeighborhood(), add[1], add[2], add[3], latLng[0], latLng[1],
					result[0], result[1], result[2], result[3]
				});
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//write file here..
		try {
			write(writeLines);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void write(List<String[]> writeLines) throws Exception{
		String path = FILE_NAME.replace(".csv", "Lat_Lng_Distance_Cal.csv");
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		
		if(writeLines.size() > 1)
//			FileUtil.writeAllText(path, aContents);
			U.writeCsvFile(writeLines, path);
	}
}
