package com.tequila.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.shatam.utils.U;
import com.shatam.utils.Util;

public class TiendoExtraction {

	public static void main(String[] args) {
//		extractForEmptyLatLong();
		
//		validateCompanyName();
	
		extactAddress();
//		validateAndCorrectCompanyName(true);
		
	}
	
	private static final String FILE_NAME = 
			"/home/glady/Downloads/MexicoProject/pramod/Tiendeo-Pharmacy_Added_LT_Correct_CMPY_NM_Correct.csv";
	
	/**
	 * This method is used to extract lat-long from tiendo
	 */
	private static void extractForEmptyLatLong(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		U.log("Total Record ::"+(readLines.size()-1));
		String lines[] = null;
		String[] latLong = null;
		
		Iterator<String[]> it = readLines.iterator();
		int i = 0;
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0)continue;
//			U.log(lines[22].isEmpty()+"\t"+lines[22].trim().length());
			if(lines[22].trim().isEmpty() && lines[23].trim().isEmpty()){
				latLong = extractLatLong(lines[24].trim());
				
				if(!latLong[0].trim().isEmpty() && !latLong[1].trim().isEmpty()){
					lines[22] = latLong[0];
					lines[23] = latLong[1];
				}
				latLong = null;
			}
		}
		
		U.writeCsvFile(readLines, FILE_NAME.replace(".csv", "_Added_LT.csv"));
	}

	private static String[] extractLatLong(String url){
		String[] latLong = {"",""};
		
		try {
			U.log("Url ::"+url);
			String html = U.getHTML(url);
			latLong[0] = U.getSectionValue(html, "storeLat\":", ",\"");
			latLong[1] = U.getSectionValue(html, "storeLon\":", "};");
			
			if(latLong[0] == null || latLong[1] == null)
				latLong[0] = latLong[1] = "";
			
			U.log("LatLng ::"+Arrays.toString(latLong));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return latLong;
	}

/*	private static void validateCompanyName(){
		List<String[]> readLines = U.readCsvWithoutHeader(FILE_NAME);
		
		String html = null;
		int i = 2;
		for(String [] lines : readLines){
			try {
				html = U.getHTML(lines[24].trim());
				if(html != null){
					if(!lines[7].trim().toLowerCase().contains(U.getSectionValue(html, "{\"Search\":\"", "\",\"").trim().toLowerCase())){
						U.log("Row ::"+i+"\tIndex::"+lines[0]+"\t\tI/P_Comy_nm :::"+lines[7].trim()+"\t\tFound ::"+U.getSectionValue(html, "{\"Search\":\"", "\",\""));
					}
					i++;
				}
				
			} catch (IOException e) {
				i++;
				e.printStackTrace();
			}
		}
	}*/
	
	private static void validateAndCorrectCompanyName(boolean status){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		
		String html = null;
		String htmlCompanyName = null;
		String [] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(x++ == 0)continue;
			try {
				html = U.getHTML(lines[24].trim());
				htmlCompanyName = U.getSectionValue(html, "{\"Search\":\"", "\",\"");
				if(html != null){
					if(htmlCompanyName.contains("tiendeo"))continue;
					if(!lines[7].trim().toLowerCase().contains(htmlCompanyName.trim().toLowerCase())){
						U.log("Row ::"+x+"\tIndex::"+lines[0]+"\t\tI/P_Comy_nm :::"+lines[7].trim()+"\t\tFound ::"+htmlCompanyName);
						lines[7] = htmlCompanyName.trim();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			htmlCompanyName = null;
		}
		if(status){
			U.writeCsvFile(readLines, FILE_NAME.replace(".csv", "_Correct_CMPY_NM.csv"));
		}
	}//eof validateAndCorrectCompanyName()
	
	
	private static void extactAddress(){
		String[] header = {"Index","Address","Street","Neighbourhood","City","State","Zip","ReferenceUrl"};
		
		List<String[]> writeLines = new ArrayList<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(FILE_NAME);
		String html = null;
		int row = 1;
		
		try{
			for(String[] lines : readLines){
				row++;
				U.log((row-1)+"] Url :"+lines[24].trim());
				html = U.getHTML(lines[24].trim());
				
				if(html == null)continue;
				
				String address = U.getSectionValue(html, " <span class=\"text-capitalize\">", "</span>");
				if(address != null) address = StringEscapeUtils.unescapeHtml4(address);
				else address = "";
				
				String addressAndColonia = U.getSectionValue(html, "id=\"address\" data-default=\"", "\">");
				if(addressAndColonia != null) addressAndColonia = StringEscapeUtils.unescapeHtml4(addressAndColonia);
				
				String street = "", colonia = "";
				if(addressAndColonia != null){
					colonia = Util.match(addressAndColonia, "((C|c)ol.?|Fracc.?|(c|C)olonia) .+");
					if(colonia == null){
						colonia = "";
					}
					if(!colonia.equals("")){
						street = addressAndColonia.replace(colonia, "").trim()
								.replaceAll(",$", "");
					}else street = addressAndColonia;
				}else addressAndColonia = ""; 
						
				String zip = U.getSectionValue(html, "PostalCode\":\"", "\",");
				if(zip != null)	zip = StringEscapeUtils.unescapeHtml4(zip);
				else zip = "";
				
				String city = U.getSectionValue(html, "City\":\"", "\",");
				if(city != null) city = StringEscapeUtils.unescapeHtml4(city);
				else city = "";
				
				writeLines.add(new String[]{
					lines[0],address.trim(),street.trim(),colonia.trim(),city.trim(),"",zip.trim(),lines[24].trim()
				});
				
			}//eof for
		}catch(Exception e){
			e.printStackTrace();
		}
		
		U.writeCsvFile(header, writeLines, FILE_NAME.replace(".csv", "_Address_File.csv"));
	}
}
