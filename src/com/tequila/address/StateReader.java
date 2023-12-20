package com.tequila.address;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class StateReader {

	
	
	public static void main(String[] args) {
		
/*		String fileName = "/home/glady/MexicoCache/source/Mexico_Address_Direcory_MiCodigo.csv";
		createStateCitiesMap(fileName);
*/
		
		Set<String> cities = getAllCities("Veracruz");
		U.log("City Count ::"+cities.size());
		for (String city : cities) {
			U.log(city);
		}
	}

	@SuppressWarnings("unchecked")
	static Map<String,Set<String>> stateWithCityList = (Map<String, Set<String>>) U.deserialized(Path.RESOURCES_SER+"MiCodigoStateCities.ser");
	
	public static final Set<String> getAllCities(String state){
		Set<String> keys = stateWithCityList.keySet();
		
/*		for(Entry<String, Set<String>> entry : stateWithCityList.entrySet()){
			U.log(entry.getKey()+"\t\t"+entry.getValue());
		}
*/		
//		U.log(keys.size());
		if(!keys.contains(state))return null;
		else{	
			return (Set<String>) stateWithCityList.get(state);
		}
	}
	
	
	private static void createStateCitiesMap(String fileName){
		Map<String,Set<String>> citiesMap = new HashMap<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for(String[] lines :  readLines){
			if(!citiesMap.containsKey(lines[0])){
				Set<String> cities = new HashSet<>();

				if(!lines[5].trim().isEmpty()){
					if(lines[5].trim().contains("Centro")){
						cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[4].replaceAll("\\(.*?\\)", "").trim())));
						continue;
					}
					cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[5].replaceAll("\\(.*?\\)", "").trim())));					
				}
				if(!lines[4].isEmpty()){
					cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[4].replaceAll("\\(.*?\\)", "").trim())));
				}
				
				citiesMap.put(lines[0], cities);
			}else{
				Set<String> cities = citiesMap.get(lines[0]);
				if(!lines[5].trim().isEmpty()){
					if(lines[5].trim().contains("Centro")){
						cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[4].replaceAll("\\(.*?\\)", "").trim())));
						continue;
					}
					cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[5].replaceAll("\\(.*?\\)", "").trim())));					
				}
				if(!lines[4].isEmpty()){
					cities.add(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[4].replaceAll("\\(.*?\\)", "").trim())));
				}
				for(Entry<String, Set<String>> entry : citiesMap.entrySet()){
					if(entry.getKey().equals(lines[0].trim()))
						entry.setValue(cities);
				}
			}
		}
		
		int count = 0;
		for(Entry<String, Set<String>> entry : citiesMap.entrySet()){
			U.log(entry.getKey()+"\t\tSize :"+entry.getValue());
			count+= entry.getValue().size();
		}
		U.log("count::"+count);
		
		U.writeSerializedFile(citiesMap, Path.ADDRESS_SOURCE_DIR+"MiCodigoStateCities.ser");
	}
	
}
