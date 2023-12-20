package com.tequila.address;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class SplitAddressFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SplitAddressFile split = new SplitAddressFile();
		split.loadFile();
		split.splitFile();
		split.writeSplitFile();
		
	}
	
	/**
	 * This field is used to set original file path.
	 */
	private static final String FILE_PATH = "/home/shatam-10/MexicoCache/Cache/BuffaloWildWings.csv";
//	private static final String FILE_PATH = "/home/shatam-100/Cache/All_Unique_Record_12_06_2023_N_0_4162_CC_500_3300.csv";


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
	
	void splitFile(){
		
		List<String[]>  splitDataList = new ArrayList<>();
		
		String[] lines = null;
	
		int i = 0;
		int deletedCount=0;
		
		Iterator<String[]> it = readLines.iterator();
		
		while (it.hasNext()) {
			lines= it.next();
			
			if(i++ == 0){
				
				System.out.println("Came here lines: "+lines[23]);
				System.out.println("Came here lines: "+lines[24]);
				
				splitDataList.add(lines);
				continue;
			}
			
			
			if(lines[23].trim().isEmpty() && lines[24].trim().isEmpty()){
				
//				System.out.println("inside lines[23]: "+lines[23]+" and lines[24]: "+lines[24]);
				
				splitDataList.add(lines);
				it.remove();
				deletedCount++;
			}
		}
		U.log("Count of split records ::"+deletedCount);
		writeSplitFile(splitDataList);
	}
	
	/**
	 * This method is used to write new file of split records from original file.
	 */
	void writeSplitFile(List<String[]>  splitDataList){
		try(CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH.replace(".csv", "_Split_Record.csv")));){
			writer.writeAll(splitDataList);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		U.log("file is write here..");
	}
	
	/**
	 * This method is used to write new file of modified original file.
	 */
	void writeSplitFile(){
		try(CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH.replace(".csv", "_With_LatLng.csv")));){
			writer.writeAll(readLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		U.log("file is write here..");
	}

}
