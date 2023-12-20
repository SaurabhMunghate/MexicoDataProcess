package com.tequila.address.extraction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class CreateAddressFile {

	public static void main(String[] args) {
		generateCityStateZipCsv();
//		generateCityStateZipSer();
	}
	
	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/"; //Mexico_Address_Direcory_MiCodigo.csv
	
	private static final String[] SOURCE_FILES = {
			"Mexico_Address_Direcory_MiCodigo.csv",
			"Mexico_Address_Direcory_TuCodigo.csv"
			};

	private static final String OUTPUT_CSV_FILE_NAME = SOURCE_DIR + "CityStateZip.csv";
	private static final String OUTPUT_SER_FILE_NAME = SOURCE_DIR + "CityStateZipSet.ser";

	
	static Map<String,String[]> uniqueDataset = new HashMap<>();
	
	static void generateCityStateZipCsv(){	
		/**
		 * uniqueKey = zip+(city/municipality)+state
		 */
		
		for(String file : SOURCE_FILES){
			
			List<String[]> readLines = loadCsvWithoutHeader(SOURCE_DIR + file);
			U.log("Load file :"+file+"\t\tSize :"+readLines.size());
			if(file.contains("MiCodigo")){
				for(String[] lines : readLines){		
					if(!lines[5].isEmpty()){ //city
						addToUniqueDataset(lines[3], lines[5], lines[0]);
					}//else 
					if(!lines[4].isEmpty()){ //municipality
						addToUniqueDataset(lines[3], lines[4], lines[0]);
					}
				}
			}
			else if(file.contains("TuCodigo")){
				for(String[] lines : readLines){		
					if(!lines[3].isEmpty()){ //city
						addToUniqueDataset(lines[4], lines[3], lines[0]);
					}//else 
					if(!lines[2].isEmpty()){ //municipality
						addToUniqueDataset(lines[4], lines[2], lines[0]);
					}
				}
			}
		}//eof for
		
		try(CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_CSV_FILE_NAME),',');){
			List<String[]> writeLines = new ArrayList<>();
			writeLines.addAll(uniqueDataset.values());
			writer.writeAll(writeLines);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("File writing is done on location :"+OUTPUT_CSV_FILE_NAME);
	}
	
	static Set<String> uniqueHashSet = new HashSet<>();
	static void generateCityStateZipSer(){	
		/**
		 * uniqueKey = zip+(city/municipality)+state
		 */
		
		for(String file : SOURCE_FILES){
			
			List<String[]> readLines = loadCsvWithoutHeader(SOURCE_DIR + file);
			U.log("Load file :"+file+"\t\tSize :"+readLines.size());
			if(file.contains("MiCodigo")){
				for(String[] lines : readLines){		
					if(!lines[5].isEmpty()){ //city
						addToUniqueHashset(lines[3], lines[5], lines[0]);
					}//else 
					if(!lines[4].isEmpty()){ //municipality
						addToUniqueHashset(lines[3], lines[4], lines[0]);
					}
				}
			}
			else if(file.contains("TuCodigo")){
				for(String[] lines : readLines){		
					if(!lines[3].isEmpty()){ //city
						addToUniqueHashset(lines[4], lines[3], lines[0]);
					}//else 
					if(!lines[2].isEmpty()){ //municipality
						addToUniqueHashset(lines[4], lines[2], lines[0]);
					}
				}
			}
		}//eof for
		U.writeSerializedFile(uniqueHashSet, OUTPUT_SER_FILE_NAME);
		U.log("File writing is done on location :"+OUTPUT_SER_FILE_NAME);
	}
	
	
	static void addToUniqueDataset(String zip, String city, String state){
		if(zip.trim().length() == 4){
			zip = "0"+zip.trim();
		}
		if(zip.trim().length() == 5){
			uniqueDataset.put((zip.trim()+city.trim()+state.trim()).toLowerCase(),
					new String []{
						zip.trim(),city.trim(),state.trim()
					}
			);			
		}
	}
	
	static void addToUniqueHashset(String zip, String city, String state){
		if(zip.trim().length() == 4){
			zip = "0"+zip.trim();
		}
		if(zip.trim().length() == 5){
			uniqueHashSet.add((zip.trim()+city.trim()+state.trim()).toLowerCase());			
		}
	}
	
	
	static List<String[]> loadCsvWithoutHeader(String fileName){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName),',','"',1);){
			readLines = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readLines;
	}
}
