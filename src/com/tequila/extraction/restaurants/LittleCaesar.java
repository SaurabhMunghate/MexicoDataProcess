/**
 * @author Sawan
 * @date 22 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class LittleCaesar extends DirectoryList{
	private static final String URL = "https://mexico.littlecaesars.com";
	private static final String NAME = "Little Caesar";
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		LittleCaesar lc = new LittleCaesar();
		lc.extractProcess();
		lc.printAll(NAME+".csv");
		
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String yearInBiz = "2006";
		String sicSub = "5812";
		String url = "https://mexico.littlecaesars.com/sucursales";
		
		String  html = U.getHTML("https://mexico.littlecaesars.com/Portals/_default/skins/xcillion/js/sucursales5.json");
		String sections[] = U.getValues(html, "{\"id\":", "}");
		U.log(sections.length);
		for(String section : sections){
			addDetails(section, sicSub, url, yearInBiz);
		}
	}
	
	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl, String yearInBiz){
//	if(i == 0)
	{
		U.log("Count ="+i);
		U.log(section);
		
		String name = U.getSectionValue(section, "\"name\":\"", "\"");
		U.log(name);
		//Address
		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK, zip = ALLOW_BLANK;
		street = U.getSectionValue(section, "\"street\":\"", "\"").trim()+" "+ U.getSectionValue(section, "\"number\":\"", "\"").trim();
		
		colonia = U.getSectionValue(section, "\"subLocality\":\"", "\"");
		city = U.getSectionValue(section, "\"locality\":\"", "\"");
		state = U.getSectionValue(section, "\"state\":\"", "\"");
		zip = U.getSectionValue(section, "\"code\":\"", "\"");
		
		String lat = U.getSectionValue(section, "lat\":", ",");
		String lng = U.getSectionValue(section, "lng\":", ",");
		
		if(zip == null)zip = ALLOW_BLANK;
		if(state == null) state = ALLOW_BLANK;
		
		String phone = U.getSectionValue(section, "\"Phone\":\"", "\"");
		
		//Add details
		addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, URL);
		addAddress(street, colonia, city, state, zip);
//		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(lat, lng);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, yearInBiz);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
	}
	i++;
	}
}
