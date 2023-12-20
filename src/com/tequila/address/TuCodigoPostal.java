package com.tequila.address;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shatam.collection.MultiMap;

public class TuCodigoPostal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private static final String SOURCE_DIR = "/home/glady/MexicoCache/source/Mexico/";
	
	private static MultiMap<String, String[]> zipMultiMap = (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"Tu_Codigo_Address_Direcory.ser");
	
	private static Map<String,String> cityZipMap = (Map<String, String>) MexicoAddress.deserialized(SOURCE_DIR+"Tu_Codigo_City_Zip.ser");
	
	public static List<String[]> getZipInfo(String zip){
		if(zip.length() < 5)throw new ArrayIndexOutOfBoundsException("Invalid zip");
		zip = zip.trim();
		Set<String> keys = zipMultiMap.keySet();
		if(!keys.contains(zip))return null;
		else{	
			return (List<String[]>) zipMultiMap.get(zip);
		}
	}
	
	public static String getCity(String zip){
		if(zip.length() == 4) zip = "0"+zip;
		if(cityZipMap.containsKey(zip)){
			return cityZipMap.get(zip);
		}
		return null;
	}
}
