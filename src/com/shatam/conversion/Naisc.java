package com.shatam.conversion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;

public class Naisc {
	
	private Naisc(){}

	private final static String FILE_SIX_DIGIT = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/estructura_6_digit.csv";
	private final static String FILE_FOUR_DIGIT = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/estructura_4_digit.csv";
	
	private final static String FILE_FOUR_DIGIT_ADDITIONAL = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/estructura_4_digit_additional_Valid.csv";
	
	private static boolean status = false; 
	public static void main(String[] args) {
		Naisc.init();
//		validateCsv("/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/estructura_4_digit_additional.csv");
		String [] data = getNaiseFourDesc("437112");
		if(data != null){
			U.log(Arrays.toString(data));
		}
	}
	
	public static void init(){
		loadFourDigit();
		loadSixDigit();
		loadFourDigitAdditional();
		status = true;
	}
	
//	private static HashMap<String, String[]> fourDigitMap = getFourDigitMapAll();	
//	private static Set<String> keySet = fourDigitMap.keySet(); 
	
	public static String[] getNaiseFourDesc(String naiscSix){
		String key = naiscSix.substring(0, 4).trim();
		HashMap<String, String[]> fourDigitMap = getFourDigitMapAll();	
		Set<String> keySet = fourDigitMap.keySet(); 
		
		if(keySet.contains(key)){
			return fourDigitMap.get(key);
		}
		
		return null;
	}
	
	static MultiMap<String, String> getNaiscMultiMap(){
		MultiMap<String, String> multiMap =  new MultiValueMap<>();
		
		HashMap<String, String[]> sixDigitMap = getSixDigitMapAll();
		for(Entry<String, String[]> entry : sixDigitMap.entrySet()){
			multiMap.put(entry.getKey().substring(0, 4), entry.getKey());
		}
		return multiMap;
	}
	
	private static List<String[]>  readLinesFromSix = null;	
	private static void loadSixDigit(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_SIX_DIGIT));
			readLinesFromSix = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<String[]>  readLinesFromFour = null;
	private static void loadFourDigit(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_FOUR_DIGIT));
			readLinesFromFour = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void loadFourDigitAdditional(){
		List<String[]>  readLines = null;
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_FOUR_DIGIT_ADDITIONAL));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		readLinesFromFour.addAll(readLines);
	}
	
	public static HashMap<String,String[]> getSixDigitMapAll(){
		call();
		
		HashMap<String,String[]> naiscMap = new HashMap<>();
		for(String[] vals : readLinesFromSix){

			naiscMap.put(vals[0].trim(), new String[]{vals[1].trim(),vals[2].trim()});
		}
		return naiscMap;
	}
	
	public static HashMap<String,String> getSixDigitMapEnglish(){
		call();
		
		HashMap<String,String> naiscMap = new HashMap<>();
		
		for(String[] vals : readLinesFromSix){

			naiscMap.put(vals[0].trim(), vals[2].trim());
		}
		return naiscMap;
	}
	
	public static HashMap<String,String> getSixDigitMapSpanish(){
		call();
		
		HashMap<String,String> naiscMap = new HashMap<>();	
		for(String[] vals : readLinesFromSix){

			naiscMap.put(vals[0].trim(), vals[1].trim());
		}
		return naiscMap;
	}
	
	
	public static HashMap<String,String[]> getFourDigitMapAll(){
		call();
		
		HashMap<String,String[]> naiscMap = new HashMap<>();
		
		for(String[] vals : readLinesFromFour){
			naiscMap.put(vals[0].trim(), new String[]{vals[1].trim(),vals[2].trim()});
		}
		return naiscMap;
	}
	
	public static HashMap<String,String> getFourDigitMapEnglish(){
		call();
		
		HashMap<String,String> naiscMap = new HashMap<>();
		
		for(String[] vals : readLinesFromFour){

			naiscMap.put(vals[0].trim(), vals[2].trim());
		}
		return naiscMap;
	}
	
	public static HashMap<String,String> getFourDigitMapSpanish(){
		call();
		
		HashMap<String,String> naiscMap = new HashMap<>();
		
		for(String[] vals : readLinesFromFour){

			naiscMap.put(vals[0].trim(), vals[1].trim());
		}
		return naiscMap;
	}
	
	private static void call(){
		if(!status)
			try {
				throw new IllegalStateException("Cann't load this method, init() is not initialised yet ...");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private static void validateCsv(String fileName){
		List<String[]>  readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName));
			CSVWriter writer= new CSVWriter(new FileWriter(fileName.replace(".csv", "_Valid.csv")),',');){
			readLines = reader.readAll();
			
			String[] lines = null;
			Iterator<String[]> it = readLines.iterator();
			while (it.hasNext()) {
				lines= it.next();
				for(int i = 0; i< lines.length; i++){
					lines[i] = U.toTitleCase(lines[i]).trim();
				}
			}
			writer.writeAll(readLines);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
