package com.shatam.test;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
public class TestDemo {
///	static String  fileName = getDeletedFileName(Path.DELETED_RECORD_PATH +"DELECTED_RECORD_"+U.getTodayDateWith()+"_1"+".csv");

	public static void main(String[] args) throws IOException {
/*		String status="NOT_FOUND";
		if(status.equalsIgnoreCase("NOT_FOUND")){
			U.log("true");
		}
		U.log(">>"+fileName);
*/		
		String file = "/home/glady/MexicoCache/database/Correctors/test/2.csv";
		validateModifiedCompanyNameWithOriginal(file);
//		generateTestingIdSer("/home/glady/MexicoCache/database/Correctors/Address_ID_For_Test.csv");
/*		Set<Integer> set = new HashSet<>();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(5);
		U.log(set);
		Set<Integer> remSet = new HashSet<>();
		remSet.add(2);
		remSet.add(5);
		set.removeAll(remSet);
		U.log(set);
*/
//U.log(new File("/home/glady/GeoCode/Cache/maps.googleapis.com/httpsmapsgoogleapiscommapsapidistancematrixjsonunitsimperialoriginsIndependencia1501MexicaliBajaCaliforniadestinations324791111708558.txt").length());
//		U.log(U.getTodayDateWith());
		
/*		int num = 1991;
		if(check(num)){
			U.log("Found");
		}else{
			for(int i = num; i<= num+10; i++){
				if(check(i)){
					U.log("Found at for statement when i ="+i);
					break;
				}
			}
		}
		createSerFileForMexicoCity("/home/glady/MexicoCache/source/Mexico_Address_Direcory_MiCodigo.csv");*/
		
		
/*		String add[] = U.getAddressGoogle("Mariano Balleza 73, Mariano Balleza, 37800 Dolores Hidalgo, Gto., Mexico");
		U.log(Arrays.toString(add));
*/	}
	
	public static final void validateModifiedCompanyNameWithOriginal(String file){
		List<String[]> readLines = U.readCsvFileWithoutHeader(file);
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(new String[]{"Incorrect_Company_Name", "Correct_Company_Name", "ID","DB_CorrectName"});
		int count = 0;
		for(String[] lines : readLines){
			String companyName = lines[1];
			String modifiedName = lines[2];
			String tempName = TextFormat.getCompanyNameStandardised(companyName);
			if(!modifiedName.equals(tempName)){
				U.log(Arrays.toString(lines)+"\tCorrectName :"+tempName);
				count++;
				writeLines.add(new String[]{
					companyName, tempName, lines[0], modifiedName	
				});
			}
		}//eof for
		U.log("Total wrong modified name :"+count);
		U.writeCsvFile(writeLines, file.replace(".csv", "_validate.csv"));
	}
	
	
	public static final String getDeletedFileName(String fileName){
//		String fileName = Path.TEQUILA_DB_PATH +"DELECTED_RECORD_"+U.getTodayDateWith()+"_1"+".csv";
		U.log("Rec ::"+fileName);
		File file = new File(fileName);
		try {
			if(file.createNewFile()){
				U.log(file.getName());
				return file.getAbsolutePath();
//				return new File(file.getPath()).toString();
			}else{
				String fileNumber = fileName.substring(fileName.lastIndexOf("_")+1,fileName.length()-4);
				U.log(fileNumber);
				int num = Integer.parseInt(fileNumber)+1;
				fileName = fileName.substring(0,fileName.lastIndexOf("_")+1)+ num + ".csv";
				U.log("Send ::"+fileName);
				getDeletedFileName(fileName);
			}
		} catch (NumberFormatException |IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	public static void generateTestingIdSer(String idFilePath){
		List<String[]> readLines = U.readCsvFile(idFilePath);
		Set<Integer> idList = new HashSet<>();
		for(String[] lines : readLines){
			if(lines.length == 1){
				if(!lines[0].isEmpty())
					idList.add(Integer.parseInt(lines[0]));
			}
		}
		U.writeSerializedFile(idList, Path.CORRECTORS_DIR+"Address_Id_Testing_Set.ser");
		U.log("File is written.");
	}
	
	
	static boolean check(int num){
		if(num == 1998) return true;
		return false;
	}
	
	static void createSerFileForMexicoCity(String fileName){
		
		MultiMap mutliMap = new MultiValueMap();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		for(String[] lines : readLines){
			if(lines[0].trim().equalsIgnoreCase("Mexico City")){
				mutliMap.put(lines[4], lines[3].substring(0, 2));
			}else continue;
		}
		Set<String> keys = mutliMap.keySet();
		for(String city : keys){
			List<String> values = (List<String>) mutliMap.get(city);
			Set<String> zips = new HashSet<>(values);
			U.log(city);
			U.log(zips);
		}
	}
}
