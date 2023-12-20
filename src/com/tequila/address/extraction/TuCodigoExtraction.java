package com.tequila.address.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;

public class TuCodigoExtraction {

	public static void main(String[] args) throws IOException {
		startExtraction();
		U.writeSerializedFileWithStringArray(multiMap, SOURCE_DIR + SER_FILE_NAME);
		U.writeSerializedFileWithString(cityZipMap, SOURCE_DIR + CITY_ZIP_SER_FILE);
		U.writeCsvFile(HEADER, listDataset, MEXICO_ADDRESS_DIR_FILE_PATH);
	}

	private static final String MAIN_URL = "http://tucodigo.mx/";
	
	private static final String HEADER[] = {"State","Colonia","Municipality","City","Postal Code"};
	
	private static final String MEXICO_ADDRESS_DIR_FILE_PATH = "/home/glady/MexicoCache/source/Mexico_Address_Direcory_TuCodigo.csv";
	
	
	private static final String SER_FILE_NAME = "Tu_Codigo_Address_Direcory.ser";
	private static final String CITY_ZIP_SER_FILE = "Tu_Codigo_City_Zip.ser";
	
	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/Mexico/";
	
	
	private static MultiMap<String, String[]> multiMap =  new MultiValueMap<>();
	private static Map<String,String> cityZipMap = new HashMap<>();
	
	
	private static List<String[]> listDataset = new ArrayList<>();
	
	static void startExtraction() throws IOException{
		
		String html = U.getHTML(MAIN_URL);
		String section = U.getSectionValue(html, "</form>", "<hr size=");
		
		String [] urlSection = U.getValues(section, "a href=", "<br");
		for(String urlSec : urlSection){
//			U.log(urlSec);
			
			String regUrl = U.getSectionValue(urlSec, "\"", "\">");
			String state = U.getSectionValue(urlSec, "\">", "</a");
//			U.log(regUrl+"\t"+state+"\t\t"+U.matchState(state));
			extractRegionData(MAIN_URL+regUrl, U.matchState(state));
//			break;
		}
	}
	
	static void extractRegionData(String url, String state) throws IOException{
		U.log("RegUrl ::"+url);
		String html = U.getHTML(url);
		
		String section = U.getSectionValue(html, "</strong></td></tr>", "<table");
		String[] municipalitiesSec = U.getValues(section, "<a href=", "<br");
		for(String sec : municipalitiesSec){
			String municipalitiesUrl = U.getSectionValue(sec, "\"", "\">");
			String municipality = U.getSectionValue(sec, "\">", "</a>");
			if(municipality.contains("&"))throw new IllegalArgumentException("Municipality is not in proper format '"+municipality+"\'");
//			U.log(municipalitiesUrl+"\t"+municipality);
			extractMunicipalitiesData(MAIN_URL + municipalitiesUrl, municipality, state);
		}
	}
	
	static void extractMunicipalitiesData(String url, String municipality, String state) throws IOException{
		U.log("MunicipalityUrl ::"+url);
		String html = U.getHTML(url);
		
		String section = U.getSectionValue(html, "Postal</strong></font>", "<table");
		
		String municipalitySection [] = U.getValues(section, "<tr bgcolor=", "</tr>");
		
		String vals[] = null;
		for(String sec : municipalitySection){
			vals = getMunicipalitiesData(sec, municipality, state);
			multiMap.put(vals[4], vals); //zip, values
			listDataset.add(vals);
			
			if(!vals[3].isEmpty()) cityZipMap.put(vals[4], vals[3]); //City
			else if(!vals[2].isEmpty()) cityZipMap.put(vals[4], vals[2]); //Municipality
			
			vals = null;
		}
	}
	
	static String[] getMunicipalitiesData(String section, String municipality, String state){
		
		String [] info = {"","","","",""}; //state,colonia, municipality, city, zip
		String [] vals = U.getValues(section, "<td>", "</td>");
		
		info[0] = state;
		info[2] = municipality;				
		for(int i = 0; i < vals.length; i++){
			
			if(i == 0){
				vals[i] = vals[i].replace("<font face=\"arial\" size=\"2\">", "");
				info[1] = U.getSectionValue(vals[i], "\">", "</a>").trim(); //colonia
				
				if(info[1].contains("&"))throw new IllegalArgumentException("Colonia is not in proper format '"+info[1]+"\'");
			}
			if(i == 1){
				info[3] = U.getSectionValue(vals[i], "\">", "</font>").trim(); //city
				if(info[3].contains("&"))throw new IllegalArgumentException("City is not in proper format '"+info[3]+"\'");
			}
			if(i == 3){
				info[4] = U.getSectionValue(vals[i], "<center>", "</center>").trim(); //zip
			}
		}
		return info;
	}
	
}
