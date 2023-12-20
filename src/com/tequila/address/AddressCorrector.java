package com.tequila.address;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.geoboundary.Boundary;
import com.shatam.utils.U;

public class AddressCorrector {

	
	public static void main(String[] args) throws Exception {
		addressCorrector();
	}

	private static final String DIR_PATH = "/home/glady/MexicoCache/ADDRESS_TEQUILA/";
	
	private static final String ADDRESS_FILE = DIR_PATH + "Tiendeo-DepartmentStores1_AddressFile.csv";

	private static final String COUNTRY = "Mexico";
	
	private final static String[] header = {"STREET_ADDRESS","NEIGHBOURHOOD","CITY","STATE","ZIP","KEY","FORMATTED_ADDRESS","NEIGHBOURHOOD_Google","ZIP_Google","LATITUDE","LONGITUDE",
			"CORRECT_STREET_ADDRESS","CORRECT_NEIGHBOURHOOD","CORRECT_CITY","CORRECT_STATE","CORRECT_ZIP","CORRECT_LATITUDE","CORRECT_LONGITUDE"};
	
	static List<String[]> readLines = null;
	static void loadAddressFile(){		
		try(CSVReader reader = new CSVReader(new FileReader(ADDRESS_FILE));){
			readLines = reader.readAll();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	

	static void addressCorrector() throws Exception{
		loadAddressFile();
		
		List<String[]> wrongAddressLines = new ArrayList<>();
		int i = 0;
		String lines[] = null;
		
		String uniqueKey = null;
		
	
		int deleteCount=0;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0){
				continue;
			}
			
			U.log(i+"] "+lines.length+"\t"+Arrays.toString(lines));
			
			/*
			 * uniqueKey = STREET_ADDRESS + NEIGHBOURHOOD + CITY + STATE
			 */
			uniqueKey = lines[0].trim()+lines[1].trim()+lines[2].trim()+lines[3].trim();
			
//			lines[5] = uniqueKey;
			
			if(!lines[9].trim().isEmpty() && !lines[9].trim().isEmpty()){
			
				if(!Boundary.boundaryCheckForState(U.matchState(lines[3].trim()), lines[9].trim(), lines[10].trim(), COUNTRY)){
					
					wrongAddressLines.add(lines);
					it.remove();
//					throw new Exception("Not within boundary for state :"+lines[3]+"\tKey :"+lines[5]);				
					deleteCount++;
				}
			}
		}
		U.log("Count of failed bounday Condition is ::"+deleteCount);
		writeWrongAddress(wrongAddressLines,header);
		
		writeModified(readLines);
	}
	
	static void writeModified(List<String[]> readLines){
		try(CSVWriter writer = new CSVWriter(new FileWriter(ADDRESS_FILE.replace(".csv", "_Modified.csv")));){
			writer.writeAll(readLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		U.log("file is write here..");
	}
	
	static void writeWrongAddress(List<String[]> wrongAddressLines, String[] header){
		try(CSVWriter writer = new CSVWriter(new FileWriter(ADDRESS_FILE.replace(".csv", "_wrong_address.csv")));){
			writer.writeNext(header);
			writer.writeAll(wrongAddressLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		U.log("file is write here..");
	}
	
	void separateFormattedAddress(String formattedAddress){
		String add[] = {"","","","","","",""}; //
	}
}
