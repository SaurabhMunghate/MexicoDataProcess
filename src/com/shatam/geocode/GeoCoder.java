package com.shatam.geocode;

import java.util.Iterator;
import java.util.List;

import com.shatam.maps.BingMap;
import com.shatam.maps.GoogleMap;
import com.shatam.maps.Map;
import com.shatam.utils.U;

public class GeoCoder {

	private static final String FILE_PATH 
		= "/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Correct_A_ADD_CORRECT_STD_CITY.csv";
	
	public static void main(String[] args) throws Exception {
		Map map = new GoogleMap();
/*		Map p=new BingMap();
		
		String[] a= {"2013 Coach Rd","2013 Coach Rd","Ontario","L0E 1T0"};
		String[] b= {"45.4983521","-73.5756912"};
		String[] ll=p.getLatLong(a);
	//	String[] aa=p.getAddress(b);
		U.log(ll[0]+"  ,  "+ll[1]);
		//U.log(ll[0]+"  ,  "+ll[1]+" "+aa[0]+"  "+aa[1]+"  "+aa[2]+"  "+aa[3]);
*/
		extractLatLong(map);
	}
	
	
	public static void extractLatLong(Map map){
		List<String[]> readLines = U.readCsvFile(FILE_PATH);
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(x++ == 0)continue;
			if(lines[22].trim().isEmpty() && lines[23].trim().isEmpty()){
				if(lines[12].trim().length() == 4){
					lines[12] = "0"+lines[12].trim();
				}
				String latLng[] = map.getLatLong(new String[]{lines[8],lines[10],lines[11],lines[12]});
				lines[22] = latLng[0];
				lines[23] = latLng[1];
			}
		}
		U.writeCsvFile(readLines, FILE_PATH.replace(".csv", "_Add_LATLNG.csv"));
	}
}
