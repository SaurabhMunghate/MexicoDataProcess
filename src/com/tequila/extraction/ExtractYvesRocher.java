package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractYvesRocher {
	public static void main(String[] args) {
		try {
			extractData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static String sicCode="7231";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() throws IOException {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		HashSet<String> uniqueSet=new HashSet<>();
		String website="https://www.yvesrocher.com.mx";
		String baseUrl="https://www.yvesrocher.com.mx/YR/Maps/GetStoreLocatorJSON";
		String baseHtml=U.getHTML(baseUrl);
		U.log(baseHtml);
		String listings[]=U.getValues(baseHtml, "{", "}");
//		U.log(listings.length);
		String companyName="Yves Rocher De Mexico, S.A. De C.V.";
		for (String listing : listings) {
			U.log(listing);
			String lat=U.getSectionValue(listing, "\"Latitude\":", ",");
			String lon=U.getSectionValue(listing, "\"Longitude\":", ",");
			String add=U.getSectionValue(listing, "\"Address\":\"", "\"");
			String formattedAdd[]=U.getAddress(add);
			String zip=U.getSectionValue(listing, "\"ZipCode\":\"", "\"");
			String cityState[]=U.getSectionValue(listing, "\"City\":\"", "\"").split(",");
			U.log(U.getSectionValue(listing, "\"City\":\"", "\""));
			String city=formattedAdd[2].replaceAll("C.P|Delg. ", "");
			if (city==null||city.trim().length()==0||city.trim().length()>10) {
				city=cityState[0].replaceAll("Delg. ", "");
			}
			String State=U.findState(formattedAdd[3].replaceAll("C.P", ""));
			if (State==null||State.trim().length()<=1) {
				State=U.findState(cityState[1]);
			}
			
			String phone=U.getSectionValue(listing, "\"Phone\":\"", "\"");
			if (uniqueSet.add((companyName+add).toLowerCase())) {
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(formattedAdd[0]),U.toTitleCase(formattedAdd[1]),U.toTitleCase(city),U.toTitleCase(State),zip,U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,"1959",lat,lon,baseUrl,U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
			}
//			break;
		}
		FileUtil.writeAllText(U.getCachePath()+"YVESRocher_Branches.csv", sw.toString());
		sw.close();
		writer.close();
	}
}
