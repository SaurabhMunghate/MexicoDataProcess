package com.shatam.corrector;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.shatam.utils.U;
import com.shatam.utils.Util;

public class CityToState implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1352026170247150076L;
	
	public static void main(String[] args) throws Exception {
			CityToState cs=new CityToState();
			String input="Dolores Hidalgo";
//			CityToState.serializedSicData("/home/glady/MexicoCache/source/CityName.csv");
			/*U.log(U.findState(cs.findStateFromCity(input)));
			U.log(U.findState("Dolores Hidalgo"));*/
			cs.test();
	}
	public void test(){
		List<String> cityName = new ArrayList<String>();
		cityName.add("Atizapan De Zaragoza");
		cityName.add("Azcapotzalco");
		cityName.add("Monterrey");
		cityName.add("Apodaca,");
		cityName.add("Guadalajara");
		cityName.add("Reynosa");
		cityName.add("El Marques");
		cityName.add("Benito Juarez");
		cityName.add("Morleos");
		cityName.add("Culiacan");
		cityName.add("Apodaca");
		cityName.add("Playa Del Carmen");
		cityName.add("Cordoba");
		cityName.add("Chetumal");
		cityName.add("Tlalnepantla");
		cityName.add("Toluca");
		cityName.add("Miguel Hidalgo");
		cityName.add("Morelia");
		cityName.add("Agauscalientes");
		cityName.add("Torreón");
		cityName.add("Cancún");
		
		for(String city : cityName){
			U.log(city+"\t>>>"+U.findState(findStateFromCity(city)));
		}
	}
	
	private static HashMap<String, String[]> cityState= U.deserializedSicData("CityStateList.ser");
	public static String findStateFromCity(String city){
		Set<String>keys=cityState.keySet();
		for (String key : keys) {
			//U.log(Arrays.toString(cityState.get(key)));
			String temp=Util.match(key, city);
			if (temp!=null) {
				//U.log(temp);
				//U.log(Arrays.toString(cityState.get(key)));
				//U.log(cityState.get(key)[3]);
				return cityState.get(key)[2];
				//break;
			}
			
		}
		return "-";
	}
	public static void serializedSicData(String path) throws Exception
	{
		HashMap<String,String[]> cityStateMap=new HashMap<String,String[]>();
		String newFile = path;
		CsvListReader newFileReader = new CsvListReader(
				new FileReader(newFile), CsvPreference.STANDARD_PREFERENCE);
		List<String> listRow = null;
		
		int count=0;
		while ((listRow = newFileReader.read()) != null) 
		{

			if (count > 0) {	
				//U.log("kkkkk  "+listRow.toString());
				U.log("City  "+listRow.get(1));
				//String allData=listRow.get(0);
				
				String[] aSicData=listRow.toArray(new String[0]);
				U.log(Arrays.toString(aSicData));
				if(listRow.size()==3){
					//log(aSicData[0]);
					cityStateMap.put(listRow.get(1),aSicData);
				}
			}
			count++;
		}
		

		//U.log("fffffffff  "+sicMap.get("01"));
        FileOutputStream fileOut =new FileOutputStream(System.getProperty("user.dir")+"/CityStateList.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(cityStateMap);
        out.close();
        fileOut.close();
	        //System.out.printf(System.getProperty("user.dir")+"/sicCodeData.ser");

		//return sicObject;
		
	}
}
