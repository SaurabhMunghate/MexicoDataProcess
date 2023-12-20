package com.chinmay.test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class FromatHerePlaceApiFile {
	public static void main(String[] args) {
		String fileName="/home/chinmay/MexicoCache/Cache/TripAdvisorCSV/FULL_DATA_TRIP_ADVISOR/Test/Tripadvisor_Restaurant_Full_Non_Matched_PlacesSearch_2000-10000.csv";
		processFile(fileName);
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	static HashMap<String , String>categories=new HashMap<>() ;
	private static void processFile(String fileName) {	
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			U.log(fileName);
			writer.writeNext(HEADER);
			categories.put("eat-drink", "5812");
			categories.put("restaurant", "5812");
			categories.put("snacks-fast-food", "5812");
			categories.put("bar-pub", "5813");
			categories.put("coffee-tea", "5814");
			categories.put("coffee", "5814");
			categories.put("tea", "5814");
			categories.put("hotel", "7011");
			categories.put("motel", "7011");
			HashSet<String> uniqueSet=new HashSet<>();
			List<String[]> inputRecords=U.readCsvFile(fileName);
			int i=0;
			for (String[] input : inputRecords) {
//				U.log(Arrays.toString(input));
				if (input[0].contains("CATEGORY"))continue;
				String catArr[]=input[0].split(",");
				for (String cat : catArr) {
//					boolean catFlag=false;
//					if (cat.contains("restaurant")) {
//						
//					}
					String sicCode=categories.get(cat.replaceAll("\\[|\\]", "").toLowerCase().trim());
//					U.log(sicCode);
					String sicdetails[]=Sic.sicInfo(sicCode);
					String key=(input[1]+input[2]+input[4]+input[5]+sicCode).toLowerCase();
					if (uniqueSet.add(key)) {
						
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(cat),U.toTitleCase(sicdetails[6]),U.toTitleCase(input[1]),U.toTitleCase(input[2]),U.toTitleCase(input[3]),U.toTitleCase(input[4]),U.toTitleCase(input[5]),input[6].trim(),U.formatNumbersAsCode(input[11]),null,input[12],null,null,null,null,null,null,input[9],input[10],input[13],U.getTodayDate()};
						writer.writeNext(out);
					}
				}
				
//				break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_FORMATTED.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
