package com.tequila.extraction;

import java.io.StringWriter;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractNissanData {
	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		String jsonUrl="https://www.nissan.com.mx/data/dealerCache.json";
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		try {
			String jsonHtml=U.getHTML(jsonUrl);
			String jsonValues[]=U.getValues(jsonHtml, "\"business_name\"", "}}");
			U.log(jsonValues.length);
			int i=0;
			for (String jsonVal : jsonValues) {
				U.log(jsonVal);
				String businessName=U.getSectionValue(jsonVal, ":\"", "\"");
				String phone=U.getSectionValue(jsonVal, "telephone\":\"", "\"");
				String latLon[]= {"",""};
				latLon[0]=U.getSectionValue(jsonVal, "latitude\":", ",");
				latLon[1]=U.getSectionValue(jsonVal, "longitude\":", ",");
				String add[]={"","","","",""};
				add[0]=U.getSectionValue(jsonVal, "\"address\":\"", "\"");
				add[1]=U.getSectionValue(jsonVal, "\"address2\":\"", "\"");
				add[2]=U.getSectionValue(jsonVal, "\"city\":{\"", "\",\"state_id\"").replaceAll("id\":\\d+,\"name\":\"", "");
//				U.log(add[2]);
				add[3]=U.getSectionValue(jsonVal, "\"state\":{\"id\"", "\",\"slug\"").replaceAll(":\\d+,\"name\":\"", "");
				add[4]=U.getSectionValue(jsonVal, "\"postal_code\":\"", "\"");
				String email=U.getSectionValue(jsonVal, "\"email_sales\":\"", "\"");
				U.log(email);
				email=email!=null?email:"";
				String website=U.getSectionValue(jsonVal, "\"url\":\"", "\"");
				U.log(website);
//				U.log(add[3]);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(businessName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],U.formatNumbersAsCode(phone),null,"https://www.nissan.com.mx",email.toLowerCase(),null,null,null,null,null,latLon[0],latLon[1],"https://www.nissan.com.mx/distribuidores/#estado=0&ciudad=0",U.getTodayDate()};
				writer.writeNext(out);
				//break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Nissan_Distributor.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
