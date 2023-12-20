package com.shatam.scrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.U;

public class CsvScrapper {

	public static void main(String[] args) {
//		splitCsvIntoTwo(1+1356);
		createSeparateAliseCsvForCompanyName("Herbalife");
//		U.log(U.formatNumbersAsCode("52 55 5366 0300"));
	}
	
	private static final String FILE_NAME = 
			"/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Correct_A_ADD_A_C_P.csv";
	
	/**
	 * This method is used to split csv into two csv's.
	 * @param rows	rows is the count of rows. This count of rows is added at first split csv file and afterward rows is added at second split csv file.
	 */
	static void splitCsvIntoTwo(int rows){
		List<String[]> writeLines = new ArrayList<>();

		List<String[]> readLines = U.readCsvFile(FILE_NAME); 		//load input file here
		
		String lines[] = null;
		int i = 0;
		
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0){
				writeLines.add(lines);
				continue;
			}
			if(i >= rows){
				writeLines.add(lines);
				it.remove();
			}
		}
		U.writeCsvFile(readLines, FILE_NAME.replace(".csv", "_Split_1.csv"));
		U.writeCsvFile(writeLines, FILE_NAME.replace(".csv", "_Split_2.csv"));
	}

	static void createSeparateAliseCsvForCompanyName(String companyName){
		List<String[]> writeLines = new ArrayList<>();

		List<String[]> readLines = U.readCsvFile(FILE_NAME); 		//load input file here
		
		String lines[] = null;
		int i = 0;
		
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0){
				writeLines.add(lines);
				continue;
			}
			if(lines[7].trim().contains(companyName.trim())){
				writeLines.add(lines);
			}
		}
		U.writeCsvFile(writeLines, FILE_NAME.replace(".csv", "_"+companyName+".csv"));		
	}
	
}
