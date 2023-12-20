package com.shatam.lightbox.loqate;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.shatam.utils.U;

public class MergeUpdateFile {
	String DIR_PATH = "/home/chinmay/Mexico/Mexicoupdate/Jan-17/Output";

	public static void main(String[] args) {
		new MergeUpdateFile().loadAllFiles();
	}
	static String[] headerFile= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE",
			"YEARS_IN_BIZ","EMP_COUNT_MIN","EMP_COUNT_MAX","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX","CREATED_DATE","SCORE","HOURS_OF_OPERATION","LOCATION_SOURCE","QUALITY_SCORE","GEO_DISTANCE"
			,"GEO_ACCURACY_CODE","ADDRESS_VERIFICATION_CODE"};
	void loadAllFiles() {

		List<String[]> readAllLines = new ArrayList<>();

		Set<String[]> header = new HashSet<>();

		File[] files = new File(DIR_PATH).listFiles();

		for (File file : files) {
			U.log("File Name ::" + file.getName());
			if (file.isDirectory()) {
				U.log("Directory found so continue");
				continue;
			}
			List<String[]> readLines = loadFiles(file);
			U.log("Record Size ::" + readLines.size());
			header.add(readLines.get(0));
			readLines.remove(0);
			readAllLines.addAll(readLines);
		}

		U.log("Size of header ::" + header.size());
		U.log("Read All Records ::" + readAllLines.size());
		int start=0;
		int end=100000;
		while(end<readAllLines.size()) {
			List<String[]> temp=new ArrayList<>();
			temp.add(headerFile);
			temp.addAll(readAllLines.subList(start, end));
			U.writeCsvFile(temp, "/home/chinmay/Mexico/LightBoxAddress/Jan-17/AddressCorrection/Update-"+start+"_"+end+".csv");
			start=end;
			end+=100000;
		}
		

		//loadUniqueRecords(readAllLines, new ArrayList<>(header).get(0));
	}
	List<String[]> loadFiles(File file){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(file));){
			readLines = reader.readAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return readLines;
	}
}
