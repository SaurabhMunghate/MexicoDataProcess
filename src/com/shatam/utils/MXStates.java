/**
 * @author Sawan
 */
package com.shatam.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public class MXStates {
	

	private static MultiMap multiMap =  new MultiValueMap();
	
    static{
		multiMap.put("Aguascalientes", "AG");
		multiMap.put("Aguascalientes", "Ags");
		multiMap.put("Aguascalientes", "AGS");
		multiMap.put("Aguascalientes", "AGU");
		multiMap.put("Baja California","B.C.");
		multiMap.put("Baja California","BC");
		multiMap.put("Baja California","BCN");
		multiMap.put("Baja California Sur","B.C.S.");
		multiMap.put("Baja California Sur","BS");
		multiMap.put("Baja California Sur","BCS");
		multiMap.put("Campeche","Camp.");
		multiMap.put("Campeche","Camp");
		multiMap.put("Campeche","CM");
		multiMap.put("Campeche","CAM");
		multiMap.put("Chiapas","Chis.");
		multiMap.put("Chiapas","Chis");
		multiMap.put("Chiapas","CS");
		multiMap.put("Chiapas","CHP");
		multiMap.put("Chihuahua","Chih.");
		multiMap.put("Chihuahua","Chih");
		multiMap.put("Chihuahua","CH");
		multiMap.put("Chihuahua","CHH");
		multiMap.put("Coahuila","Coah.");
		multiMap.put("Coahuila","Coah");
		multiMap.put("Coahuila","CO");
		multiMap.put("Coahuila","COA");
		multiMap.put("Colima","Col.");
		multiMap.put("Colima","Col");
		multiMap.put("Colima","CL");
		multiMap.put("Colima","COL");
		multiMap.put("Mexico City","CDMX");
		multiMap.put("Mexico City","DF");
		multiMap.put("Mexico City","CMX");
		multiMap.put("Mexico City","DIF");
		multiMap.put("Durango","Dgo.");
		multiMap.put("Durango","DG");
		multiMap.put("Durango","DUR");
		multiMap.put("Guanajuato","Gto.");
		multiMap.put("Guanajuato","Gto");
		multiMap.put("Guanajuato","GT");
		multiMap.put("Guanajuato","GUA");
		multiMap.put("Guanajuato","GTO");
		multiMap.put("Guerrero","Gro.");
		multiMap.put("Guerrero","Gro");
		multiMap.put("Guerrero","GR");
		multiMap.put("Guerrero","GRO");
		multiMap.put("Hidalgo","Hgo.");
		multiMap.put("Hidalgo","Hgo");
		multiMap.put("Hidalgo","HG");
		multiMap.put("Hidalgo","HID");
		multiMap.put("Jalisco","Jal.");
		multiMap.put("Jalisco","JA");
		multiMap.put("Jalisco","JAL");
		multiMap.put("Mexico State","Mex.");
		multiMap.put("Mexico State","Méx.");
		multiMap.put("Mexico State","Mex");
		multiMap.put("Mexico State","Edomex.");
		multiMap.put("Mexico State","EM");
		multiMap.put("Mexico State","MEX");
		multiMap.put("Michoacan","Mich.");
		multiMap.put("Michoacan","MI");
		multiMap.put("Michoacan","MIC");
		multiMap.put("Morelos","Mor.");
		multiMap.put("Morelos","MO");
		multiMap.put("Morelos","MOR");
		multiMap.put("Nayarit","Nay.");
		multiMap.put("Nayarit","NA");
		multiMap.put("Nayarit","NAY");
		multiMap.put("Nuevo Leon","N.L.");
		multiMap.put("Nuevo Leon","NL");
		multiMap.put("Nuevo Leon","NLE");
		multiMap.put("Oaxaca","Oax.");
		multiMap.put("Oaxaca","OA");
		multiMap.put("Oaxaca","OAX");
		multiMap.put("Puebla","Pue.");
		multiMap.put("Puebla","PU");
		multiMap.put("Puebla","PUE");
		multiMap.put("Queretaro","Qro.");
		multiMap.put("Queretaro","QT");
		multiMap.put("Queretaro","QUE");
		multiMap.put("Quintana Roo","Q.R.");
		multiMap.put("Quintana Roo","Q. Roo.");
		multiMap.put("Quintana Roo","QR");
		multiMap.put("Quintana Roo","ROO");
		multiMap.put("San Luis Potosi","S.L.P.");
		multiMap.put("San Luis Potosi","SL");
		multiMap.put("San Luis Potosi","SLP");
		multiMap.put("Sinaloa","Sin.");
		multiMap.put("Sinaloa","SI");
		multiMap.put("Sinaloa","SIN");
		multiMap.put("Sonora","Son.");
		multiMap.put("Sonora","SO");
		multiMap.put("Sonora","SON");
		multiMap.put("Tabasco","Tab.");
		multiMap.put("Tabasco","TB");
		multiMap.put("Tabasco","TAB");
		multiMap.put("Tamaulipas","Tamps.");
		multiMap.put("Tamaulipas","TM");
		multiMap.put("Tamaulipas","TAM");
		multiMap.put("Tlaxcala","Tlax.");
		multiMap.put("Tlaxcala","TL");
		multiMap.put("Tlaxcala","TLA");
		multiMap.put("Veracruz","Ver.");
		multiMap.put("Veracruz","VE");
		multiMap.put("Veracruz","VER");
		multiMap.put("Yucatan","Yuc.");
		multiMap.put("Yucatan","YU");
		multiMap.put("Yucatan","YUC");
		multiMap.put("Zacatecas","Zac.");
		multiMap.put("Zacatecas","ZA");
		multiMap.put("Zacatecas","ZAC");
    };
    
	public static void main(String[] args) throws Exception {
		U.log(getFullNameFromAbbr("ZA"));
	}
	
	public static String getFullNameFromAbbr(String abbr){

		Set<String> keys = multiMap.keySet();
//		U.log(multiMap.size());
		// iterate over the map
		for(String key : keys){
			List<String> values = (List<String>) multiMap.get(key);
			if(values.contains(abbr)){
				return key;
			}
		}
		return null;
	}
 
	@SuppressWarnings("unchecked")
	private static Map<String,String> mexicoMunicipalites = new HashMap(){
		{
			put("01","Álvaro Obregón");
			put("02","Azcapotzalco");
			put("03","Benito Juárez");
			put("04","Coyoacán");
			put("05","Cuajimalpa De Morelos");
			put("06","Cuauhtémoc");
			put("07","Gustavo A. Madero");
			put("08","Iztacalco");
			put("09","Iztapalapa");
			put("10","La Magdalena Contreras");
			put("11","Miguel Hidalgo");
			put("12","Milpa Alta");
			put("13","Tláhuac");
			put("14","Tlalpan");
			put("15","Venustiano Carranza");
			put("16","Xochimilco");
		}
	};
	
	public static String getMexicoCityMunicipalites(String zip){
		if(zip.trim().length() == 4) zip = "0"+zip;
		if (zip.trim().length()<2) {
			return ""; 
		}
		return mexicoMunicipalites.get(zip.substring(0, 2));
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
	public static HashSet<String> getAllStates(){
		HashSet<String> allStates=new HashSet<>();
		for (Object key : multiMap.keySet()) {
			allStates.add(key.toString());
		}
		U.log(allStates.size());
		return allStates;
	}
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
	
	public static HashMap<String, Integer[]> mexicoPostalCodeRange =  new HashMap<String, Integer[]>(){
		{
			put("Mexico City", new Integer[]{0,16999}); //Metropolitan
			put("Jalisco",new Integer[]{44000,49000});//Metropolitan
			put("Mexico State", new Integer[]{50000,57999});//Metropolitan			
			put("Nuevo Leon",new Integer[]{64000,67999});//Metropolitan
			put("Aguascalientes",new Integer[]{20000,20999});
			put("Baja California",new Integer[]{21000,22999});
			put("Baja California Sur",new Integer[]{23000,23999});
			put("Campeche", new Integer[]{24000,24999});
			put("Coahuila",new Integer[]{25000,27999});
			put("Colima",new Integer[]{28000,28999});
			put("Chiapas",new Integer[]{29000,30999});			
			put("Chihuahua",new Integer[]{31000,33999});
			put("Durango",new Integer[]{34000,35999});
			put("Guanajuato",new Integer[]{36000,38999});
			put("Guerrero",new Integer[]{39000,41999});	
			put("Hidalgo",new Integer[]{42000,43999});
			put("Michoacan",new Integer[]{58000,61999});
			put("Morelos",new Integer[]{62000,62999});
			put("Nayarit",new Integer[]{63000,63999});			
			put("Oaxaca",new Integer[]{68000,71999});
			put("Puebla",new Integer[]{72000,75999});
			put("Queretaro",new Integer[]{76000,76999});			
			put("Quintana Roo",new Integer[]{77000,77999});
			put("San Luis Potosi",new Integer[]{78000,79999});
			put("Sinaloa",new Integer[]{80000,82999});
			put("Sonora",new Integer[]{83000,85999});
			put("Tabasco",new Integer[]{86000,86999});
			put("Tamaulipas",new Integer[]{87000,89999});			
			put("Tlaxcala", new Integer[]{90000,90999});
			put("Veracruz",new Integer[]{91000,96999});
			put("Yucatan",new Integer[]{97000,97999});
			put("Zacatecas",new Integer[]{98000,99999});
		}
	};
	
	@SuppressWarnings("unchecked")
	private final static Map<String, String> enSpStateFormMap = new HashMap(){
		{
			put("Aguascalientes", 		"Aguascalientes");
			put("Baja California", 		"Baja California");
			put("Baja California Sur", 	"Baja California Sur");
			put("Campeche", 			"Campeche");
			put("Chiapas", 				"Chiapas");
			put("Chihuahua", 			"Chihuahua");
			put("Coahuila", 			"Coahuila");
			put("Colima", 				"Colima");
			put("Durango", 				"Durango");
			put("Guanajuato", 			"Guanajuato");
			put("Guerrero", 			"Guerrero");
			put("Hidalgo", 				"Hidalgo");
			put("Jalisco", 				"Jalisco");
//			put("Mexico City", 			"Ciudad De México");
			put("Mexico City", 			"Mexico City");
			put("Mexico State", 		"Mexico State");
//			put("Mexico State", 		"Estado De México");
			put("Michoacan", 			"Michoacán");
			put("Morelos", 				"Morelos");
			put("Nayarit", 				"Nayarit");
			put("Nuevo Leon", 			"Nuevo León");
			put("Oaxaca", 				"Oaxaca");
			put("Puebla", 				"Puebla");
			put("Queretaro", 			"Querétaro");
			put("Quintana Roo", 		"Quintana Roo");
			put("San Luis Potosi", 		"San Luis Potosí");
			put("Sinaloa", 				"Sinaloa");
			put("Sonora", 				"Sonora");
			put("Tabasco", 				"Tabasco");
			put("Tamaulipas", 			"Tamaulipas");
			put("Tlaxcala", 			"Tlaxcala");
			put("Veracruz", 			"Veracruz");
			put("Yucatan", 				"Yucatán");
			put("Zacatecas", 			"Zacatecas");
		}
	};
	
	
	public static String getStateSpanish(String state){
		state = U.matchState(state);
		if(state.equals("-"))return null;
		return enSpStateFormMap.get(state.trim());
	}
}	