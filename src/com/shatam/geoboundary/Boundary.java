package com.shatam.geoboundary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.shatam.utils.MexicoStateList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public final class Boundary {
	private Boundary(){
	}

/*	public static void main(String[] args) {
		//U.log(boundaryCheckForState("Queretaro","20.5717583",	"-100.3866531","Mexico"));
		String country = "Mexico";
		
		loadCoordincateMap (country);
		for(String state : MexicoStateList.STATES){
			double[] value = BoundaryCoordincateMap.get(state+","+country);
			U.log(state+","+country);
			U.log(Arrays.toString(BoundaryCoordincateMap.get(state+","+country)));
		}
	}*/
	
	final static String BaseDirectory = Path.CACHE_PATH + "GeoFiles" + File.separator;
	
	private static Map<String, double[]> BoundaryCoordincateMap = null;
	
	private static void loadCoordincateMap(String country){
		BoundaryCoordincateMap = null;
		if (BoundaryCoordincateMap == null) {
			BoundaryCoordincateMap = new HashMap<>();
			
			File [] files  = new File(BaseDirectory +country+ File.separator).listFiles();
			
			for (File file : files) {
				
				String content = null;
				try {
					content = FileUtil.readAllText(file.getPath());
				} catch (IOException e) {					
					e.printStackTrace();
				}
				
				
				String key = Util.match (content, "\"formatted_address\"\\s:\\s\"(.*?)\",", 1).trim ();
				key = key.replace ("Heroica Veracruz, Ver.", "Veracruz").replace ("Québec City,QC,", "Quebec,")
						.replace("State of Mexico", "Mexico State").replace("Mexico City, CDMX,", "Mexico City,")
						.replace(", ",",").replace("\"","");
				
				key = removeSpecialChars(key);
				
				String value = Util.match (content,"\"bounds\" : \\{(.+?),\\s+\"location\"",1,Pattern.DOTALL);
				String[] dirs = value.split ("},");
				
				String northeastLat = Util.match (dirs [0], "lat\"\\s+:\\s+(.*?),", 1);
							
				String northeastLon = Util.match(dirs [0], "lng\"\\s+:\\s+(.*?)\\s", 1);
				
				String southwestLat = Util.match(dirs [1], "lat\"\\s+:\\s+(.*?),", 1);
				
				String southwestLon = Util.match(dirs [1], "lng\"\\s+:\\s+(.*?)\\s", 1);

				BoundaryCoordincateMap.put (key, new double[]{
						Double.parseDouble(northeastLat),
						Double.parseDouble(northeastLon),
						Double.parseDouble(southwestLat),
						Double.parseDouble(southwestLon)
						});
			}
		}
	}

	public static boolean boundaryCheckForState(String state,String latitude,String longitude,String country){
		
		try {
			return boundaryCheckForState (state,Double.parseDouble(latitude),Double.parseDouble(longitude),country);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean boundaryCheckForState(String state,double latitude,double longitude,String country) {
		
		loadCoordincateMap (country);
	//	U.log(BoundaryCoordincateMap.size());
		
		double[] value = BoundaryCoordincateMap.get(state+","+country);
//		U.log(state+","+country);
//		U.log(Arrays.toString(BoundaryCoordincateMap.get(state+","+country)));

		double northEastLat = value[0];
		double northEastLon = value[1];

		double southWestLat = value[2];
		double southWestLon = value[3];
		if (latitude <= northEastLat && Math.abs (longitude) >= Math.abs (northEastLon)) { // NorthEast Checking.
			if (latitude >= southWestLat && Math.abs (longitude) <= Math.abs (southWestLon)) { // NorthEast Checking.
				return true;
			}
		}
		return false;				
	}
	
	public static boolean boundaryCheckForCityZipState(String latitude,String longitude,String northEastLat,String northEastLon,String southWestLat,String southWestLon){
		
		try {
			return boundaryCheckForCityZipState (Double.parseDouble(latitude),Double.parseDouble(longitude),
					Double.parseDouble(northEastLat),Double.parseDouble(northEastLon),
					Double.parseDouble(southWestLat),Double.parseDouble(southWestLon));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean boundaryCheckForCityZipState(double latitude,double longitude,double northEastLat,double northEastLon,double southWestLat,double southWestLon) {
		
		if (latitude <= northEastLat && Math.abs (longitude) >= Math.abs (northEastLon)) { // NorthEast Checking.
			if (latitude >= southWestLat && Math.abs (longitude) <= Math.abs (southWestLon)) { // NorthEast Checking.
				return true;
			}
		}
		return false;				
	}
	

	private static char[] singleCapVowels = {'á', 'ó', 'é', 'í','ú'};
	private static char[] vowels = {'a', 'o', 'e', 'i','u'};

	private static String removeSpecialChars(String str){
		for(int i = 0; i < singleCapVowels.length; i++){
			str = str.replace(singleCapVowels[i], vowels[i]);
		}
		return str;
	}
}
