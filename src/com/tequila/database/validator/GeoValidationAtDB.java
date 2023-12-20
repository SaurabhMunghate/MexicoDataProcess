package com.tequila.database.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.database.connection.Connect;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;
import com.tequila.process.DistanceMatrixValues;
import com.tequila.process.LatLngDataProcess;

public class GeoValidationAtDB{

	private static final String DISTANCE_FILE_DIR = "";
	
//	private static String idFilePath = "/home/glady/MexicoCache/database/Correctors/Address_ID_For_Test.csv";
	
	private static String distanceCalcFilePath = "/home/glady/MexicoCache/database/Correctors/11_Sep_2018/Lat_Lng_Distance_Cal_11_Sep_2018_4.csv";
	
	public static void main(String[] args) {
		GeoValidationAtDB validate = new GeoValidationAtDB();
		validate.processDistanceCalculateFiles();
	}
	
	private void processDistanceCalculateFiles(){
		List<Integer> idForUpdate = new ArrayList<>();
		Set<Integer> failedIdSets = new HashSet<>();
		
		Map<Integer, DistanceMatrixValues> distanceMap = new HashMap<>();
		
		distanceMap.putAll(loadDistanceCalculateFiles(distanceCalcFilePath));
		U.log("Distance Map Size :"+distanceMap.size());
		
		for(Entry<Integer,DistanceMatrixValues> entry : distanceMap.entrySet()){
			DistanceMatrixValues dmv = entry.getValue();
//			U.log(dmv.getDistanceInMeter());
			if(dmv.getDistanceInMeter() <= 100){
				idForUpdate.add(entry.getKey()); //id for update geo validation
			}else{
				failedIdSets.add(entry.getKey()); //id for testing id set
			}
		}
		U.log("Id for Update count ::"+idForUpdate.size());
		U.log("Id for Failed count ::"+failedIdSets.size());
		//Update Validate geo
		if(idForUpdate.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateGeo(idForUpdate);
			uvt.disconnect();
		}
		
		Set<Integer> loadFailedIdSet = null;
		File file = new File(LatLngDataProcess.FAILED_ID_FILE);
		if(file.exists()){
			if(file.length() > 100){
				loadFailedIdSet = (Set<Integer>) U.deserialized(LatLngDataProcess.FAILED_ID_FILE);
				if(loadFailedIdSet.size()>1){
					U.log("Size of Original Failed Id Set ::"+loadFailedIdSet.size());
					loadFailedIdSet.addAll(failedIdSets);
					U.log("Size of Original Failed Id Set After new Id added ::"+loadFailedIdSet.size());
					U.writeSerializedFile(loadFailedIdSet, LatLngDataProcess.FAILED_ID_FILE);
				}
			}
		}else{
			U.writeSerializedFile(failedIdSets, LatLngDataProcess.FAILED_ID_FILE);	
		}

	}
	
	private Map<Integer, DistanceMatrixValues> loadDistanceCalculateFiles(String filePath){
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(filePath);
		U.log("Total Records ::"+readLines.size());
		int found = 0;
		Map<Integer, DistanceMatrixValues> distanceMap = new HashMap<>();
		
		for(String [] lines : readLines){
			
			if(lines[11].trim().isEmpty() && lines[12].trim().isEmpty())continue;
			found++;
			
			String val = Util.match(lines[11].trim(), "\\d+(\\.\\d+)?");

			if(val != null){
				DistanceMatrixValues dmv = new DistanceMatrixValues();
				lines[11] = lines[11].replace(val, "").trim();
				if(lines[11].equals("mi") || lines[11].equals("ft")){
					dmv.setDistanceType(lines[11]);
					dmv.setDistanceTypeValue(Float.parseFloat(val.trim()));
					dmv.setDistanceInMeter(Float.parseFloat(lines[12].trim()));
					if(!lines[1].trim().isEmpty()){
						int id = Integer.parseInt(lines[1].trim());
						distanceMap.put(id, dmv);
					}
				}
			}else{
				U.log(val+"\t>>"+lines[11].trim()+"\t>"+lines[12]);
			}
		}//eof for
		U.log("Found Records :: "+found);
		return distanceMap;
	}
	
/*	public static List<String> testingIdList(){
		List<String[]> readLines = U.readCsvFile(idFilePath);
		List<String> idList = new ArrayList<>();
		for(String[] lines : readLines){
			if(lines.length == 1){
				if(!lines[0].isEmpty())
					idList.add(lines[0]);
			}
		}
		return idList;
	}//eof testingIdList()
	*/
	
}//eof GeoValidationAtDB

