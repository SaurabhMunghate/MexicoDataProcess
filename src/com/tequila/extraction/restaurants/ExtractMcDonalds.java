package com.tequila.extraction.restaurants;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.chinmay.test.SearchUsingGooglePlacesApi;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class ExtractMcDonalds extends DirectoryList{
	public static void main(String[] args) {
		try {
			ExtractMcDonalds extract=new ExtractMcDonalds();
			extract.extractProcess();
			extract.printAll("McDonalds.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void extractData() throws Exception {
		String sicSub="5812";
		String name="McDonald's";
		String baseUrl="https://www.mcdonalds.com.ec/api/restaurantsByCountry?country=MX";
		String html=U.getHTML(baseUrl);
//		U.log(html);
		String stores[]=U.getValues(html, "{", "}");
		U.log(stores.length);
		SearchUsingGooglePlacesApi search =new SearchUsingGooglePlacesApi();
		int i=0;
		U.log(stores.length);
		for (String store : stores) {
			U.log(i+++"->"+store);
			
			String lat=U.getSectionValue(store, "\"latitude\":\"", "\"");
			String lon=U.getSectionValue(store, "\"longitude\":\"", "\"");
			String branchName=U.getSectionValue(store, "\"name\":\"", "<br\\/>");
			String addressSec=U.getSectionValue(store, "<small>", "<\\/small>");
			if (addressSec==null) addressSec=U.getSectionValue(store, "\"name\":\"", ", \"");
			addressSec=StringEscapeUtils.unescapeJson(addressSec);
			//String detailHtml=U.getHTML(U.getSectionValue(store, "\"href\":\"", "\""));
			String add[]=search.getAddressFromHereUsingPlaceApi("McDonald's", Double.parseDouble(lat), Double.parseDouble(lon), 100);
			String addCol[]=SplitNeighborhoodFromAdd.splitColonia(addressSec.replace("\\\"", ""));
			//U.log("--==--="+Arrays.toString(add));
			//U.log(Arrays.toString(addCol));
			if(isDuplicateRecord(sicSub, name, addCol[0], add[2], add[3], add[4]))continue;
			//add details
			addCompanyDetailsFromMexico(sicSub, name, add[6], ALLOW_BLANK, "https://www.mcdonalds.com.mx/");
			addAddress(addCol[0], addCol[1], add[2], add[3], add[4]);
			addBoundaries(lat, lon);
			addReferenceUrl(baseUrl);
			addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
			break;
		}
	}
	@Override
	protected void extractProcess() throws Exception {
		extractData();
	}
}
