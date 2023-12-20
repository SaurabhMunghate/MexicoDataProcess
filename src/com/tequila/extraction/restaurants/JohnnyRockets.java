/**
 * @author Sawan
 * @date 23 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class JohnnyRockets  extends DirectoryList{
	private static final String URL = "https://www.johnnyrockets.com";
	private static final String NAME = "Johnny Rockets";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JohnnyRockets jr = new JohnnyRockets();
		jr.extractProcess();
//		jr.printAll(NAME+".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String sicSub = "5812";
		String url = "https://www.johnnyrockets.com/locations/";
		
		String  html = U.getHTML(url);
		String section = U.getSectionValue(html, "<div id=\"cg_mexico\" class=\"country", "class=\"country_group\">");
		
		String sections[] = U.getValues(section, "<div class=\"list__item loc_item\"", "Get Directions</a>");
		U.log(sections.length);
		for(String sec : sections){
			addDetails(sec, sicSub, url);
		}
	}

	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl) {
		// TODO Auto-generated method stub
//	if(i == 0)
	{
		U.log("count ="+i);
//		U.log(section);
		
		//URL
		String url = Util.match(section, "href=\"(.*?)\">", 1);
//		U.log(url);
		
		//Name
		String name = Util.match(section, "href=\"(.*?)\">(.*?)</a>", 2);
//		U.log(name);
		
		//Lat-Lng
		String lat = U.getSectionValue(section, "data-lat=\"", "\"");
		String lng = U.getSectionValue(section, "data-lon=\"", "\"");
	
		//Address
		String addSec = U.getSectionValue(section, "class=\"data__address\">", "</div>");
		addSec = addSec.replace("<br>", ", ").replaceAll("\\s{2,}", " ");
		U.log(addSec);

		String add [] = U.getAddress(addSec);
		
		//Add details
		addCompanyDetailsFromMexico(sicSub, NAME+" "+name, ALLOW_BLANK, ALLOW_BLANK, URL+url);
//		addAddress(street, colonia, city, state, zip);
		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(lat, lng);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
	}
	i++;
	}

}
