package com.tequila.address;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

import com.shatam.collection.MultiMap;
import com.shatam.utils.U;

public class MexicoAddress {

	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/Address/";
	
//	private static MultiMap<String, String> stateCitiesMap =  (MultiMap<String, String>) deserialized(SOURCE_DIR+"State_Cities.ser");
//	private static MultiMap<String, String> zipCitiesMap =  (MultiMap<String, String>) deserialized(SOURCE_DIR+"Zip_Cities.ser");
//	private static MultiMap<String, String> zipStatesMap =  (MultiMap<String, String>) deserialized(SOURCE_DIR+"Zip_State.ser");
//	private static MultiMap<String, String> citiesStateMap =  (MultiMap<String, String>) deserialized(SOURCE_DIR+"Cities_State.ser");
//	private static MultiMap<String, String[]> zipStateCityMap =  (MultiMap<String, String[]>) deserialized(SOURCE_DIR+"Zip_City_State.ser");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		
//		U.log(stateCitiesMap.size());
		//Baja California	22800

		U.log(getZipCities("22800"));
	}

	public static String getZipCities(String zip){
		Set<String> keys = zipCitiesMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		if(keys.contains(zip)){
			List<String> values = (List<String>) zipCitiesMap.get(zip);
			U.log(values);
		}
		return null;
	}
	
	public static String getZipState(String zip){
		Set<String> keys = zipStatesMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		for(String key : keys){
			List<String> values = (List<String>) zipStatesMap.get(key);
			if(values.contains(zip)){
				return key;
			}
		}
		return null;
	}
	
	public static String getCityState(String city){
		Set<String> keys = citiesStateMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		for(String key : keys){
			List<String> values = (List<String>) citiesStateMap.get(key);
			if(values.contains(city)){
				return key;
			}
		}
		return null;
	}
	
	public static String getZipCityState(String zip){
		Set<String> keys = zipStateCityMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		for(String key : keys){
			List<String[]> values = (List<String[]>) zipStateCityMap.get(key);
			if(values.contains(zip)){
				return key;
			}
		}
		return null;
	}
	
	public static String getStateCity(String state){
		Set<String> keys = stateCitiesMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		for(String key : keys){
			List<String> values = (List<String>) stateCitiesMap.get(key);
			if(values.contains(state)){
				return key;
			}
		}
		return null;
	}
	
	public static Object deserialized(String path){
		Object obj = null;
		try{
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return obj;
	}
}
