package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.chinmay.test.JsonGoogleApiLatlonExtraction;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractVolkswagon {
	public static void main(String[] args) {
		//extractData();
		testDistance();
	}
	private static void testDistance() {
		
	}
	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String dataUrl="https://www.vw.com.mx/app/concesionarias/vw-mx/es/B%C3%BAsqueda%20de%20concesionarias%20Volkswagen/+/23.9108313666701/-101.82614999999998/5/+/+/+/+";
			String dataHtml=U.getHTML(dataUrl);
			//U.log(StringEscapeUtils.unescapeJson(U.getSectionValue(dataHtml, "<script type=\"x-store/initial-state\">", "</script>")));
			String listingSec=URLDecoder.decode(U.getSectionValue(dataHtml, "<script type=\"x-store/initial-state\">", "</script>"), StandardCharsets.UTF_8.toString());
			String lisitings[]=U.getValues(listingSec, "\"id\":\"", "\"regions\"");
			U.log(lisitings.length);
			int i=0;
			for (String listing : lisitings) {
				U.log(listing);
				String name=U.getSectionValue(listing, "\"name\":\"", "\"");
				String streetAdd=U.getSectionValue(listing, "\"street\":\"", "\"");
				String city=U.getSectionValue(listing, "\"city\":\"", "\"");
				String postalCode=U.getSectionValue(listing, "\"postalCode\":\"", "\"");
				String latLonSec=U.getSectionValue(listing, "\"coordinates\":[", "]");
				String latLon[]=latLonSec.split(",");
				String phone=U.getSectionValue(listing, "\"phoneNumber\":\"", "\"");
				String testAdd[]= {latLon[0],latLon[1]};
				String jsonAdd[]=JsonGoogleApiLatlonExtraction.testJsonCode(testAdd);
				U.log(Arrays.toString(jsonAdd));
				if (jsonAdd[6].contains("Estado de México")) {
					jsonAdd[6]="Mexico State";
				}else if (jsonAdd[6].contains("Ciudad de México")) {
					jsonAdd[6]="Mexico City";
					city=MXStates.getMexicoCityMunicipalites(postalCode);
				}
				U.log(jsonAdd.length);
				String website=U.getSectionValue(listing, "\"website\":\"", "\"");
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase("Nuevos Y Usados Concesionarios De Coches Y Camiones"),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(jsonAdd[1]),U.toTitleCase(city),U.toTitleCase(jsonAdd[6]),postalCode.trim(),U.formatNumbersAsCode(phone),"",website,null,null,null,null,null,null,latLon[0],latLon[1],dataUrl,U.getTodayDate()};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Volkswagon_Distributor2.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
