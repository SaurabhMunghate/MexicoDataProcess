package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class ExtractZaraStores {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5651";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			List<String[]> zipCsv=U.readCsvFile("/home/mypremserver/DatabasesTequila/DatabaseCSv/zipdataset.csv");
			int s=0,i=0;
			HashSet<String> uniqueKey=new HashSet<>();
			String webSite="https://www.zara.com";
			String companyName="ZARA";
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int id=0;
//			String webSite="";
			String sicdetails[]=Sic.sicInfo(sicCode);
			for (String[] zip : zipCsv) {
				if(zip[1].contains("LATITUDE")) continue;
				U.log((s++)+" "+zip[0]);
				String lat=zip[1];
				String lng=zip[2];
				U.log(lat+" "+lng);
				String jsonUrl="https://www.zara.com/mx/en/stores-locator/search?lat="+lat+"&lng="+lng+"&ajax=true";
				U.log(jsonUrl);
				String jsonHtml=U.getPageSource(jsonUrl);
				
				jsonHtml=StringEscapeUtils.unescapeJson(jsonHtml);
//				U.log(jsonHtml);
				String listings[]=U.getValues(jsonHtml, "{\"datatype\"", "storeServices\":");
				for (String listing : listings) {
					U.log(listing);
					String latLon[]= {"",""};
				
					latLon[0]=U.getSectionValue(listing, "\"latitude\":", ",");
					latLon[1]=U.getSectionValue(listing, "\"longitude\":", ",");
					String streetAddd=U.getSectionValue(listing, "\"addressLines\":[\"", "\"]");
					String city=U.getSectionValue(listing, "\"city\":\"", "\"");
					String state=U.getSectionValue(listing, "\"state\":\"", "\"");
					String postalCode=U.getSectionValue(listing, "\"zipCode\":\"", "\"");
					String phone=U.getSectionValue(listing, "\"phones\":[\"", "\"]");
					state=state!=null?U.findState(state):"";
					city=state.equals("Mexico City")?MXStates.getMexicoCityMunicipalites(postalCode):city;
					String name=U.getSectionValue(listing, "\"name\":\"", "\"");
					if(uniqueKey.add((name+streetAddd).toLowerCase())){
						String out[] = {""+(id++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName+" "+name),U.toTitleCase(streetAddd),"",U.toTitleCase(city),U.toTitleCase(state),postalCode,U.formatNumbersAsCode(phone),null,webSite,null,null,null,null,null,null,latLon[0],latLon[1],jsonUrl,U.getTodayDate()};
						writer.writeNext(out);
					}
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Zara_Stores_2.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
