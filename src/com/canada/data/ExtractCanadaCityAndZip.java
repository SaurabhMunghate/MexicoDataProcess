package com.canada.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.Path;
import com.shatam.utils.U;

public class ExtractCanadaCityAndZip {

	public static void main(String[] args) throws IOException {
		extractFromZipCodes();
	}

	private static String[] states = {"AB","BC","MB","NB","NL","NS","NT","NU","ON","PE","QC","SK","YT"};
	
	static String header[] = {"INDEX","ZIP","CITY","STATE","AREA_CODE","LATITUDE","LONGITUDE"};
	static int index = 0;
	
	static List<String[]> dataset = new ArrayList<>();
	
	static String mainUrl = "https://www.zip-codes.com";
	
	
	static void extractFromZipCodes() throws IOException{
		
		dataset.add(header); //add header at dataset
		
		String url = "https://www.zip-codes.com/canadian/province.asp?province=";
		for(String state : states){
			
			String html = U.getHTML(url+state);
			
			String section = U.getSectionValue(html, "<h2 class=\"dtl\">", "<style type=\"text/css\">");
			String[] cityUrlSections = U.getValues(section, "<li>", "</li>");
			for(String cityUrlSection : cityUrlSections){
				if(cityUrlSection.contains("area-code"))continue;
				
				String cityUrl = U.getSectionValue(cityUrlSection, "<a href=\"", "\">");
			
				if(cityUrl != null){
					String cityNameWithState = U.getSectionValue(cityUrlSection, ">", "</a>");
					U.log("City Name ::"+cityNameWithState);
					findZips(mainUrl + cityUrl);
				}else{
					U.log("Not found url for city ::"+cityUrlSection);
				}
			}			
		}//eof for
		
		if(dataset.size() > 1){
			U.writeCsvFile(dataset, Path.CACHE_PATH_FOR_EXTRACTION+"Zip_Cities_For_Canada.csv");
		}
	}
	
	static void findZips(String url) throws IOException{
		String html = U.getHTML(url);
		String section = U.getSectionValue(html, "Canada Postal Codes</h2>", "</ul>");
		String [] postalCodeUrlSections = U.getValues(section, "<li>", "</li>");
		for(String postalCodeUrlSection : postalCodeUrlSections){

			String postalCodeUrl = U.getSectionValue(postalCodeUrlSection, "<a href=\"", "\">");
			
			if(postalCodeUrl != null){
				postalCodeUrl = mainUrl + postalCodeUrl;
				addDetails(postalCodeUrl);
			}else{
				U.log("Not found url for postal code :"+postalCodeUrlSection);
			}
		}
	}
	
	static void addDetails(String url) throws IOException{
		String html = U.getHTML(url);
		String section = U.getSectionValue(html, "<table border=", "</table>");
		
		if(section != null){
			String zip = U.getSectionValue(section, "Postal Code:</td>", "</td>");
			zip = toTrim(zip);
			
			String city = U.getSectionValue(section, "City:</td>", "</td>");
			city = toTrim(city);
			
			String areaName = U.getSectionValue(section, "Area Name:</td>", "</td>");
			areaName = toTrim(areaName);
			
			String state = U.getSectionValue(section, "Province:</td>", "</td>");
			state = toTrim(state);
			
			String areaCode = U.getSectionValue(section, "Area Code:</td>", "</td>");
			areaCode = toTrim(areaCode);
			
			String latitude = U.getSectionValue(section, "Latitude:</td>", "</td>");
			latitude = toTrim(latitude);
			
			String longitude = U.getSectionValue(section, "Longitude:</td>", "</td>");
			longitude = toTrim(longitude);
			
			if(!city.isEmpty())
				dataset.add(new String[]{""+index++, zip, city, state, areaCode, latitude, longitude});
			
			//In case areaName & city are differ.
			if(!areaName.isEmpty() && !city.equalsIgnoreCase(areaName)){
				dataset.add(new String[]{""+index++, zip, areaName, state, areaCode, latitude, longitude});
			}
			
		}else{
			U.log("Not Found for zip details, url is  ::"+url);
		}
	}
	
	static String toTrim(String val){
		if(val == null)return "";
		val = val.replaceAll("<.*?>", "").replaceAll("\n|\\s{2,}", "").trim();
		return val;
	}
}
