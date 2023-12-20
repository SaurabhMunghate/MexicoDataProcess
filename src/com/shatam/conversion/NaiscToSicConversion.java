package com.shatam.conversion;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.opencsv.CSVReader;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;

public final class NaiscToSicConversion {

	private NaiscToSicConversion(){}
	
	private static final String FILE_PATH = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/SIC-to-NAICS-Crosswalk_Valid.csv";
	private static boolean status = false; 

	public static void main(String[] args) {
		NaiscToSicConversion.init();
//		NaiscToSicConversion.conversionNaiscToSic();
		compare("115113");
//		compare("524220");
	}

	public static void init(){
		load();
		status = true;
	}
	
	public static void compare(String naisc){
		U.log("\n***\t\tNaisc ::"+naisc);
		
		Naisc.init();
		String [] data = Naisc.getNaiseFourDesc(naisc);
		if(data != null){
			U.log("naisc four digit desc ::"+Arrays.toString(data));
		}else{
			U.errLog("naise four digit desc not find");
		}
		
		HashMap<String,String> naiscMap = getNaiscMap();
		if(naiscMap.containsKey(naisc)){
			U.log("naisc Six digit desc ::"+naiscMap.get(naisc));
		}
		HashMap<String,String> sicMap = getSicMap();
		
		Collection<String> collectionSic = getSicForNaisc(naisc);
		if(collectionSic != null && collectionSic.size() > 1){
			U.log("Sic list::"+collectionSic);
			for(String sic : collectionSic){
				U.log("Sic ::"+sic);
				
				
				if(sicMap.containsKey(sic)){
					U.log("sic desc ::"+sicMap.get(sic));
				}
			}
		}else if(collectionSic != null && collectionSic.size() == 1){
			U.errLog("Exact match here....");
			U.errLog("Sic list::"+collectionSic);
			for(String sic : collectionSic){
				U.errLog("Sic ::"+sic);
				
				
				if(sicMap.containsKey(sic)){
					U.errLog("sic desc ::"+sicMap.get(sic));
				}
			}
		}
		
		
	}
	
	public static void conversionNaiscToSic(){
		
		HashMap<String,String> sicMap = getSicMap();
		HashMap<String,String> naiscMap = getNaiscMap();
		MultiMap<String, String> naiscMultiMap = getNaiscMultiMap();
		
		
		Set<String> keys = naiscMultiMap.keySet();
		
		for(String naisc : keys){
			U.log(naisc+"\t"+naiscMultiMap.get(naisc));
		}		
	}
	
	
	public static Collection<String> getSicForNaisc(String naisc){
		MultiMap<String, String> naiscMultiMap = getNaiscMultiMap();
		Set<String> keys = naiscMultiMap.keySet();
		if(keys.contains(naisc)){
			return new TreeSet<>(naiscMultiMap.get(naisc));
		}
		return null;		
	}
	
	
	
	
	private static List<String[]>  readLines = null;
	private static void load(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_PATH));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getSicMap(){
		call();
		
		HashMap<String,String> sicMap = new HashMap<>();
		
		String key = null;
		for(String[] vals : readLines){
			key = vals[0];
			if(key.trim().length() == 3) key = "0"+key;
			sicMap.put(key, vals[1].trim());
		}
		return sicMap;
	}
	
	public static HashMap<String,String> getNaiscMap(){
		call();
		HashMap<String,String> naiscMap = new HashMap<>();
		for(String[] vals : readLines){
			naiscMap.put(vals[2].trim(), vals[3].trim());
		}
		return naiscMap;
	}
	
	
	public static MultiMap<String, String> getNaiscMultiMap(){
		call();
		MultiMap<String, String> naiscMultiMap = new MultiValueMap<>();
		
		String sic = null;
		
		for(String[] vals : readLines){
			sic = vals[0];
			if(sic.trim().length() == 3) sic = "0"+sic;
			naiscMultiMap.put(vals[2].trim(), sic);
		}		
		return naiscMultiMap;
	}
	
	private static void call(){
		if(!status)
			try {
				throw new IllegalStateException("Cann't load this method, init() is not initialised yet ...");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
