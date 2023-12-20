package com.tequila.address;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shatam.collection.MultiMap;
import com.shatam.utils.U;

public class MiCodigoPostal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		U.log(getCity("06050"));
		List<String[]> result =  getZipInfo("76179");
		for(String[] lines : result)
			U.log(Arrays.toString(lines));
//		 getCity(String zip)
	}
	
//	private static final String SOURCE_DIR = "/home/chinmay/eclipse-workspace/MexicoDataProcess/resources/MexicoAddress/";
	private static final String SOURCE_DIR = "/home/shatam-100/CODE_Repository/Maxico/MexicoDataProcess/resources/MexicoAddress/";

/*	@SuppressWarnings("unchecked")
	private static MultiMap<String, String[]> zipStateCityMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"MiCodigoPostal.ser");
*/	
	private static MultiMap<String, String[]> zeroZipMultiMap =  (MultiMap<String, String[]>) U.deserialized(SOURCE_DIR+"0_Address_Direcory.ser");
	private static MultiMap<String, String[]> oneZipMultiMap =  (MultiMap<String, String[]>) U.deserialized(SOURCE_DIR+"1_Address_Direcory.ser");
	private static MultiMap<String, String[]> twoZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"2_Address_Direcory.ser");
	private static MultiMap<String, String[]> threeZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"3_Address_Direcory.ser");
	private static MultiMap<String, String[]> fourZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"4_Address_Direcory.ser");
	private static MultiMap<String, String[]> fiveZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"5_Address_Direcory.ser");
	private static MultiMap<String, String[]> sixZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"6_Address_Direcory.ser");
	private static MultiMap<String, String[]> sevenZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"7_Address_Direcory.ser");
	private static MultiMap<String, String[]> eightZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"8_Address_Direcory.ser");
	private static MultiMap<String, String[]> nineZipMultiMap =  (MultiMap<String, String[]>) MexicoAddress.deserialized(SOURCE_DIR+"9_Address_Direcory.ser");
	
	private static Map<String,String> cityZipMap = (Map<String, String>) MexicoAddress.deserialized(SOURCE_DIR+"cityZip.ser");
	
//	private static Map<String,List<String>> stateZipMap = (Map<String, List<String>>) MexicoAddress.deserialized(SOURCE_DIR+"stateZip.ser");
/*	public static List<String[]> getZipInfo(String zip){
		Set<String> keys = zipStateCityMap.keySet();
		if(!keys.contains(zip))return null;
		else{
			List<String[]> values = (List<String[]>) zipStateCityMap.get(zip);
			if(values.size() == 1)
				return values.get(0);
			else{
				throw new ArrayIndexOutOfBoundsException("zip got more values, total values is "+values.size());
			}
			
			return (List<String[]>) zipStateCityMap.get(zip);
		}
	}
	
*/	
	public static List<String[]> getZipInfo(String zip){
		if(zip.length() < 5)throw new ArrayIndexOutOfBoundsException("Invalid zip");
		zip = zip.trim();
		if(zip.startsWith("0")) return getZip(zip,zeroZipMultiMap);
		else if(zip.startsWith("1")) return getZip(zip,oneZipMultiMap);
		else if(zip.startsWith("2")) return getZip(zip,twoZipMultiMap);
		else if(zip.startsWith("3")) return getZip(zip,threeZipMultiMap);
		else if(zip.startsWith("4")) return getZip(zip,fourZipMultiMap);
		else if(zip.startsWith("5")) return getZip(zip,fiveZipMultiMap );
		else if(zip.startsWith("6")) return getZip(zip,sixZipMultiMap);
		else if(zip.startsWith("7")) return getZip(zip,sevenZipMultiMap);
		else if(zip.startsWith("8")) return getZip(zip,eightZipMultiMap);
		else if(zip.startsWith("9")) return getZip(zip,nineZipMultiMap);
		
		return null;
	}
	
	private static List<String[]> getZip(String zip, MultiMap<String, String[]> multiMap){
		Set<String> keys = multiMap.keySet();
		if(!keys.contains(zip))return null;
		else{	
			return (List<String[]>) multiMap.get(zip);
		}
	}

	public static String getCity(String zip){
		if(zip.length() == 4) zip = "0"+zip;
		if(cityZipMap.containsKey(zip)){
			return cityZipMap.get(zip);
		}
		return null;
	}
	
/*	public static boolean isStateZip(String zip){
		return stateZipMap.get(zip).contains(zip);
	}
*/	
}
