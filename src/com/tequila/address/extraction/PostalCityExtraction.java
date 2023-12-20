package com.tequila.address.extraction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class PostalCityExtraction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3332702649978319617L;

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		new PostalCityExtraction().start();
	}
	
	private static final String MAIN_URL = "https://worldpostalcode.com/mexico/";
	
	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/";
	
	MultiMap<String, String> stateCitiesMap =  new MultiValueMap<String, String>();
	MultiMap<String, String> zipCitiesMap =  new MultiValueMap<String, String>();
	MultiMap<String, String> zipStatesMap =  new MultiValueMap<String, String>();
	MultiMap<String, String> citiesStateMap =  new MultiValueMap<String, String>();
	MultiMap<String, String[]> zipStateCityMap =  new MultiValueMap<String, String[]>();
	
	
	void start() throws IOException{
		
		String html = U.getHTML(MAIN_URL);
		
		String regionSection = U.getSectionValue(html, "<div class=\"codelist\">", "<div class=\"clear\">");
		String regUrls[] = U.getValues(regionSection, "<a href=\"/", "\"");
		for(String regUrl : regUrls){
			String state = U.matchState(regUrl.replace("mexico/", ""));
			U.log(regUrl);
			U.log(state);
			String regHtml = U.getHTML(MAIN_URL+regUrl);
			
			U.log(U.getCache(MAIN_URL+regUrl));
			
//			if(state.equals("Chihuahua"))
				findCitiesAndPostal(regHtml, state);
			
		}
		
		writeSerializedFile(stateCitiesMap, "State_Cities.ser");
		writeSerializedFile(citiesStateMap, "Cities_State.ser");
		writeSerializedFile(zipStatesMap, "Zip_State.ser");
		writeSerializedFile(zipCitiesMap, "Zip_Cities.ser");
		writeSerializedFileWithString(zipStateCityMap, "Zip_City_State.ser");
	}
	

	void findCitiesAndPostal(String html, String state){
		
		String section = U.getSectionValue(html, "<div class=\"codelist", "<h2>");
//		U.log(section);
		String[] citiesZips = U.getValues(section, "<div class=\"leftc\"", "</div></div>");
		
		for(String sec : citiesZips){
//			U.log(sec);
			String cityName = U.getSectionValue(sec, ">", "</div").trim();
//			String zipSec = U.getSectionValue(sec, ,To);	
			ArrayList<String> zipList = Util.matchAll(sec.replace(cityName, ""), "\\d{5}", 0);
			
	//		U.log(cityName+"\t\t"+zipList);
			
			stateCitiesMap.put(state.trim(), cityName.trim());
			citiesStateMap.put(cityName.trim(), state.trim());
			
			for(String zip : zipList){
				zipStatesMap.put(zip.trim(), state.trim());
				zipCitiesMap.put(zip.trim(), cityName.trim());
				zipStateCityMap.put(zip.trim(), new String[]{cityName.trim(),state.trim()});
			}
				
		}
		
//		U.log(state);
				
	}

	private void writeSerializedFile(MultiMap<String, String> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(SOURCE_DIR+fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);		
	}
	
	public static void writeSerializedFileWithString(MultiMap<String, String[]> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(SOURCE_DIR+fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}

}
