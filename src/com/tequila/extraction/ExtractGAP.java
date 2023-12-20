package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractGAP {
	public static void main(String[] args) throws Exception {
		extractData();
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
		String website="https://www.gap.com.mx";
		String jsonHtml=U.getHTML("https://www.gap.com.mx/rest/stores/4520,4523,4522,4516,4519,4524,4517,4521,4514,4513,4515,4511,4512,4518,4465,4471,4488,4493,4491,4456,4506,4497,4461,4487,4486,4480,4482,4481,4500,4457,4492,4508,4476,4504,4485,4495,4479,4494,4470,4472,4496,4483,4473,4466,4460,4458,4477,4469,4507,4501,4462,4459,4467,4509,4490,4489,4475,4502,4468,4505,4463,4510,4478,4503,4464,4499,4484,4498,4474");
		String listings[]=U.getValues(jsonHtml, "{", "}");
//		U.log(listings.length);
		for (String listing : listings) {
			listing=StringEscapeUtils.unescapeJson(listing);
			U.log(listing);
			String latLonSec=U.getSectionValue(listing, "\"latlng\":\"", "\"");
			String latLon[]=latLonSec.split(",");
			String companyName=U.getSectionValue(listing, "\"title\":\"", "\"");
			String phoneSec=U.getSectionValue(listing, "<a href=\"tel:", "\"");
			String phone="";
			if (phoneSec!=null) {
				phone=URLDecoder.decode(phoneSec,StandardCharsets.UTF_8.toString());
			}
			U.log(phone);
			String streetAdd=U.getSectionValue(listing, "<span class=\"address-line1\">", "</span>");
			String city=U.getSectionValue(listing, "<span class=\"address-line2\">", "</span>");
			String zip=U.getSectionValue(listing, "<span class=\"postal-code\">", "</span>");
			String state=U.matchState(U.getSectionValue(listing, "<span class=\"locality\">", "</span>"));
			U.log(streetAdd+" <-> "+city+" <-> "+state+" <-> "+zip);
			String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAdd),"",U.toTitleCase(city),U.toTitleCase(state),zip.trim(),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,latLon[0],latLon[1],website,U.getTodayDate()};
			writer.writeNext(out);
			U.log(Arrays.toString(out));
//			break;
		}
		FileUtil.writeAllText(U.getCachePath()+"GAP_Branches.csv", sw.toString());
		sw.close();
		writer.close();
	}
}
