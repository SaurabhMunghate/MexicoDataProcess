/**
 * @author Sawan
 * @date 22 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class PandaExpress extends DirectoryList{
	private static final String URL = "https://www.pandaexpress.com.mx";
	private static final String NAME = "Panda Express";
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PandaExpress pe = new PandaExpress();
		pe.extractProcess();
		pe.printAll(NAME+".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
	
		String yearInBiz = "2011";
		String email = "contacto@pandaexpress.com.mx";
		String sicSub = "5812";
		String url = "https://www.pandaexpress.com.mx/es/userlocation";
		
		String  html = U.getHTML("https://www.pandaexpress.com.mx/es/userlocation/searchbycoordinates?lat=34.0667&lng=-118.0833&limit=25&hours=true&_=1563784698530");
		String sections[] = U.getValues(html, "{\"Id\":", "\"ClientId\":");
		U.log(sections.length);
		for(String section : sections){
			addDetails(section, sicSub, email, url, yearInBiz);
		}
	}

	int i = 0;
	private void addDetails(String section, String sicSub, String email, String refUrl, String yearInBiz){
//	if(i == 0)
	{
		U.log(section);
		String name = U.getSectionValue(section, "\"Name\":\"", "\"");
		U.log(name);
		
		//Address
		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK, zip = ALLOW_BLANK;
		street = U.getSectionValue(section, "\"Address\":\"", "\"");
		city = U.getSectionValue(section, "\"City\":\"", "\"");
		state = U.getSectionValue(section, "\"State\":\"", "\"");
		zip = U.getSectionValue(section, "\"Zip\":\"", "\"");
		
		String lat = U.getSectionValue(section, "Latitude\":", ",");
		String lng = U.getSectionValue(section, "Longitude\":", ",");
		
		if(zip == null)zip = ALLOW_BLANK;
		if(state == null) state = ALLOW_BLANK;
		
		String phone = U.getSectionValue(section, "\"Phone\":\"", "\"");
		//Add details
		addCompanyDetailsFromMexico(sicSub, NAME+" "+name, phone, ALLOW_BLANK, URL);
		addAddress(street, colonia, city, state, zip);
//		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(lat, lng);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, yearInBiz);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email);
	}
	i++;
	}
}
