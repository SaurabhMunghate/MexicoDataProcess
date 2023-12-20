package com.chinmay.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.SystemUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.shatam.utils.U;

public class GoogleAddressJson {
	public static void main(String[] args) {
		String add[]= {"Av Costera Miguel Alemán 186", "Magallanes", "Acapulco", "Guerrero", "39670"};
		String latLon[]= {"16.8470032","-99.908419"};
		try {
			new GoogleAddressJson().getGoogleAddressFromLatLon(latLon);
//			new GoogleAddressJson().getGoogleLatLonFromAddress(add);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getGoogleAddressFromLatLon(String latLon[]) throws IOException, ParseException {
		U.log("Input::::::::\nLatitude: "+latLon[0]+" Longitude: "+latLon[1]);
		String latLonStr=latLon[0]+","+latLon[1];
		String addURL= "https://maps.googleapis.com/maps/api/geocode/json?latlng="+URLEncoder.encode(latLonStr,StandardCharsets.UTF_8.toString());
		String googleHtml=U.getGoogleHTML(addURL);
		ObjectMapper mapper = new ObjectMapper();
		GoogleGeoCodeResponse result = mapper.readValue(googleHtml,GoogleGeoCodeResponse.class);
		String city="",state="",street="",neighbourhood="",postalCode="",formattedaddress="";
		for (int i = 0; i < result.results[0].address_components.length; i++) {
			if (result.results[0].address_components[i].types[0].equals("locality")) {
	            city = result.results[0].address_components[i].long_name;
	        }else if (result.results[0].address_components[i].types[0].equals("administrative_area_level_1")) {
	        	state=result.results[0].address_components[i].long_name;
			}else if (result.results[0].address_components[i].types[0].equals("sublocality")||(result.results[0].address_components[i].types.length>1&&result.results[0].address_components[i].types[1].equals("sublocality"))) {
				neighbourhood=result.results[0].address_components[i].long_name;
			}else if (result.results[0].address_components[i].types[0].equals("postal_code")) {
				postalCode=result.results[0].address_components[i].long_name;
			}/*else if(result.results[0].address_components[i].types[0].equals("route")) {
//				U.log(result.results[0].address_components[i].long_name);
//			}
*/		}
		formattedaddress=result.results[0].formatted_address;
		String add[]=formattedaddress.split(",");
		street=add[0];
		U.log(street+", "+neighbourhood+", "+city+", "+state+", "+postalCode);
		String out[]= {street,neighbourhood,city,state,postalCode};
		return out;
	}
	public String[] getGoogleLatLonFromAddress(String address[]) throws IOException, ParseException {
		U.log("Input::::::::\nAdd[0]: "+address[0]+" add[1]: "+address[1]+" add[2]: "+address[2]+" add[3]: "+address[3]+" add[4]: "+address[4]);
		String addressStr=address[0]+","+address[2]+","+address[3];
		String addURL= "https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(addressStr,StandardCharsets.UTF_8.toString());
		String googleHtml=U.getGoogleHTML(addURL);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(googleHtml);
		JSONObject jb = (JSONObject) obj;
		JSONArray jsonObject1 = (JSONArray) jb.get("results");
		JSONObject jsonObject2 = (JSONObject)jsonObject1.get(0);
		JSONObject jsonObject3 = (JSONObject)jsonObject2.get("geometry");
		JSONObject location = (JSONObject) jsonObject3.get("location");
		String latLon[]= {location.get("lat").toString(),location.get("lng").toString()};
		U.log("Output::::::::\nLatitude: "+latLon[0]+" Longitude: "+latLon[1]);
		return latLon;
	}
}
