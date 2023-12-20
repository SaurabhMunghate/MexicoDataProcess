package com.tequila.address.extraction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.CSVWriter;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;


public class MiCodigoPostalExtraction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5713363476385934150L;

	public static void main(String[] args) throws IOException {
		start(false);
	}

	private static final String MAIN_URL = "https://micodigopostal.org/";
	
	private static final String HEADER[] = {"State=Estados","Settlement=Asentamiento","Type Of Settlement=Tipo de Asentamiento",
			"Postal Code=Código Postal","Municipality=Municipio","City=Ciudad","Zone=Zona","Latitude","Longitude"};
	
	private static final String MEXICO_ADDRESS_DIR_FILE_PATH = "/home/glady/MexicoCache/source/Mexico_Address_Direcory_MiCodigo.csv";
	
	private static final String SER_FILE_NAME_0 = "0_Address_Direcory.ser";
	private static final String SER_FILE_NAME_1 = "1_Address_Direcory.ser";
	private static final String SER_FILE_NAME_2 = "2_Address_Direcory.ser";
	private static final String SER_FILE_NAME_3 = "3_Address_Direcory.ser";
	private static final String SER_FILE_NAME_4 = "4_Address_Direcory.ser";
	private static final String SER_FILE_NAME_5 = "5_Address_Direcory.ser";
	private static final String SER_FILE_NAME_6 = "6_Address_Direcory.ser";
	private static final String SER_FILE_NAME_7 = "7_Address_Direcory.ser";
	private static final String SER_FILE_NAME_8 = "8_Address_Direcory.ser";
	private static final String SER_FILE_NAME_9 = "9_Address_Direcory.ser";
	
	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/Mexico/";
	private static final String CITY_ZIP_SER_FILE = SOURCE_DIR + "cityZip.ser";
	private static final String STATE_ZIP_SER_FILE = SOURCE_DIR + "stateZip.ser";
	
//	private static final String SER_FILE_NAME = "MiCodigoPostal.ser";
//	static MultiMap<String, String[]> zipMultiMap =  new MultiValueMap<String, String[]>();
	
	static MultiMap<String, String[]> zeroZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> oneZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> twoZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> threeZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> fourZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> fiveZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> sixZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> sevenZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> eightZipMultiMap =  new MultiValueMap<String, String[]>();
	static MultiMap<String, String[]> nineZipMultiMap =  new MultiValueMap<String, String[]>();
	
	static Map<String,String> cityZipMap = new HashMap<>();
	
	static Map<String,List<String>> stateZipMap = new HashMap<>();
	
	private static boolean writeSerFile = false;
	
	/**
	 * 
	 * @param writeSerFileStatus
	 * :-<br> <b>True</b> : for writing serialized file.<br>
	 * <strong>False</strong> : for not writing serialized file.
	 * @throws IOException
	 */
	static void start(boolean writeSerFileStatus) throws IOException{
		writeSerFile = writeSerFileStatus;
		
		List<String[]> dataset = new ArrayList<>(); 
		
		String html = U.getHTML(MAIN_URL);
		
		String section = U.getSectionValue(html, "<tbody>", "</tbody>");
		String [] regSection = U.getValues(section, "<td>", "</td>");
		
		int i = 0;
		for(String sec : regSection){
//			if(i++ != 30)continue;
			
			String stateUrl = U.getSectionValue(sec, "<a href=\"/", "\"");
			String state = U.getSectionValue(sec, "addressRegion\">", "</span>");
//			U.log(i++ +"\t"+state);
			dataset.addAll(extractStateInfo(MAIN_URL+stateUrl, state));
		}
		U.log("Size of dataset ::"+dataset.size());
		
		/*
		 * Write serialize file here
		 */
		if(writeSerFile){
			U.writeSerializedFileWithString(cityZipMap, CITY_ZIP_SER_FILE);
			U.writeSerializedFileWithList(stateZipMap, STATE_ZIP_SER_FILE);
			
			//PostalCityExtraction.writeSerializedFileWithString(zipMultiMap, SER_FILE_NAME);
			PostalCityExtraction.writeSerializedFileWithString(zeroZipMultiMap, SER_FILE_NAME_0);
			PostalCityExtraction.writeSerializedFileWithString(oneZipMultiMap, SER_FILE_NAME_1);
			PostalCityExtraction.writeSerializedFileWithString(twoZipMultiMap, SER_FILE_NAME_2);
			PostalCityExtraction.writeSerializedFileWithString(threeZipMultiMap, SER_FILE_NAME_3);
			PostalCityExtraction.writeSerializedFileWithString(fourZipMultiMap, SER_FILE_NAME_4);
			PostalCityExtraction.writeSerializedFileWithString(fiveZipMultiMap, SER_FILE_NAME_5);
			PostalCityExtraction.writeSerializedFileWithString(sixZipMultiMap, SER_FILE_NAME_6);
			PostalCityExtraction.writeSerializedFileWithString(sevenZipMultiMap, SER_FILE_NAME_7);
			PostalCityExtraction.writeSerializedFileWithString(eightZipMultiMap, SER_FILE_NAME_8);
			PostalCityExtraction.writeSerializedFileWithString(nineZipMultiMap, SER_FILE_NAME_9);		
		}
		
		/*
		 * Write output file here
		 */
		writeFile(dataset);
	}
	
	static List<String[]> extractStateInfo(String url, String state) throws IOException{
		
		List<String[]> dataset = new ArrayList<>(); 
		
		U.log(url+"\t"+state);
		String html = U.getHTML(url);
		
		String section = U.getSectionValue(html, "<tbody>", "</tbody>");
		String []  municipalitiesSection = U.getValues(section, "<td>", "</td>");
		
		for(String sec : municipalitiesSection){
			String municipalityUrl = U.getSectionValue(sec, "<a href=\"/", "\"");
			String municipality = U.getSectionValue(sec, "addressLocality\">", "</span>");
			
			dataset.addAll(extractMunicipalitiesInfo(MAIN_URL+municipalityUrl, state, municipality));
//			U.log(municipalityUrl);
		}
		U.log("Municipalities count and settlement count ::"+dataset.size());		
		return dataset;
	}
	
	static List<String[]> extractMunicipalitiesInfo(String url, String state, String municipality) throws IOException{
		
		List<String[]> infoList = new ArrayList<>(); 
		
		U.log("MunicipalityUrl ::"+url);
		String html = U.getHTML(url);
		
		String section = U.getSectionValue(html, "<tbody>", "</tbody>");
		
		String []  municipalitiesSection = U.getValues(section, "<tr>", "</tr>");
		
		for(String sec : municipalitiesSection){
			if(sec.contains("<script>"))continue;
			infoList.add(extractInfo(sec, state));			
		}
		
		return infoList;
	}
	static int j = 0;
	static String [] extractInfo(String sec, String state) throws IOException{
		String[] info = {"","","","","","","","",""};
//		if(j == 0)
		{
//			U.log("Sec ::"+sec);
			
			
			String settlementUrl = U.getSectionValue(sec, "<a href = \"/", "\"");
			
//			U.log("settlementUrl ::"+MAIN_URL+settlementUrl);
			String[] latLng = extractLatLng(MAIN_URL+settlementUrl);
			
	//		String[] latLng = extractLatLng(MAIN_URL + U.getSectionValue(sec, "<a href = \"/", "\""));
			
			String[] vals = U.getValues(sec, "<td>", "</td>");
			
			for(int i = 0; i < vals.length; i++){
				if(i == 0) info[1] = U.getSectionValue(vals[i], ">", "</a>").trim(); //settlement
				if(i == 1) info[2] = vals[i].trim(); //Type Of Settlement
				if(i == 3) info[4] = U.getSectionValue(vals[i], "addressLocality\">", "</span>").trim(); //Municipality
				if(i == 4){
					info[5] = U.getSectionValue(vals[i], "addressLocality\">", "</span>").trim(); //City
					if(info[5].trim().length() == 1) info[5] = "";
				}
				if(i == 5)	info[6] = U.getSectionValue(vals[i], "ocultar\">", "</div>").trim(); //Zone
			}
			
			info[0] = U.matchState(state); //state
			info[3] = U.getSectionValue(sec, "postalCode\">", "</span>").trim(); //zip
			info[7] = latLng[0].trim(); //Latitude
			info[8] = latLng[1].trim(); //Longitude
			
			if(writeSerFile){
				
				if(!info[5].isEmpty()) cityZipMap.put(info[3], info[5]); //City
				else if(!info[4].isEmpty()) cityZipMap.put(info[3], info[4]); //Municipality
				
				if(stateZipMap.containsKey(state)){
					List<String> zipList = stateZipMap.get(state);
					zipList.add(info[3]);
					for(Entry<String, List<String>> entry : stateZipMap.entrySet()){
						if(state.equals(entry.getKey()))
							entry.setValue(zipList);
					}
				}else{
					List<String> zipList = new ArrayList<>();
					zipList.add(info[3]);
					stateZipMap.put(state, zipList);
				}
				
//				zipMultiMap.put(info[3], info);
				if(info[3].startsWith("0")) zeroZipMultiMap.put(info[3], info);
				if(info[3].startsWith("1")) oneZipMultiMap.put(info[3], info);
				if(info[3].startsWith("2")) twoZipMultiMap.put(info[3], info);
				if(info[3].startsWith("3")) threeZipMultiMap.put(info[3], info);
				if(info[3].startsWith("4")) fourZipMultiMap.put(info[3], info);
				if(info[3].startsWith("5")) fiveZipMultiMap.put(info[3], info);
				if(info[3].startsWith("6")) sixZipMultiMap.put(info[3], info);
				if(info[3].startsWith("7")) sevenZipMultiMap.put(info[3], info);
				if(info[3].startsWith("8")) eightZipMultiMap.put(info[3], info);
				if(info[3].startsWith("9")) nineZipMultiMap.put(info[3], info);
			}
		}
		j++;
		return info;
		
	}
	
	static String [] extractLatLng(String url) throws IOException{
		String[] latLng = {"",""};
		U.log("settlementUrl ::"+url);
		String html = U.getHTML(url);

		String section = U.getSectionValue(html, "google.maps.LatLng(", ")");
		if(section != null)
			latLng = section.split(",");
		if(section == null){
			latLng[0] = U.getSectionValue(html, "var lat=", ";");
			latLng[1] = U.getSectionValue(html, "var lon=", ";");
		}
		if(latLng[0] == null || latLng[1] == null)
			latLng[0] = latLng[1] = "";
		
		return latLng;
	}
	
	static void writeFile(List<String[]> readLines){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(MEXICO_ADDRESS_DIR_FILE_PATH),',');
			writer.writeNext(HEADER);
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing validate file.....Done");
	}
}
