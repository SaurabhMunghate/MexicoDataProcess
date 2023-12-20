package com.tequila.address;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class AddressAdder {

	public static void main(String[] args) {

		AddressAdder add = new AddressAdder();
		
		add.loadFile();
		add.addDataFromCorrectedAddressFile();
		add.addDataFromAddressFile();
		add.writeModified();
		
	}
	
	
	/**
	 * This field is used to set original file path.
	 */
	private static final String FILE_PATH = "/home/glady/MexicoCache/Tequila_DATA/files/Tested/Tiendo/tiendo-DepartmentStores/Tiendeo-DepartmentStores1.csv";
	/**
	 * This field is used to set corrected address file path.
	 */
	private static final String CORRECTED_ADDRESS_FILE = "/home/glady/MexicoCache/ADDRESS_TEQUILA/Tiendeo-DepartmentStores1_AddressFile_wrong_address.csv";
	/**
	 * This field is used to set address file path.
	 */
	private static final String ADDRESS_FILE = "/home/glady/MexicoCache/ADDRESS_TEQUILA/Tiendeo-DepartmentStores1_AddressFile_Modified.csv";
	
	private List<String[]> readLines = null;
	
	/**
	 * This method is used to load original data file.
	 */
	void loadFile(){		
		try(CSVReader reader = new CSVReader(new FileReader(FILE_PATH));){
			readLines = reader.readAll();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to load correct address file.
	 * @return
	 */
	List<String[]> loadAddressCorrectedFile(){	
		List<String[]> readCorrectAddressLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(CORRECTED_ADDRESS_FILE));){
			readCorrectAddressLines = reader.readAll();
		}catch(IOException e){
			e.printStackTrace();
		}
		return readCorrectAddressLines;
	}
	/**
	 * This method is used to load address file.
	 * @return
	 */
	List<String[]> loadAddressFile(){	
		List<String[]> readAddressLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(ADDRESS_FILE));){
			readAddressLines = reader.readAll();
		}catch(IOException e){
			e.printStackTrace();
		}
		return readAddressLines;
	}
	
	/**
	 * This method is used to store data from correct address file.
	 * @return
	 */
	Map<String, String[]> loadAddressCorrectedMap(){
		Map<String, String[]> addressCorrectedMap = new HashMap<>();
		List<String[]> readCorrectAddressLines = loadAddressCorrectedFile();
		readCorrectAddressLines.remove(0);
		
		String uniqueKey = null;
		
		for(String[] lines : readCorrectAddressLines){
			/*
			 * uniqueKey = STREET_ADDRESS + NEIGHBOURHOOD + CITY + STATE
			 */
			uniqueKey = lines[0].trim()+lines[1].trim()+lines[2].trim()+lines[3].trim();
			
			addressCorrectedMap.put(uniqueKey.toLowerCase(), lines);
		}
		
		return addressCorrectedMap;
	}
	/**
	 * This method is used to store data from address file.
	 * @return
	 */
	Map<String, String[]> loadAddressMap(){
		Map<String, String[]> addressMap = new HashMap<>();
		List<String[]> readAddressLines = loadAddressFile();
		readAddressLines.remove(0);
		
		String uniqueKey = null;
		
		for(String[] lines : readAddressLines){
			/*
			 * uniqueKey = STREET_ADDRESS + NEIGHBOURHOOD + CITY + STATE
			 */
			uniqueKey = lines[0].trim()+lines[1].trim()+lines[2].trim()+lines[3].trim();
			
			if(!lines[9].trim().isEmpty() && !lines[10].trim().isEmpty())
				addressMap.put(uniqueKey.toLowerCase(), lines);
		}
		
		return addressMap;
	}
	/**
	 * This method is used to add data from correct address file into original file.
	 * @throws Exception
	 */
	void addDataFromCorrectedAddressFile(){
		Map<String, String[]> addressCorrectedMap = loadAddressCorrectedMap();
		
		int i = 0;
		int correctedAddressCount = 0;
		String lines[] = null;
		
		String uniqueKey = null;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0){
				continue;
			}
			/*
			 * uniqueKey = STREET_ADDRESS + NEIGHBOURHOOD + CITY + STATE
			 */
			uniqueKey = lines[8].trim()+lines[9].trim()+lines[10].trim()+lines[11].trim();
			
			if(addressCorrectedMap.containsKey(uniqueKey.toLowerCase())){
				if(lines[22].trim().isEmpty() && lines[23].trim().isEmpty()){
					String[] vals = addressCorrectedMap.get(uniqueKey.toLowerCase());
					
					if(!vals[11].trim().isEmpty()){
						lines[8] = vals[11].trim(); //STREET_ADDRESS
					}
					if(!vals[12].trim().isEmpty()){
						lines[9] = vals[12].trim(); //NEIGHBOURHOOD
					}
					if(!vals[13].trim().isEmpty()){
						lines[10] = vals[13].trim(); //CITY
					}
					if(!vals[14].trim().isEmpty()){ 
						lines[11] = vals[14].trim(); //STATE
					}
					if(!vals[15].trim().isEmpty()){
						lines[12] = vals[15].trim(); //ZIP
					}
					if(!vals[16].trim().isEmpty()){
						lines[22] = vals[16].trim(); //LATITUDE
					}
					if(!vals[17].trim().isEmpty()){
						lines[23] = vals[17].trim(); //LONGITUDE
					}
					
					correctedAddressCount++;
				}
			}
		}
		
		U.log("Total count of address correct from correct address file :::"+correctedAddressCount);
	}
	/**
	 * This method is used to add data from address file into original file.
	 * @throws Exception
	 */
	void addDataFromAddressFile(){
		Map<String, String[]> addressMap = loadAddressMap();
		
		int i = 0;
		int addressCount = 0;
		
		String lines[] = null;
		
		String uniqueKey = null;

		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0){
				continue;
			}
			/*
			 * uniqueKey = STREET_ADDRESS + NEIGHBOURHOOD + CITY + STATE
			 */
			uniqueKey = lines[8].trim()+lines[9].trim()+lines[10].trim()+lines[11].trim();
			
			if(addressMap.containsKey(uniqueKey.toLowerCase())){
				if(lines[22].trim().isEmpty() && lines[23].trim().isEmpty()){
					String[] vals = addressMap.get(uniqueKey.toLowerCase());
					
					//ZIP
					if(!vals[8].trim().isEmpty()){ 
						if(lines[12].trim().equals(vals[8].trim())){
							if(lines[9].trim().isEmpty() && !vals[7].trim().isEmpty()){
								lines[9] = vals[7].trim();  //NEIGHBOURHOOD
							}
						}
					}

					if(!vals[9].trim().isEmpty()){
						lines[22] = vals[9].trim(); //LATITUDE
					}
					if(!vals[10].trim().isEmpty()){
						lines[23] = vals[10].trim(); //LONGITUDE
					}
					addressCount++;
				}
			}
		}
		U.log("Total count of correct address from address file :::"+addressCount);
	}
	/**
	 * This method is used to write new file of modified original file.
	 */
	void writeModified(){
		try(CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH.replace(".csv", "_ADDRESS_CORRECTION.csv")));){
			writer.writeAll(readLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		U.log("file is write here..");
	}

}
