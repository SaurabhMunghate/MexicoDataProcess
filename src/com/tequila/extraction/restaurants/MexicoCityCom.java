/**
 * @author Sawan
 * @date 18 July 2019
 */
package com.tequila.extraction.restaurants;

import java.io.IOException;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class MexicoCityCom extends DirectoryList{

	public MexicoCityCom(String fileName){
		super(fileName);
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MexicoCityCom mcc = new MexicoCityCom("MexicoCityCom.csv");
		mcc.extractProcess();
		mcc.printAll();
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String[] urls = {
				"https://www.mexicocity.com/v/restaurants/",
				"https://www.mexicocity.com/v/nightclubs/",
				"https://www.mexicocity.com/v/bars/"
		};
		
		for(String url : urls){
			String html = U.getHTML(url);
			String[] sections = U.getValues(html, "<div class=\"SpotDescription\"><div class=\"credit", "</div></div></div>");
			for(String section : sections){
				
				//Sic Sub
				String sicSub = ALLOW_BLANK;
				if(url.contains("restaurant")) sicSub = "5812";
				else if (url.contains("bar") || url.contains("nightclubs")) sicSub = "5813";
				
				addDetails(section, sicSub, url);
			}
		}//eof for
		
	}

	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl) throws IOException{
//	if(i == 22)
	{
		U.log("Count ="+i);
		U.log(refUrl);
//		U.log(section);
		//company name
		String name = U.getSectionValue(section, "\">", "</div>");
		
		//url
		String companyUrl = U.getSectionValue(section, "a href=\"", "\"");
		
		//Phone
		String phone = Util.match(section, "Tel.:\\s?</(S|s)trong>(.*?)</p>", 2);
	
		//Address
/*		String addSec = U.getSectionValue(section, "Address: </Strong>", "<br>");
		if(addSec == null) addSec = U.getSectionValue(section, "Address: </strong>", "<br>");
		if(addSec == null) addSec = U.getSectionValue(section, "Address:</strong>", "<br>");
		if(addSec == null) addSec = U.getSectionValue(section, "Address: </strong>", "</p>");
		if(addSec == null) addSec ="";
*/		
		String addSec = Util.match(section, "Address:\\s?</(s|S)trong>(.*?)(<br>|</p>)", 2);
//		U.log(addSec);
		if(addSec == null) addSec ="";
//		addSec = addSec.replaceAll(", Mexico$", "");
		String[] add = U.getAddress(addSec);
		
		//Lat-Lng
		String mapUrl = U.getSectionValue(section, "</a><a href=\"", "\"");
		if(mapUrl == null && companyUrl.contains("https://goo.gl")){
			mapUrl = companyUrl;
			companyUrl = ALLOW_BLANK;
		}
//		U.log(mapUrl);
		
		
		String latLng [] = {ALLOW_BLANK, ALLOW_BLANK};
		if(mapUrl != null){
			String mapHtml = U.getHTML(mapUrl);
			String latLngSec = U.getSectionValue(mapHtml, "maps/api/staticmap?center=", "&amp;");
//			U.log(latLngSec);
			latLng = latLngSec.split("%2C");
		}
		
		//add details
		addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, companyUrl);
		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(latLng[0], latLng[1]);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
	}
	i++;
	}
}
