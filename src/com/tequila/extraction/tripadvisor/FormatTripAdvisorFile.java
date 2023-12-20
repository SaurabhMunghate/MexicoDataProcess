package com.tequila.extraction.tripadvisor;

import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;

import com.shatam.utils.Field;
import com.shatam.utils.U;

public class FormatTripAdvisorFile {
	
	public static void main(String[] args) {
		String inputFileName="/home/mypremserver/MexicoCache/Cache/Tripadvisor_Hotel_old.csv";
		List<String[]> inputData=U.readCsvFile(inputFileName);
		MultiValueMap inputFile=new MultiValueMap();
		for (String[] inputRecords : inputData) {
			if (inputRecords[0].contains(Field.ID.toString())) continue;
			String key="",value[]= {inputRecords[8],inputRecords[0]};
			if (inputFile.containsValue(key, value)) {
				inputFile.put(key, value);
			}
			
		}
	}
}
