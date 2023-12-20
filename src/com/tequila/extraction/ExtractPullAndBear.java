package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractPullAndBear {
	String baseUrl="https://www.pullandbear.com/mx/en/store-locator.html";
	String website="https://www.pullandbear.com/mx/en";
	String companyName="Pull & Bear";
	HashSet<String> unique=new HashSet<>();
	public static void main(String[] args) {
		try {
			new ExtractPullAndBear().extractData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String sicCode="5651";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	StringWriter sw=new StringWriter();
	CSVWriter writer=new CSVWriter(sw);
	
	String sicdetails[]=Sic.sicInfo(sicCode);
	private void extractData() throws IOException {
		writer.writeNext(HEADER);
		
		String baseHtml=U.getHTML(baseUrl);
		String citySecs=U.getSectionValue(baseHtml, "\"list-cities\"", "</div>").replace("<li><span class=\"icon\"", "");
		String cities[]=U.getValues(citySecs, "<li>", "</li>");
		for (String city : cities) {
			String googleJson=U.getHTML("https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(city.toLowerCase(), StandardCharsets.UTF_8.toString())+",mexico&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
			String latlonSecs=U.getSectionValue(googleJson, " \"location\" : {", "\"location_type\"");
			String lat=U.getSectionValue(latlonSecs, "\"lat\" : ", ",");
			String lon=U.getSectionValue(latlonSecs, "\"lng\" : ", "}").trim();
			getData(lat,lon);
//			U.log(lat+" <--> "+lon);
		}
		cities=U.getValues(citySecs, "<span class=\"name\">", "</span>");
		for (String city : cities) {
			String googleJson=U.getHTML("https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(city.toLowerCase(), StandardCharsets.UTF_8.toString())+",mexico&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
			String latlonSecs=U.getSectionValue(googleJson, " \"location\" : {", "\"location_type\"");
			String lat=U.getSectionValue(latlonSecs, "\"lat\" : ", ",");
			String lon=U.getSectionValue(latlonSecs, "\"lng\" : ", "}").trim();
			getData(lat,lon);
//			U.log(lat+" <--> "+lon);
		}
		//U.log(citySecs);
		FileUtil.writeAllText(U.getCachePath()+"PullAndBear_Branches.csv", sw.toString());
		sw.close();
		writer.close();
	}
	int i=0;
	private void getData(String lat, String lon) throws IOException {
//		if (i==1) {
//			return;
//		}
//		i=1;
		String jsonUrl="https://www.pullandbear.com/itxrest/2/bam/store/25009520/physical-store?favouriteStores=false&lastStores=false&closerStores=true&latitude="+lat+"&longitude="+lon+"&min=0&max=25&receiveEcommerce=true&countryCode=MX&languageId=-1&appId=1";
		String jsonHtml=U.getHTML(jsonUrl);
		String lisitngsSecs[]=U.getValues(jsonHtml, "{\"id\":\"", "\"district\":");
		for (String listings : lisitngsSecs) {
			U.log(listings);
			String state=U.matchState(U.getSectionValue(listings, "\"state\":\"", "\""));
			
//			String state=U.getSectionValue(listings, "\"state\":\"", "\"");
			String latitude=U.getSectionValue(listings, "\"latitude\":", ",");
			String longitude=U.getSectionValue(listings, "\"longitude\":", ",");
			String streetAdd=U.getSectionValue(listings, "\"addressLines\":[\"", "\"]");
			String city=U.getSectionValue(listings, "\"city\":\"", "\"");
			String zipCode=U.getSectionValue(listings, "\"zipCode\":\"", "\"");
			if (state.equals("Mexico City")) {
				city=MXStates.getMexicoCityMunicipalites(zipCode);
			}
			String phones=U.getSectionValue(listings, "\"phones\":[\"", "\"],");
			if (unique.add((companyName+streetAdd+city).toLowerCase())) {
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAdd),"",U.toTitleCase(city),U.toTitleCase(state),zipCode.trim(),U.formatNumbersAsCode(phones),null,website,null,null,null,null,null,null,latitude,longitude,baseUrl,U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
			}
			
//			break;
		}
	}
}
