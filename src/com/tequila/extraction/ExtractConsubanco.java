package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractConsubanco {
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		extractData();
	}
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			String website="https://www.consubanco.com";
			String jsonUrl="https://www.consubanco.com/assets/src/json/todos.json";
			String html=U.getHTML(jsonUrl);
//			U.log(html);
			String listings[]=U.getValues(html, "{", "}");
			for (String listing : listings) {
				U.log(listing);
				String companyName="Consubanco, S.A "+U.getSectionValue(listing, "\"title\": \"", "\"");
				String lat=U.getSectionValue(listing, "\"lat\": \"", "\"");
				String lon=U.getSectionValue(listing, "\"lng\": \"", "\"");
				String email=U.getSectionValue(listing, "\"city\": \"", "\"");
				String phone=U.getSectionValue(listing, "\"zip\": \"", "\"");
				String street=U.getSectionValue(listing, "\"street\": \"", "\"");
				String add[]=U.getAddress(street);
				U.log(Arrays.toString(add));
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,website,email.trim(),null,null,null,null,null,lat,lon,jsonUrl,U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Consubanco_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
