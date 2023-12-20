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

public class ExtractFramaCon {
	private static String sicCode="5912";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		HashSet<String> uniqueSet=new HashSet<>();
		String baseUrl="http://www.farmacon.com.mx/assets/sucursales.json";
		try {
			String baseHtml=U.getHTML(baseUrl);
//			U.log(baseHtml);
			String listingsSecs[]=U.getValues(baseHtml, "\"type\": \"Feature\",", "\"consultorio\":");
			for (String listing : listingsSecs) {
				U.log(listing);
				String latlonSecs=U.getSectionValue(listing, "\"coordinates\": [", "]");
				String latlon[]=latlonSecs.split(",");
				String temp=latlon[0];
				latlon[0]=latlon[1];
				latlon[1]=temp;
				String city=U.getSectionValue(listing, "\"ciudad\": \"", "\",");
				String state=U.matchState(U.getSectionValue(listing, "\"estado\": \"", "\","));
				String streetAdd=U.getSectionValue(listing, "\"calle\": \"", "\"")+" "+U.getSectionValue(listing, "\"numero\": \"", "\"");
				String neighb=U.getSectionValue(listing, "\"colonia\": \"", "\"");
				String zipCode=U.getSectionValue(listing, "\"cp\": ", ",");
				String companyName="Farmacon "+U.getSectionValue(listing, "\"tienda\": \"", "\"");
				String phone=U.getSectionValue(listing, "\"servicioDomicilio\": \"", "\",");
				if (phone==null)
					phone="";
				if (uniqueSet.add((companyName+streetAdd+state).toLowerCase())) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAdd),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),zipCode.trim(),U.formatNumbersAsCode(phone),null,"http://www.farmacon.com.mx",null,null,null,null,null,null,latlon[0].trim(),latlon[1].trim(),"http://www.farmacon.com.mx",U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"FarmaCon_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
