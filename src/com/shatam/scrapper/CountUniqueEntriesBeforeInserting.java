package com.shatam.scrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.Corrector;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.tequila.database.CreateSerializofCompositeKey;

public class CountUniqueEntriesBeforeInserting {
	
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/shatam-10/mexico_files/uniqueKeyTequila.ser";
	private static final String UNIQUE_CSV_FILE = "/home/shatam-10/Downloads/Inegi_Information_0_8000_Charumam_13_Jan.csv";
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		long startTime = System.currentTimeMillis();
		getEntriesData();
		long endTime = System.currentTimeMillis();
		U.log("Total time taken in secs: "+(endTime - startTime)/1000);
	}
	
	private static void getEntriesData() throws FileNotFoundException, IOException {
		
		HashMap<String,String> companyNameMap = Corrector.loadCompanyNameStandardised(UNIQUE_CSV_FILE, 7);
		
		HashSet<String> uniqueKeyHashSetCSV = new HashSet<String>();
		
		Set<String[]> header = new HashSet<>();
		List<String[]> csvRowsComplete = new ArrayList<>();
		
		File csvFileName = new File(UNIQUE_CSV_FILE);
		List<String[]> readLines = readCSV(csvFileName);
		U.log("CSV Records Size = "+readLines.size());
		header.add(readLines.get(0));
		readLines.remove(0);
		csvRowsComplete.addAll(readLines);
		
		for(String[] csvRow:csvRowsComplete) {
			
			String companyName = companyNameMap.get(csvRow[7]);
			if(companyName == null)throw new IllegalArgumentException("Company Name is never be null. Input company name is "+csvRow[7]);
			//U.log("companyName: "+companyName);
			
			String address = csvRow[8].trim();
			//U.log("address: "+address);
			
			String uniqueKeyCSV = U.toTitleCase(csvRow[4].trim())+ U.toTitleCase(companyName.trim()) + U.toTitleCase(address) + U.toTitleCase(csvRow[10].trim()) + U.toTitleCase(csvRow[11].trim())
			+ U.toTitleCase(csvRow[17].trim());
			//U.log("uniqueKeyCSV: "+uniqueKeyCSV.toLowerCase().trim());
			
			uniqueKeyHashSetCSV.add(uniqueKeyCSV.toLowerCase().trim());
			//break;
		}
		U.log("uniqueKeyHashSetCSV size = "+uniqueKeyHashSetCSV.size());
		
		
		//reading the serialized file to get keys.
		int keyCount = 0;
		CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
		HashSet<String> uniqueKeyHashSet = chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
		U.log("Composite key set size:::"+uniqueKeyHashSet.size());
		
		getUniqueEntries(uniqueKeyHashSetCSV, uniqueKeyHashSet);
	}
	
	public static void getUniqueEntries(HashSet<String> uniqueKeyHashSetCSV, HashSet<String> uniqueKeyHashSet) throws FileNotFoundException, IOException {
		
		int keyCount = 0;
		int matchedRecords = 0;
		String hash = null;
		boolean match = false;
		
		//comparing uniqueKeys with csvRecords.  
		for(String uniqueSerKey:uniqueKeyHashSet) {
			//U.log("uniqueSerKey: "+uniqueSerKey);
			keyCount++;
			
			for(String uniqueKeyCSV:uniqueKeyHashSetCSV) {

				if(uniqueKeyCSV.equals(uniqueSerKey)) {
//					U.log("MATCHED.");
//					U.log("uniqueSerKey matched: "+uniqueSerKey);
//					U.log("uniqueKeyCSV matched: "+uniqueKeyCSV);
					matchedRecords++;
					match = true;
					break;
				}
			}
			//if(match) break;
		}
		U.log("Matched Records = "+matchedRecords);
		U.log("CSV Records Count = "+uniqueKeyHashSetCSV.size());
		U.log("Unique Records Count = "+(uniqueKeyHashSetCSV.size() - matchedRecords));
	}
	
	static List<String[]> readCSV(File file) {
		
		List<String[]> readLines = null;
		
		try(CSVReader reader = new CSVReader(new FileReader(file));){
			readLines = reader.readAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return readLines;
	}	
}