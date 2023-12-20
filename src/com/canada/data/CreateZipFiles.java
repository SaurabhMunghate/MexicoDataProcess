package com.canada.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shatam.utils.U;

public class CreateZipFiles {

	static String inputFile = "/home/glady/CanadaSAC/Canada_cities_data/zip_states.csv";
	static String outputFile = "/home/glady/CanadaSAC/Canada_cities_data/ZIP_CODES_CA.txt";
	
	public static void main(String[] args) throws IOException {
		generateZipFileForSAC();
	}
	
	static void generateZipFileForSAC() throws IOException{
		Set<String> uniqueSet = new HashSet<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(inputFile);
		FileWriter fw = new FileWriter(outputFile);
		int count = 0;
		String data = null;
		for(String[] lines : readLines){
			String zip = getZip(lines[0]);
			String cityName = lines[1];
			String areaName = lines[2];
			String state = lines[3];
			
			if(zip != null && cityName.toUpperCase().trim().equals(areaName.toUpperCase().trim())){
				if(!state.isEmpty()){
					data = zip+","+cityName.toUpperCase().trim()+","+state.toUpperCase()+"\n";
					if(uniqueSet.add(data)){
						fw.write(data);
						count++;
					}
				}
			}else if(zip != null && !cityName.toUpperCase().trim().equals(areaName.toUpperCase().trim())){
				if(!state.isEmpty()){
					data = zip+","+cityName.toUpperCase().trim()+","+state.toUpperCase()+"\n";
					if(uniqueSet.add(data)){
						fw.write(data);
						count++;
					}

					data = zip+","+areaName.toUpperCase().trim()+","+state.toUpperCase()+"\n";
					if(uniqueSet.add(data)){
						fw.write(data);
						count++;
					}
				}
			}
		}//eof for
		
		fw.flush();
		fw.close();
		U.log("Count ::"+count);
	}
	
	static String getZip(String zip){
		if(zip.length() == 3){
			if(isZip(zip))return zip;
		}
		if(zip.trim().length() == 7 || zip.trim().length() == 6){
			zip = zip.substring(0, 3);
			if(isZip(zip.trim()))return zip.trim();
		}
		return null;
	}
	
	static boolean isZip(String zip){
		if(zip.matches("[A-Z]{1}\\d[A-Z]{1}"))return true;
		return false;
	}
}
