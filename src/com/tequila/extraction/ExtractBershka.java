package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractBershka {
	public static void main(String[] args) {
		try {
			extractData();
		} catch (IOException e) {
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
	private static void extractData() throws IOException {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		String website="https://www.bershka.com/mx/mujer-c1010193132.html";
		String companyName="Bershka";
		String baseUrl="https://www.bershka.com/itxrest/2/bam/store/45109520/physical-store?languageId=-5&countryCode=MX&latitude=19.3538129&longitude=-99.16360510000004&receiveEcommerce=false&favouriteStores=true&lastStores=false&max=100&appId=1";
		String jsonHtml=U.getHTML(baseUrl);
		String listings[]=U.getValues(jsonHtml, "{\"id\":", "\"district\"");
		for (String listing : listings) {
			U.log(listing);
			String state=U.matchState(U.getSectionValue(listing, "\"state\":\"", "\""));
			
			String lat=U.getSectionValue(listing, "\"latitude\":", ",");
			String lon=U.getSectionValue(listing, "\"longitude\":", ",");
			String streetAdd=U.getSectionValue(listing, "\"addressLines\":[\"", "\"]");
			String city=U.getSectionValue(listing, "\"city\":\"", "\",");
			String zipCode=U.getSectionValue(listing, "\"zipCode\":\"", "\",");
			String stateCode=U.getSectionValue(listing, "\"stateCode\":\"", "\"");
			if (stateCode.equals("MXMX")) {
				state="Mexico State";
			}
			if (state.equals("Mexico City")) {
				city=MXStates.getMexicoCityMunicipalites(zipCode);
			}
			String phone=U.getSectionValue(listing, "\"phones\":[\"", "\"],");
			String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAdd),"",U.toTitleCase(city),U.toTitleCase(state),zipCode.trim(),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,lat,lon,baseUrl,U.getTodayDate()};
			writer.writeNext(out);
			U.log(Arrays.toString(out));
//			break;
		}
		FileUtil.writeAllText(U.getCachePath()+"Bershka_Branches.csv", sw.toString());
		sw.close();
		writer.close();
	}
}
