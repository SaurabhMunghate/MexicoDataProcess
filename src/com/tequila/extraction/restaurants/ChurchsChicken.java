/**
 * @author Sawan
 * @date 22 July 2019
 */
package com.tequila.extraction.restaurants;

import java.io.IOException;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ChurchsChicken  extends DirectoryList{
	private static final String URL = "http://www.churchs.com.mx";
	private static final String NAME = "Church's Chicken";
	
	public static void main(String[] args) throws Exception {
		ChurchsChicken cc = new ChurchsChicken();
		cc.extractProcess();
		cc.printAll(NAME+".csv");
	}
	
	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String sicSub = "5812";
		String url = "https://churchs.com.mx/app/feed/getListAll";
		String  html = U.getHTML(url);
		
		JsonParser json = new JsonParser();
		JsonObject dataObj = (JsonObject) json.parse(html);
		
		U.log("dataObj: "+dataObj.toString());
		
		JsonArray arrayRestaurants = new JsonArray();
		arrayRestaurants = dataObj.getAsJsonArray("restaurants");
		U.log("arrayRestaurants: "+arrayRestaurants.size());
		
		for(JsonElement arrayRestaurant:arrayRestaurants) {
			
//			U.log("arrayRestaurant: "+arrayRestaurant);
			addDetails(arrayRestaurant.toString(), sicSub, url);
//			break;
		}
		
	}

	int i = 0;
	private void addDetails(String restaurantSec, String sicSub, String refUrl) throws IOException {
		
//	if(i == 30)
	{
		U.log("Count ==== "+i);
		U.log(restaurantSec);
		
		//BRANCH NAME
		String name = U.getSectionValue(restaurantSec, "name\":\"", "\",\"");
		U.log("name: "+name);
		
		
		//Address
		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK, zip = ALLOW_BLANK;
		
		String addressSec = U.getSectionValue(restaurantSec, "name_arabic\":\"", "\",\"");
		U.log("addressSec: "+addressSec);
		
		String[] addresses = addressSec.split(",");
		U.log("addresses size: "+addresses.length);
		
		if(addresses.length == 4) {
			street = addresses[0];
			colonia = addresses[1];
			city = addresses[2].replaceAll("\\d+", "");
			zip = Util.match(addresses[2], "\\d+");
			state = addresses[3];
			
			if(zip == null) zip = ALLOW_BLANK;
			
			U.log("street: "+street);
			U.log("colonia: "+colonia);
			U.log("city: "+city);
			U.log("zip: "+zip);
			U.log("state: "+state);
		}
		
		if(addresses.length == 5) {
			street = addresses[0];
			colonia = addresses[1] + ", "+addresses[2];
			city = addresses[3].replaceAll("\\d+", "");
			zip = Util.match(addresses[3], "\\d+");
			state = addresses[4];
			
			if(zip == null) zip = ALLOW_BLANK;
			
			U.log("street: "+street);
			U.log("colonia: "+colonia);
			U.log("city: "+city);
			U.log("zip: "+zip);
			U.log("state: "+state);
		}
		
		if(addresses.length == 6) {
			street = addresses[0] + ", " + addresses[1];
			colonia =  addresses[2] + ", " + addresses[3];
			city = addresses[4].replaceAll("\\d+", "");
			zip = Util.match(addresses[4], "\\d+");
			state = addresses[5];
			
			if(zip == null) zip = ALLOW_BLANK;
			
			if(zip == ALLOW_BLANK) {
				
				colonia =  addresses[2];
				zip = Util.match(addresses[3], "\\d+");
			}
			
			U.log("street: "+street);
			U.log("colonia: "+colonia);
			U.log("city: "+city);
			U.log("zip: "+zip);
			U.log("state: "+state);
		}
		
		if(addresses.length == 3) {
			street = addresses[0];
			colonia =  ALLOW_BLANK;
			city = addresses[1].replaceAll("\\d+", "");
			zip = Util.match(addresses[1], "\\d+");
			state = addresses[2];
			
			if(zip == null) zip = ALLOW_BLANK;
			
			if(zip == ALLOW_BLANK) {
				
				colonia =  addresses[2];
				zip = Util.match(addresses[3], "\\d+");
			}
			
			U.log("street: "+street);
			U.log("colonia: "+colonia);
			U.log("city: "+city);
			U.log("zip: "+zip);
			U.log("state: "+state);
		}
		
		
		//LatLong
		String latlon[] = {ALLOW_BLANK, ALLOW_BLANK};
		
		latlon[0] = U.getSectionValue(restaurantSec, "lat\":\"", "\"");
		latlon[1] = U.getSectionValue(restaurantSec, "lng\":\"", "\"");
		
		U.log("latlong: "+Arrays.toString(latlon));
		
		
		if(street == ALLOW_BLANK && city == ALLOW_BLANK && latlon[0] != ALLOW_BLANK) {
			
			String dataApi = getAddressFromLatlonHereApi(latlon);
			U.log("dataApi: "+dataApi);
			
			//---------------------Getting state, zip from API data
			if(dataApi != ALLOW_BLANK) {
				
				street = U.getSectionValue(dataApi, "Street\":\"", "\",\"");
				U.log("street inside: "+street);
				
				colonia = U.getSectionValue(dataApi, "District\":\"", "\",\"");
				U.log("colonia inside: "+colonia);
				
				city = U.getSectionValue(dataApi, "City\":\"", "\",\"");
				U.log("city inside: "+city);
				
				zip = Util.match(dataApi, "PostalCode\":\"\\d{5}").replace("PostalCode\":\"", "").trim();
				U.log("zip inside: "+zip);
				
				state = U.getSectionValue(dataApi, "State\":\"", "\",\"").trim();
				U.log("state inside: "+state);
			}
			
		}
		
		if(state != null) {
			
			state = state.replaceAll("Tamps\\.|TAMPS", "Tamaulipas").replaceAll("Gto.", "Guanajuato")
					.replaceAll("Chih.", "Chihuahua").replaceAll("Coah.", "Coahuila").replaceAll("S.L.P.", "San Luis Potosi");
		}
				
		
		addCompanyDetailsFromMexico(sicSub, NAME+" "+name, ALLOW_BLANK, ALLOW_BLANK, URL);
		addAddress(street, colonia, city, state, zip);
		addCompanyHoursOfOperation(ALLOW_BLANK);
		addBoundaries(latlon[0], latlon[1]);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
	}
	i++;
	}
	
	public String getAddressFromLatlonHereApi(String[] latlon) throws IOException {
		
		String geoData = ALLOW_BLANK;
		
		String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
		U.log("Here API Url: "+url);
		
		geoData = U.getHTML(url); 
		
		return geoData;
	}
}
