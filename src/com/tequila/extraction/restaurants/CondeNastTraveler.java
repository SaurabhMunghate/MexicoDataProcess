/**
 * @author Sawan
 * @date 17 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class CondeNastTraveler extends DirectoryList{

	private final String url = "https://www.cntraveler.com/";	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CondeNastTraveler cnt =  new CondeNastTraveler();
		cnt.extractProcess();
		cnt.printAll("Condé Nast Traveler.csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		
		String html = U.getHTML("https://www.cntraveler.com/gallery/best-restaurants-in-mexico-city");
		
		String [] sections = U.getValues(html, "{\"_source\":{},\"id\":\"", "\"md\":{\"aspectRatio\":\"3:4");
		U.log(sections.length);
		for(String section : sections){
			
			addDetails(section);
		}
	}
	
	private void addDetails(String section){
		//Comapny Name
		String name = U.getSectionValue(section, "\"hed\":\"", "\"");
		U.log(name);
		
		//Sic Sub
		String sicSub = ALLOW_BLANK;
		String typeCompany = U.getSectionValue(section, "modelName\":\"", "\"");
		if(typeCompany.contains("restaurant")) sicSub = "5812";
		else if (typeCompany.contains("bar")) sicSub = "5813";
	
		//Address
		String street = U.getSectionValue(section, "street\":\"", "\"");
		String colonia = U.getSectionValue(section, "neighborhood\":\"", "\"");
		String city = U.getSectionValue(section, "city\":\"", "\"");
		String state = U.getSectionValue(section, "state\":\"", "\"");
		String zip = U.getSectionValue(section, "postalCode\":\"", "\"");
		
		//Lat-lng
		String lat = U.getSectionValue(section, "lat\":", ",");
		String lng = U.getSectionValue(section, "lng\":", "}");
		
		//URL
		String companyUrl = U.getSectionValue(section, "externalUrls\":[\"", "\"]");
		
		//Phone
		String phone = U.getSectionValue(section, "phone\":[\"", "\"]");
		
		//Reference Url
		String refUrl = url + U.getSectionValue(section, "tags\":[],\"url\":\"", "\""); 
		
		//To check if record is duplicated or not. 
		if(isDuplicateRecord(sicSub, name, street, city, state, ALLOW_BLANK))return;
		
		//add details
		addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, companyUrl);
		addAddress(street, colonia, city, state, zip);
		addBoundaries(lat, lng);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		
	}

}
