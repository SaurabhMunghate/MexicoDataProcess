package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractIvonneData {
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
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		String website="https://www.ivonne.com/";
		try {
			String baseHtml=U.getHTML(website);
			String dataSec=U.getSectionValue(baseHtml, "pos_json: JSON.parse(", "let template_list");
//			U.log(dataSec);
			String listings[]=U.getValues(dataSec, "{\"city\"", "]}");
//			U.log(listings.length);
			for (String lisitng : listings) {
				lisitng=StringEscapeUtils.unescapeJson(lisitng);
				U.log(lisitng);
				String companyName="Ivonne "+U.getSectionValue(lisitng, "\"name\":\"", "\"");
				String streetAdd=U.getSectionValue(lisitng, "\"address_1\":\"", "\",");
				String neighb=streetAdd.split(",")[1];
				streetAdd=streetAdd.split(",")[0];
				String city=U.getSectionValue(lisitng, ":\"", "\"");
				String state=U.matchState(U.getSectionValue(lisitng, "\"state\":\"", "\""));
				String zipCode=U.getSectionValue(lisitng, "\"postal_code\":\"", "\"");
				String phones=U.getSectionValue(lisitng, "\"telephone\":\"", "\"").replace(", ", ";");
				String email=U.getSectionValue(lisitng, "\"email\":\"", "\"");
				String lat=U.getSectionValue(lisitng, "\"latitude\":\"", "\"");
				String lon=U.getSectionValue(lisitng, "\"longitude\":\"", "\"");
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAdd),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),zipCode.trim(),U.formatNumbersAsCode(phones),null,website,email,null,null,null,null,null,lat,lon,website,U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Ivonne_Branches_1.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
