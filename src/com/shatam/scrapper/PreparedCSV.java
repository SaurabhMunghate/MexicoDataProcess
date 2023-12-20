package com.shatam.scrapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class PreparedCSV {

	private String inputFile = "/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Farmacias Moderna_Correct_Add.csv";

	private static String inputAddressFile = 
			"/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Correct_A__Herbalife_Correct.csv";
	
	private static String originalFile = 
			"/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Correct_A_ADD_A_C_P.csv";
	
	List<String[]>  readLines = null;
	
	public static void main(String[] args) {
//		innerJoinCompanyNameUniqueRecord();
//		compareOriginalAddressWithFound(true);
		addAddressColoniaAndOthersFromCompanyFile(true, true, true, true);
		
//		addaddressFromGoogleDataFile(true);
//		generateCompanyRecordRemainingFile(false);
//		addAddressCorrected();
		
//		correctAddressFormatFromAddress(true);
		
//		findDuplicateColoniaFromAddress(false);
		// TODO Auto-generated method stub
/*		PreparedCSV prepare = new PreparedCSV();
		prepare.loadReadFile();
*/
/*		prepare.correctCompanyName();
		prepare.writeFile();
*/	
		
/*		prepare.validateFile();
		prepare.writeFile();
*/		
//		prepare.separatedNonEmptyCompanyName();
		
//		prepare.separatedNonEmptyNaisc();		
//		prepare.changedCoulmnCountent();
		
	}
	/**
	 * This method is used to search a particular companies records at .csv file and find those records which is either near or surrounding to each other.<br>
	 * This will helps to remove unnecessary data. Method is using lat-lng of each records for given company and search each record if they near each other.<br>
	 * This method is only work when lat-lng is present. 
	 * @param companyName
	 * Input company name from csv file.
	 */
	static void findNearAddressesEachOther(String companyName){
		
	}
	
	static void generateCompanyRecordRemainingFile(boolean status){
		List<String[]> inputLines = U.readCsvFile(originalFile);
		
		List<String[]> companyRecordLines = U.readCsvFile(inputAddressFile);
		Map<String,String[]> companyRecordMap = new HashMap<>();
		
		int i = 0;
		String header [] = null;
		for(String[] lines : companyRecordLines){
			if(i++ == 0){
				header = lines;
				continue;
			}
			companyRecordMap.put(lines[2].trim(), lines);
		}
		int found = 0;
		
		i = 0;
		for(String lines[] : inputLines){
			if(i++ == 0)continue;
			
			List<ExtractedResult> result = FuzzySearch.extractSorted(lines[8], companyRecordMap.keySet(), 88);
			if(result.size() == 0)continue;
			if(result.size() == 1){
				found++;
				ExtractedResult er = result.get(0);
				companyRecordMap.remove(er.getString());
				U.log(lines[13]);
				U.log(companyRecordMap.get(er.getString())[7]);
				U.log("==>"+lines[8]+"\t\t"+result);					

				if(!lines[13].isEmpty() && U.formatNumbersAsCode(lines[13]).equals(U.formatNumbersAsCode(""))){
				}

			}
/*			else{
				ExtractedResult er = result.get(0);
				U.log("*********==>"+lines[8]+"\t\t"+er.toString());
				found++;
				companyRecordMap.remove(er.getString());
			}
*/		}
		U.log("Found ::"+found);
		if(status){
			U.writeCsvFile(header, companyRecordMap.values(), inputAddressFile.replace(".csv", "_Remain_Record.csv"));
		}
	}
	
	static void addaddressFromGoogleDataFile(boolean status){
		List<String[]> readLines = U.readCsvFile(originalFile);
		List<String[]> addressLines = U.readCsvFileWithoutHeader(inputAddressFile);
		
		Map<String,String[]> addressMap = new HashMap<>();
		
		for(String[] lines : addressLines)addressMap.put(lines[13].trim(), lines);
		
		U.log(addressMap.size());
		int i = 0;
		for(String[] lines : readLines){
			if(i++ == 0)continue;
			
			if(addressMap.containsKey(lines[24].trim())){
				if(lines[9].trim().isEmpty()){
					lines[9] = addressMap.get(lines[24].trim())[4]; //colonia
					U.log(lines[9] +"\t"+i);
				}
			}
		}
		if(status){
			U.writeCsvFile(readLines, originalFile.replace(".csv", "_ADD_A_C_P.csv"));
		}
	}
	
	static void addAddressColoniaAndOthersFromCompanyFile(boolean addAddress, boolean addColonia, boolean addOthers, boolean status){
		List<String[]> readLines = U.readCsvFile(originalFile);
		List<String[]> addressLines = U.readCsvFileWithoutHeader(inputAddressFile);
		
		Map<String,String[]> addressMap = new HashMap<>();
		
		for(String[] lines : addressLines)addressMap.put(lines[24], lines);
		
		int i = 0;
		for(String[] lines : readLines){
			if(i++ == 0)continue;
			
			if(addressMap.containsKey(lines[24])){
				String[] data = addressMap.get(lines[24]);
				
				if(addAddress){
					lines[8] = data[8]; //street
				}
				if(addColonia){
					lines[9] = data[9]; //neighbourhood
				}
				if(addOthers){
					lines[4]  = data[4]; //sic sub
//					lines[7]  = data[7]; //company name
//					lines[10] = data[10]; //city
//					lines[11] = data[11]; //state
					lines[12] = data[12]; //zip
					if(!lines[13].isEmpty()){
						if(!data[13].isEmpty()){
							lines[13] = TextFormat.getUniqueNumber(lines[13]+";"+data[13]);
						}
					}else{
						lines[13] = data[13]; //phone
					}
					lines[14] = data[14]; //fax
					lines[15] = data[15]; //url
					lines[16] = data[16]; //email
					lines[17] = data[17]; //contact person
					lines[18] = data[18]; //title
					lines[19] = data[19]; //annual sales
					lines[20] = data[20]; //emp count
					lines[21] = data[21]; //YearInBiz
					lines[22] = data[22]; //latitude
					lines[23] = data[23]; //longitude
				}
				
			}//eof if
		}//eof for
		
		if(status){
			U.writeCsvFile(readLines, originalFile.replace(".csv", "_ADD_A_C_P.csv"));
		}
	}
	

	
	static void compareOriginalAddressWithFound(boolean status){
		List<String[]> inputLines = U.readCsvFile(originalFile);
		
		List<String[]> addressLines = U.readCsvFile(inputAddressFile);
		
		Map<String,String[]> addressMap = new HashMap<>();
		String[] header = null;
		int i = 0;		
		for(String[] lines : addressLines){
			if(i++ == 0){
				header = lines;
				continue;
			}
			addressMap.put(lines[2], lines);
		}

		i = 0;
		int foundColonia = 0, foundPhone = 0, correctZip=0;
		for(String[] lines : inputLines){
			
			List<ExtractedResult> result = FuzzySearch.extractSorted(lines[8], addressMap.keySet(),60);
//			ExtractedResult result = FuzzySearch.extractOne(lines[8], addressMap.keySet()); //, 90);
			
//			List<ExtractedResult> result = FuzzySearch.extractTop(lines[8], addressMap.keySet(),2,90);
/*			for(ExtractedResult er : result){
				er.getString();
			}
*/			if(result.size() == 0)continue;
			if(result.size() >= 1){
				ExtractedResult er = result.get(0);
				if(er.getScore() < 87)continue;
				U.log("<<==>"+lines[8]+"\t\t"+result);
				
				if(er.getScore() != 100){
					if(lines[8].trim().length()< er.getString().trim().length())
						lines[8] = er.getString();  //street
				}
				if(lines[9].trim().isEmpty()){
					lines[9] = addressMap.get(er.getString())[3];  //colonia
				}
				if(!lines[9].trim().isEmpty())foundColonia++;
				//phone
/*				String tempPhone = addressMap.get(er.getString())[7]; //phone
				if(lines[13].trim().isEmpty()){
					if(!tempPhone.trim().isEmpty()){
						lines[13] = tempPhone;
						U.log("***"+lines[13]);
						foundPhone++;
					}
				}else{
					if(!tempPhone.trim().isEmpty()){
						if(FuzzySearch.tokenSetRatio(lines[13].trim().replaceAll("-|\\s", ""), tempPhone.trim().replaceAll("-|\\s", "")) < 98){
							lines[13] += ";"+tempPhone;
							U.log("***"+lines[13]);
							foundPhone++;
						}else{
							U.log(lines[13]+"\t\tMatch :"+tempPhone);
						}
					}
				}
*/				//zip
				String tempZip = addressMap.get(er.getString())[6]; //zip
				if(lines[12].trim().isEmpty()){
					if(!tempZip.trim().isEmpty()){
						lines[12] = tempZip;
						correctZip++;
					}					
				}else{
					if(!tempZip.trim().isEmpty()){
						if(!lines[12].trim().equals(tempZip.trim())){
							lines[12] = tempZip;
							correctZip++;
							U.log(">>>"+lines[12]+"\t\tzip ::"+tempZip);
						}
					}
				}
				//email
				lines[16] = addressMap.get(er.getString())[11]; //EMAIL
				
				//delete found data
				addressMap.remove(er.getString());
			}else{
//				U.log("*** "+Arrays.toString(lines));
//				ExtractedResult er = result.get(0);
//				U.log("==>"+lines[8]+"\t\t"+er.toString());
			}
		}//eof for outer
		
		U.log("Found Colonia ::"+foundColonia+"\nFound Phone ::"+foundPhone);
		U.log("Correct zip ::"+correctZip);
		
		if(status){
			U.writeCsvFile(inputLines, originalFile.replace(".csv", "_Add_Col.csv"));
			U.writeCsvFile(header, addressMap.values(), inputAddressFile.replace(".csv", "_Remain_Record.csv"));
		}
	}
	
	/**
	 * Add corrected addresss content from one csv file to original csv file
	 */
	static void addAddressCorrected(){
		List<String[]> addressLines = U.readCsvFileWithoutHeader(inputAddressFile);
		Map<String,String[]> addressMap = new HashMap<>(); 
				
		for(String [] vals : addressLines){
			addressMap.put((vals[0].trim()+vals[7].trim()).toLowerCase(), vals);
		}
	
		List<String[]> readLines = U.readCsvFile(originalFile);
		
		String [] lines = null;
		int x = 0;
		int count = 0;
		Iterator<String[]> it = readLines.iterator();
		
		while(it.hasNext()){
			lines = it.next();			
			if(x++ == 0)continue;
			String key = (lines[0].trim()+lines[24].trim()).toLowerCase();
			
			if(addressMap.containsKey(key)){
				String[] vals = addressMap.get(key);
				
				if(lines[8].length() < vals[2].length())
					lines[8] = vals[2]; //address
				else U.log("Found add : "+lines[8]+"\t\tFrom ADD File : "+vals[2]+"\t\tIndex :"+lines[0]+"\tRow :"+x);
				
				if(lines[9].length()<1 || lines[9].isEmpty()){
					if(!vals[3].isEmpty() || vals[3].length() > 2)
						lines[9] = vals[3]; //colonia
				}
/*				lines[10] = vals[4]; //city
				lines[11] = vals[5]; //state
*/
				if(lines[12].isEmpty() && !vals[6].isEmpty())
					lines[12] = vals[6]; //zip
				
				count++;
			}else{
				U.log("Index :"+lines[0]+"\t not found. Row is :"+x);
			}
		}
		U.log("Count of records that found in address file :: "+count);
		U.writeCsvFile(readLines, originalFile.replace(".csv", new Date()+".csv"));
	}
	/**
	 * This method is used to correct address column by removing zip and city.
	 * @param status
	 */
	static void correctAddressFormatFromAddress(boolean status){
		List<String[]> addressLines = U.readCsvFile(inputAddressFile);
		String [] lines = null;
		int x = 0;
		Iterator<String[]> it = addressLines.iterator();
		
		while(it.hasNext()){
			lines = it.next();			
			if(x++ == 0)continue;

			if(lines[6].trim().length() == 4)lines[6] = "0"+lines[6].trim();
			lines[2] = lines[2].replace(" ", " ").trim();
			lines[2] = U.toTitleCase(lines[2].trim());
			if(lines[2].trim().contains(lines[6])){
				String address = lines[2].trim().substring(lines[2].indexOf(lines[6]));
				if(address != null){
					U.log(address);
					lines[2] = lines[2].trim().replace(address, "").replace(" ", " ").trim()
							.replace(" Cp", "").replaceAll(",$", "");
					
					if(lines[2].endsWith(U.toTitleCase(lines[4].trim()))){
						
						lines[2] = lines[2].trim().replaceAll(U.toTitleCase(lines[4].trim())+"$", "");
						U.log(lines[4]+"\t\t>>"+lines[2]);
					}
				}
			}
		}
		//write address files
		if(status)
		U.writeCsvFile(addressLines, inputAddressFile.replace(".csv", "_Correct_Ad.csv"));
	}
	
	static void findDuplicateColoniaFromAddress(boolean status){
		List<String[]> readLines = U.readCsvFile(originalFile);
		String [] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		
		while(it.hasNext()){
			lines = it.next();			
			if(x++ == 0)continue;

			lines[8] = lines[8].trim().replaceAll(",$", "");
			if(!lines[9].trim().isEmpty()){
				if( U.toTitleCase(lines[8]).endsWith(U.toTitleCase(lines[9].trim())) ){
					U.log("Index : "+x+"\t\tAdd :"+lines[8]+"\t\tColonia ::"+lines[9]);
					lines[8] = U.toTitleCase(lines[8]).replaceAll(", "+U.toTitleCase(lines[9])+"$", "");
					U.log(">>> Add : "+lines[8]);
				}
			}
			lines[8] = lines[8].trim().replaceAll(",$", "");
		}
		if(status)
			U.writeCsvFile(readLines, originalFile.replace(".csv", "_REM_COL.csv"));
	}
	
	void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(inputFile));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
	}
	
	void correctCompanyName(){
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
/*			U.log(Arrays.toString(lines));
			U.log(lines.length);
			if (x==10)break;
*/
//			lines[8] = lines[8].trim().replaceAll(",$|^,|^ |-$|^ |^ ", "").replace(", ,", ","); // address
//			lines[9] = lines[9].trim().replaceAll("\\.$|,$|^,|^ |^-|^ ", "");  //neighbourhood
	
			lines[7] = lines[7].replaceAll("<.*?>", "");
			lines[20] = lines[20].replaceAll("<.*?>", "");
		}
		
		U.log("Swap column here..");
	}
	
	void separatedNonEmptyCompanyName(){
		List<String[]>  readEmptyCompanyLines = new ArrayList<>();
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0){
				readEmptyCompanyLines.add(lines);
				continue;
			}
			
			if(lines[7].trim().isEmpty()){
				readEmptyCompanyLines.add(lines);
				it.remove();
			}
		}
		writeFile(readLines, inputFile.replace(".csv", "_NonEmptyCompany.csv"));
		writeFile(readEmptyCompanyLines, inputFile.replace(".csv", "_EmptyCompany.csv"));
		U.log("correct company name here..");
	}
	
	void separatedNonEmptyNaisc(){
		List<String[]>  readEmptyNaiscLines = new ArrayList<>();
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0){
				readEmptyNaiscLines.add(lines);
				continue;
			}
			
			if(lines[4].trim().isEmpty()){
				readEmptyNaiscLines.add(lines);
				it.remove();
			}
		}
		writeFile(readLines, inputFile.replace(".csv", "_NonEmptyNaisc.csv"));
		writeFile(readEmptyNaiscLines, inputFile.replace(".csv", "_EmptyNaisc.csv"));
		U.log("separate empty & not empty naisc code here..");
	}
	
	void validateFile(){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;

			for(int i = 0; i< lines.length; i++){
				
				if( i == 15 || i == 16 || i ==27){
					lines[i] = lines[i].toLowerCase();
				}else if(i == 13){
					lines[i] = U.formatNumbersAsCode(lines[i]).trim();
				}else{
					lines[i] = U.toTitleCase(lines[i]).trim();
				}
			}
		}
		U.log("Validate file here..");
	}
	
	void changedCoulmnCountent(){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
/*			U.log(Arrays.toString(lines));
			U.log(lines.length);
			if (x==10)break;
*/
//			lines[8] = lines[8].trim().replaceAll(",$|^,|^ |-$|^ |^ ", "").replace(", ,", ","); // address
//			lines[9] = lines[9].trim().replaceAll("\\.$|,$|^,|^ |^-|^ ", "");  //neighbourhood
	
			lines[6] = lines[2];
			lines[2] = lines[24];
			lines[24] = "";
		}
		writeFile(readLines, inputFile.replace(".csv", "_ChangeCol.csv"));
		U.log("correct company name here..");
		
	}
	
	static void innerJoinCompanyNameUniqueRecord(){
		List<String[]> readLines = U.readCsvFile(originalFile);
		
		Set<String> uniqueKey = new HashSet<>();		
		Iterator<String[]> it = readLines.iterator();
		String[] lines = null;
		int x = 0;
		while(it.hasNext()){
			lines = it.next();
			
			if(x++ == 0)continue;
			String unique = lines[4].trim()+"#"+lines[5].trim();
			if(!uniqueKey.add(unique)){
				it.remove();
				continue;
			}
			
			unique = lines[5].trim()+"#"+lines[4].trim();
			if(!uniqueKey.add(unique)){
				it.remove();
				continue;
			}
		}//eof while
		
		U.writeCsvFile(readLines, originalFile.replace(".csv", "_unique.csv"));
		
		
	}
	void writeFile(){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(inputFile.replace(".csv", "_Correct.csv")),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing validate file.....Done");
	}
	
	public static void writeFile(List<String[]> lines, String fileName){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(fileName),',');
			writer.writeAll(lines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log(fileName+" Writing file.....Done");
	}

}
