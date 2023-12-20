package com.shatam.maps;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import com.shatam.utils.U;

public class MapQuest implements Map{

	private final static String COUNTRY = "MX";  //CA   //US
	public static void main(String[] args) {
		MapQuest map = new MapQuest();
		String add[] = {"Rio San Lorenzo Ote 25","Culiacan","Sinaloa","80000"};
		String[] latLng = map.getLatLong(add);
		U.log("latlng:::"+Arrays.toString(latLng));
		
	
		String[] latlng = {"24.7944","-107.39218"};
		String add1[] = map.getAddress(latlng);
		U.log("Add::"+Arrays.toString(add1));
	}
	
	public String[] getLatLong(String[] address) {
		String[] latLong = {"",""};

		String adr = String.join(",", address);
		try {
			String host = "http://www.mapquestapi.com/geocoding/v1/address?key=7AV661nmsttamglUwA8meTKVbngvSC2k&location="+
					URLEncoder.encode(adr, "UTF-8")+"&ignoreLatLngInput=false&callback=geocodeResult&outFormat=xml";
						
			URL url = new URL(host);
			U.log(url.toString());
//			U.log(U.getCache(url.toString()));
			String html = U.getHTML(url.toString());
			String statusCode = U.getSectionValue(html, "<statusCode>", "</statusCode>");
			if(statusCode.trim().equals("0") || !statusCode.startsWith("40") || !statusCode.trim().startsWith("50")){
				String stateSection = U.getSectionValue(html, "\'Country\'>"+COUNTRY, "<mapUrl>");
//				U.log(stateSection);
				if (stateSection != null) {
					latLong[0]= U.getSectionValue(stateSection,"<lat>", "</lat>");
					latLong[1]= U.getSectionValue(stateSection,"<lng>", "</lng>");
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return latLong;
	}

	
	public String[] getAddress(String[] latLong) {
		String[] add= {"","","",""};
		
		String location = String.join(",", latLong);
		try {
			String url = "http://www.mapquestapi.com/geocoding/v1/reverse?key=7AV661nmsttamglUwA8meTKVbngvSC2k&location="+location
		+"&includeNearestIntersection=true&callback=reverseGeocodeResult&outFormat=xml";

			U.log(url);
			String html= U.getHTML(url);
//			U.log(U.getCache(url));
			String statusCode = U.getSectionValue(html, "<statusCode>", "</statusCode>");
			if(statusCode.trim().equals("0") || !statusCode.startsWith("40") || !statusCode.trim().startsWith("50")){
				add[0] = U.getSectionValue(html, "<street>", "</street>");
				add[1] = U.getSectionValue(html, "type=\'City\'>", "</adminArea5>");
				if(add[1] == null || add[1].isEmpty())
					add[1] = U.getSectionValue(html, "type=\'County\'>", "</adminArea4>");
				
				add[2] = U.getSectionValue(html, "type=\'State\'>", "</adminArea3>");
				add[3] = U.getSectionValue(html, "<postalCode>", "</postalCode>");
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return add;
	}
}
