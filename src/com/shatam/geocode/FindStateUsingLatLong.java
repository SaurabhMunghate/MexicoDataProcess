package com.shatam.geocode;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class FindStateUsingLatLong {

	private static final String inpuFilePath = "/home/glady/MexicoCache/Tequila_DATA/first-csv/files_25_jan/Ofertia-BooksStores_Correct_1_Correct.csv";
	List<String[]>  readLines = null;
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FindStateUsingLatLong find = new FindStateUsingLatLong();
		find.loadReadFile();
		find.readFile();
		find.writeFile();
		
	//	String add[] = find.getGoogleAddress(new String[] {"18.464708","-97.409994"});
	//	String add[] = find.getMapQeustAddress(new String[] {"18.464708","-97.409994"});
		
//		U.log(Arrays.toString(add));
//		U.log(MXStates.getFullNameFromAbbr("VE"));

	}
	
	void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(inpuFilePath));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void readFile(){
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
//			x++;
			if (x++ == 0) continue;
			

//			U.log(Arrays.toString(lines));
//			U.log(lines.length);
			
			String state = getState(lines[22], lines[23]);
			if(state.length() < 4){
				state = MXStates.getFullNameFromAbbr(state);
				state = U.matchState(state);
			}else{
				state = U.matchState(state);
			}
			lines[11] = state;
		}
	}
	
	
	private String getState(String lat,String lng){
		
		String[] latLong = {lat.trim(), lng.trim()};
		U.log(Arrays.toString(latLong));
		String state = getMapQeustAddress(latLong)[2];
		
		return state;
	}
	
	
	void writeFile(){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(inpuFilePath.replace(".csv", "_Find_State.csv")),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String[] getMapQeustAddress(String[] latLong) {
		String[] add= {"","","",""};
		
		String location = String.join(",", latLong);
		try {
			String url = "http://www.mapquestapi.com/geocoding/v1/reverse?key=7AV661nmsttamglUwA8meTKVbngvSC2k&location="+location
		+"&includeNearestIntersection=true&callback=reverseGeocodeResult&outFormat=xml";

			U.log(url);
			String html= U.getHTML(url);
//			U.log(html);
//			U.log(U.getCache(url));
			String statusCode = U.getSectionValue(html, "<statusCode>", "</statusCode>");
			if(statusCode.trim().equals("0") || !statusCode.startsWith("40") || !statusCode.trim().startsWith("50")){
				add[0] = U.getSectionValue(html, "<street>", "</street>");
				add[1] = U.getSectionValue(html, "type=\'City\'>", "</adminArea5>");
				if(add[1] == null || add[1].isEmpty())
					add[1] = U.getSectionValue(html, "type=\'County\'>", "</adminArea4>");
				
				add[2] = U.getSectionValue(html, "type=\'State\'>", "</adminArea3>");
				add[3] = U.getSectionValue(html, "<postalCode>", "</postalCode>");
			}else{
				U.delete(U.getCache(url));
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log(Arrays.toString(add));
		return add;
	}
	
	public String[] getGoogleAddress(String latLng[]) throws IOException{
		
		String st = latLng[0].trim() + "," + latLng[1].trim();
		String addr = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ st;
		U.log(addr);

		U.log(U.getCache(addr));
		String html = U.getGoogleHTML(addr);
//		U.log(html);
		String status = U.getSectionValue(html, "status\" : \"", "\"");
		final String[] type = {"ROOFTOP","RANGE_INTERPOLATED","GEOMETRIC_CENTER"};
		
		if(status.trim().equals("OK")){
			String[] section = U.getValues(html, "address_components", "viewport");
		
			for(String sec : section){
				String location = U.getSectionValue(st, "location_type\" : \"", "\"");
				if(section.length == 0){
					
				}
			}
			String txt = U.getSectionValue(html, "formatted_address\" : \"", "\"");
			U.log("txt:: " + txt);
			if (txt != null)
				txt = txt.trim();
		/**/
			String[] add = txt.split(",");
			if(add.length == 5){
				add[4] = add[3];
				add[3] = Util.match(add[2], "\\d{5}");  //zip
				add[2] = add[2].replaceAll("\\d+", "").trim();				
			}else if(add.length == 6){
				add[0] = add[0] +","+add[1];
				add[1] = add[2];
				add[3] = Util.match(add[3], "\\d{5}");  //zip
				add[2] = add[3].replaceAll("\\d+", "").trim();	
			}
			
			return add;
		}else{
			return new String[]{"","","","",""};
		}
	}
}
