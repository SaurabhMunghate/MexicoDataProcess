/**
 * @author Sawan
 * @date 19 July 2019
 */
package com.tequila.extraction.restaurants;

import java.io.IOException;
import java.util.Arrays;

import com.shatam.lightbox.loqate.LoqateHelper;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class BuffaloWildWings extends DirectoryList{

	private static final String URL = "https://buffalowildwings.com.mx";
	private static final String NAME = "Buffalo Wild Wings";
	
	final static String LOQATE_KEY = "CH54-GY38-HJ49-JM43";
	final static int noOfRecords = 10000;
	final static int startPoint = 24001;
	final static int MAXBATCHSIZE = 300;
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BuffaloWildWings bww = new BuffaloWildWings();
		bww.extractProcess();
		bww.printAll("BuffaloWildWings.csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String refUrl = "https://buffalowildwings.com.mx/";
		String html = U.getHTML(refUrl);
		U.log(U.getCache(refUrl));
		
//		String[] sections = U.getValues(html, "<div class=\"column column-2\">", "<div class=\"column column-3\">");
		
		String[] sections = U.getValues(html, "<div class=\"title-div-sucursal-item\">", "HORARIOS Y DELIVERY");
		U.log(sections.length);
		String sicSub = "5812";
		for(String section : sections){
			addDetails(section, sicSub, refUrl);
		}
	}
	
	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl) throws IOException {
		
//	if(i == 1)
		
	{
		U.log("Count =="+i);
/*		String companyUrl = U.getSectionValue(section, " href=\"", "\"");
		U.log(companyUrl);
*/	

		//Branch Names
		String companyName = U.getSectionValue(section, "\">", "</h3>").trim();
		U.log("companyName: "+companyName);
		
		
		U.log("section: "+section);
		
		//Address
		String city = ALLOW_BLANK, state = ALLOW_BLANK;

		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, zip = ALLOW_BLANK;
				
		String addSec = U.getSectionValue(section, "<h4 class=\"pdir", "<div class=\"telefonowrapper");
		
		if(addSec != null && addSec.contains("</h4><h4 class=\"pnormal\">")) {
			addSec = addSec.replace("</h4><h4 class=\"pnormal\">", ", ");
		}
		
		U.log("addSec === "+addSec);
		
	
		
		String street_one = U.getSectionValue(addSec, "eccion\">", "</h4>");
		U.log("street_one: "+street_one);
		
		String[] add = SplitNeighborhoodFromAdd.splitColonia(street_one);
		U.log("add: "+Arrays.toString(add));
		
		street = add[0];
		U.log("street: "+street);
		
		colonia = add[1];
		U.log("colonia: "+colonia);
		
		city = U.getSectionValue(section, "</h3><h3 class=\"h2tall\">", "</h3></div><div class=\"second");
		U.log("city: "+city);
				
		//LATLONG SECTION - HERE API.
		String latlon[] = {ALLOW_BLANK, ALLOW_BLANK};
		String dataApi = ALLOW_BLANK;
		
		if(latlon[0] == ALLOW_BLANK && latlon[1] == ALLOW_BLANK) {
			
			if(section.contains("google.com/maps")) {
				
				String geoSec = U.getSectionValue(section, "google.com/maps", "data=");
				U.log("geoSec: "+geoSec);
				
				if(geoSec != null) {
					
					String geoCode = U.getSectionValue(geoSec, "/@", ",1");
//					if(geoCode==null) geoCode = U.getSectionValue(geoSec, "/@", ",13z");
					U.log("geoCode: "+geoCode);
					
					if(geoCode.contains(",")) {
						
						String[] geoarray = geoCode.split(",");
						latlon[0] = geoarray[0];
						latlon[1] = geoarray[1];
						
						U.log("latlon ==== "+Arrays.toString(latlon));
						
						dataApi = getAddressFromLatlonHereApi(latlon);
						U.log("dataApi: "+dataApi);
						
					}
				}
				
			}
		} 
		
		
		//---------------------Getting state, zip from API data
		if(dataApi != ALLOW_BLANK) {
			
			zip = Util.match(dataApi, "PostalCode\":\"\\d{5}").replace("PostalCode\":\"", "").trim();
			U.log("zip: "+zip);
			
			state = Util.match(dataApi, "State\":\"\\w+\"").replace("State\":\"", "").replace("\"", "").trim();
			U.log("state: "+state);
		}
		
		U.log("Street :"+street);
		U.log("Colonia :"+colonia);
		U.log("City :"+city);
		U.log("State :"+state);
		U.log("Zip :"+zip);

		
		//phone
		String phone = U.getSectionValue(section, "tel:", "\"").trim();
		U.log("phone: "+phone);
	
		
		//add details
		addCompanyDetailsFromMexico(sicSub, NAME+" "+companyName, phone, ALLOW_BLANK, URL);
		addAddress(street, colonia, city, state, zip);
		addBoundaries(ALLOW_BLANK, ALLOW_BLANK);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addCompanyHoursOfOperation(ALLOW_BLANK);
		addBoundaries(latlon[0], latlon[1]);
		
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
