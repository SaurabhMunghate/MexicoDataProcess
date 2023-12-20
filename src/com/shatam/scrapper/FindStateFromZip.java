package com.shatam.scrapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class FindStateFromZip {

	private static String inputFile = "/home/glady/Downloads/MexicoProject/Priyanka/Tiendeo-RestaurantData_Split_1_Address_File.csv";
	
	public static void main(String[] args) {
		addState(11, 12);
	}

	static void addState(int stateIndex, int zipIndex){
		List<String[]>  readLines  = U.readCsvFile(inputFile);
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			if(lines[zipIndex].trim().length() == 4){
				lines[zipIndex] = "0"+lines[zipIndex].trim();
			}
				
			if(!lines[zipIndex].trim().isEmpty())
				lines[stateIndex] = findStateFromZip(lines[zipIndex]);
		}
		U.writeCsvFile(readLines, inputFile.replace(".csv", "_Add_State.csv"));
	}
	
	private static Map<String, String> validator = new HashMap<String,String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("Mexico City",	"00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16");
			put("Aguascalientes",	"20");
			put("Baja California",	"21,22");
			put("Baja California Sur",	"23");
			put("Campeche",	"24");
			put("Chiapas",	"29,30");
			put("Chihuahua",	"31,32,33");
			put("Coahuila",	"25,26,27");
			put("Colima",	"28");
			put("Durango",	"34,35");
			put("Guanajuato",	"36,37,38");
			put("Guerrero",	"39,40,41");
			put("Hidalgo","42,43");
			put("Jalisco","44,45,46,47,48,49");
			put("Mexico State",	"50,51,52,53,54,55,56,57");
			put("Michoacan","58,59,60,61");
			put("Morelos","62");
			put("Nayarit","63");
			put("Nuevo Leon","64,65,66,67");
			put("Oaxaca","68,69,70,71");
			put("Puebla","72,73,74,75");
			put("Queretaro","76");
			put("Quintana Roo","77");
			put("San Luis Potosi","78,79");
			put("Sinaloa","80,81,82");
			put("Sonora","83,84,85");
			put("Tabasco","86");
			put("Tamaulipas","87,88,89");
			put("Tlaxcala","90");
			put("Veracruz","91,92,93,94,95,96");
			put("Yucatan","97");
			put("Zacatecas","98,99");
		}
	};
	

	
	public static String findStateFromZip(String zip){
		
		if(!zip.matches("\\d{4,5}")){
			return null; 
		}
		if(zip.trim().length() == 4)
			zip = "0"+zip;
		
		for(Entry<String, String> it : validator.entrySet()){
/*			String[] zipList = it.getValue().split(",");
		
			for(String val : zipList){
				if(zip.startsWith(val)){
					return it.getKey().toString();
				}
			}	*/		
		
			if(it.getValue().contains(zip.substring(0, 2))){
				return it.getKey().toString();
			}
		}		
		return null;
	}

}
