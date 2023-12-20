/**
 * @author Sawan
 * @date 23 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ElPorton extends DirectoryList{
	private static final String URL = "http://porton.com.mx";
	private static final String NAME = "El Portón";
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ElPorton ep = new ElPorton();
		ep.extractProcess();
		ep.printAll(NAME+".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String sicSub = "5812";
		String url = "http://porton.com.mx/sucursales/";
		
		String  html = U.getHTML("http://porton.com.mx/wp-content/themes/elporton/ajax/getAllRestaurants.php");
		//html = StringEscapeUtils.unescapeHtml3(html);
		html = TranslateEnglish.removeUnicode(html);
		String sections[] = U.getValues(html, "{\"name\":", "}");
		U.log(sections.length);
		for(String section : sections){
			addDetails(section, sicSub, url);
		}
	}

	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl) {
		// TODO Auto-generated method stub
//	if(i == 24)
	{
		U.log("Count ="+i);
		U.log(section);
		
		//Name
		String name = U.getSectionValue(section, "\"", "\"");
		U.log(name);
		
		//Phone
		String phone = U.getSectionValue(section, "delegation\":\"", "\"");
		phone = phone.replace("Teléfonos:", "").replace(",", ";");
		//lat-Lng
		String lat = U.getSectionValue(section, "latitude\":\"", "\"");
		String lng = U.getSectionValue(section, "longitude\":\"", "\"");
		
		//Address
		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK, zip = ALLOW_BLANK;
		street = U.getSectionValue(section, "\"street\":\"", "\"").trim();
		
		colonia = U.getSectionValue(section, "\"colony\":\"", "\"");
//		city = U.getSectionValue(section, "\"locality\":\"", "\"");
//		state = U.getSectionValue(section, "\"state\":\"", "\"");
		zip = U.getSectionValue(section, "\"postal_code\":\"", "\"");
		if(zip.trim().length() < 5) zip = ALLOW_BLANK;
		else if(zip.trim().length() > 5){ 
			street = street +" "+zip;
			zip = ALLOW_BLANK;
		}
		if(Util.match(phone, "\\d+") == null && !phone.isEmpty()){
			city = phone;
			phone = ALLOW_BLANK;
		}
		
		if(city.isEmpty() || city.length() < 2){
			String v[] = colonia.split(",");
			if(v.length == 3){
				if(v[2].trim().matches("\\d+") && zip.length() < 2) zip = v[2];
				if(!v[0].trim().matches("\\d+")) colonia = v[0];
				if(!v[1].trim().matches("\\d+")) city = v[1];
			}
		}
				
		//Add details
		addCompanyDetailsFromMexico(sicSub, NAME+" ("+name.trim()+")", phone, ALLOW_BLANK, URL);
		addAddress(street, colonia, city, state, zip);
//		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(lat, lng);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);

	}
	i++;
	}
}
