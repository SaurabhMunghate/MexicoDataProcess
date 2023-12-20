package com.sawan.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class ProcessRestaurantData {
//set For trip Advisor
	private static String FILE_NAME = "/home/chinmay/MexicoCache/Cache/Tripadvisor_Hotel_GOOGLESearch.csv";
	public static void main(String[] args) {
		start();
		seprtateQualityData();
	}

	private static void seprtateQualityData() {
		int companyPercentageIndex=0;
		int addressPercentageIndex=0;
		
		FILE_NAME=FILE_NAME.replace(".csv", "_Success_Match.csv");
//		FILE_NAME="/home/chinmay/MexicoCache/RestaurantGuruCSV/ExtractedFiles/temp/MERGEDFILE/DOne/SUCCESSMATCH/RestaurantGuru_Unique_File_0-30000_SUCCESS_MATCH.csv";
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		List<String[]> matchedData=new ArrayList<>();
		List<String[]> nonMatchedData=new ArrayList<>();
		for (String[] readData : readLines) {
			if (readData[0].contains("ID")||readData[0].contains("SrNo")) {
				for (int i = 0; i < readData.length; i++) {
					if (readData[i].contains("COMPANY_NAME_MATCH"))
						companyPercentageIndex=i;
					else if (readData[i].contains("ADDRESS_MATCH"))
						addressPercentageIndex=i;
					
				}
				
				matchedData.add(readData);
				nonMatchedData.add(readData);
				continue;
			}
//			U.log(readData[companyPercentageIndex]);
//			U.log(readData[addressPercentageIndex]);
			if (Integer.parseInt(readData[companyPercentageIndex])>95&&Integer.parseInt(readData[addressPercentageIndex])>95)
				matchedData.add(readData);
			else
				nonMatchedData.add(readData);
		}
		if(matchedData.size()>1)
		U.writeCsvFile(matchedData,FILE_NAME.replace(".csv", "_Matched.csv"));
		if(nonMatchedData.size()>1)
		U.writeCsvFile(nonMatchedData,FILE_NAME.replace(".csv", "_NON_Matched.csv"));
	}

	private static void start(){
		int companyGoogleName=0;
		int companyGoogleAddress=0;
		List<String[]> readLines = separateFoundAtHereApi();
//		if (readData[i].contains("GOOGLE_ADDRESS"))
//			companyGoogleAddress=i;
//		else if (readData[i].contains("GOOGLE_NAME"))
//			companyGoogleName=i;
		List<String[]> writeLines = new ArrayList<>();
		String HEADER[]=new String[readLines.get(0).length+2];
		System.arraycopy(readLines.get(0), 0, HEADER, 0, readLines.get(0).length);
		HEADER[readLines.get(0).length]="COMPANY_NAME_MATCH";
		HEADER[readLines.get(0).length+1]="ADDRESS_MATCH";
		writeLines.add(HEADER);
		//readLines.remove(0);
		
		Iterator<String[]> it = readLines.iterator();
		String[] lines = null;
		int i = 0;
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0)continue; //Ignore header
			
			//Comapre URL
			String url = lines[15].replaceAll("https?://|/$", "").toLowerCase();
			String hereUrl = lines[34].replaceAll("https?://|/$", "").toLowerCase();
			if(!url.isEmpty() && !hereUrl.isEmpty()){
				if(url.contains(hereUrl) || hereUrl.contains(url)){
					writeLines.add(lines);
					it.remove();
					continue;
				}
			}
			
			//Compare Phone
			String phone = lines[13].replaceAll("\\s|-|,|;|:|\\+|\\(|\\)", "");
			String herePhone = lines[33].replaceAll("\\s|-|,|;|:|\\+|\\(|\\)", "");
			if(!phone.isEmpty() && !herePhone.isEmpty()){
				if(phone.contains(herePhone) || herePhone.contains(phone)){
					writeLines.add(lines);
					it.remove();
					continue;
				}
			}
			
			//Address Compare
			String street = TextFormat.getAddressStandardised(lines[8]);
			String companyName = lines[7];
			String companyNameGoogle = lines[35];
			String hereStreet = TextFormat.getAddressStandardised(lines[27]);
			if(!street.isEmpty() && !hereStreet.isEmpty()){
				if(FuzzySearch.partialRatio(street, hereStreet) >= 85){
					String out[]=new String[lines.length+2];
					System.arraycopy(lines, 0, out, 0, lines.length);
					out[lines.length]=""+FuzzySearch.partialRatio(companyName.toLowerCase(), companyNameGoogle.toLowerCase());
					out[lines.length+1]=""+FuzzySearch.partialRatio(street.toLowerCase(), hereStreet.toLowerCase());
					writeLines.add(out);
					it.remove();
					continue;
				}
			}
			/*
			String colonia = lines[9];
			String city = lines[10];
			String state = lines[11];
			String zip = lines[12];
			
			String hereColonia = lines[27];
			String hereCity = lines[28];
			String hereState = lines[29];
			String hereZip = lines[30];
			*/
			String searchStreet = street+" "+lines[9]+" "+lines[10]+" "+lines[11]+" "+lines[12];
			String searchHereStreet = hereStreet+" "+lines[28]+" "+lines[29]+" "+lines[30]+" "+lines[31];
			
			if(FuzzySearch.partialRatio(searchStreet.toLowerCase(), searchHereStreet.toLowerCase()) >= 75){
				String out[]=new String[lines.length+2];
				
				System.arraycopy(lines, 0, out, 0, lines.length);
				out[lines.length]=""+FuzzySearch.partialRatio(companyName.toLowerCase(), companyNameGoogle.toLowerCase());
				//U.log(FuzzySearch.partialRatio(companyName.toLowerCase(), companyNameGoogle.toLowerCase()));
				//U.log(out[lines.length]);
				out[lines.length+1]=""+FuzzySearch.partialRatio(searchStreet.toLowerCase(), searchHereStreet.toLowerCase());
				writeLines.add(out);
				it.remove();
				continue;
			}
			
		}//eof while
		
		
		if(readLines.size() > 0){
			U.writeCsvFile(readLines, FILE_NAME.replace(".csv", "_Failed_Match.csv"));
		}
		if(writeLines.size() > 0){
			U.writeCsvFile(writeLines, FILE_NAME.replace(".csv", "_Success_Match.csv")); 
		}
		U.writeCsvFile(separateNotFoundAtHereApi(), FILE_NAME.replace(".csv", "_Not_Found_At_Here.csv"));
	}
	
	
	private static List<String[]> separateFoundAtHereApi(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(readLines.get(0));
		
		readLines.remove(0);
		for(String lines[] : readLines){
			if(!lines[34].isEmpty())
				writeLines.add(lines);
		}
		
		readLines = null;
		return writeLines;
	}
	
	private static List<String[]> separateNotFoundAtHereApi(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(readLines.get(0));
		
		readLines.remove(0);
		for(String lines[] : readLines){
			if(lines[34].isEmpty())
				writeLines.add(lines);
		}
		
		readLines = null;
		return writeLines;
	}
}
