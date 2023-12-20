package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractFarmaciasEspecializadas {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5912";
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
			int i=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> uniqueSet=new HashSet<>();
			String extractUrl="https://api.farmaciasespecializadas.com/api/v1/sucursales";
			String extratHtml=U.getHTML(extractUrl);
			String website="https://farmaciasespecializadas.com";
//			U.log(extratHtml);
			String lisitings[]=U.getValues(extratHtml, "{\"BranchID\"", "}");
			U.log(lisitings.length);
			for (String listing : lisitings) {
				U.log(listing);
				String lat=U.getSectionValue(listing, "\"Latitude\":\"", "\"");
				String lon=U.getSectionValue(listing, "\"Longitude\":\"", "\"");
				String phone=U.getSectionValue(listing, "\"Phone\":\"", "\"");
				String companyName="Farmacias Especializadas "+U.getSectionValue(listing, "\"Name\":\"", "\"");
				String address=U.getSectionValue(listing, "\"AddressStreet\":\"", "\"").replace("AV ", "Av. ").replace("BLVD ", "Blvd. ").replace("CALZ ", "Calz. ");
				String city=StringEscapeUtils.unescapeJson(U.getSectionValue(listing, "\"AddressCity\":\"", "\""));
				String state=U.matchState(StringEscapeUtils.unescapeJson(U.getSectionValue(listing, "\"AddressState\":\"", "\"")));
//				String state=U.getSectionValue(listing, "\"AddressState\":\"", "\"");
				
				String zip=U.getSectionValue(listing, "\"AddressZipCode\":\"", "\"");
				if (state.equals("Mexico City")) {
					city=MXStates.getMexicoCityMunicipalites(zip);
				}else if (state.equals("-")) {
					state=MXStates.findStateFromZip(zip);
				}
				String neighb=U.getSectionValue(listing, "\"AddressMunicipality\":\"", "\"");
				if (uniqueSet.add((companyName+address).toLowerCase())) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(address),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),U.toTitleCase(zip),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,lat,lon,website,U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"FarmaciaEspecializadas_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
