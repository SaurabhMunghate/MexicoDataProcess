package com.tequila.extraction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractHSBCData {
	public static void main(String[] args) {
		try {
			String jsonFile=FileUtil.readAllText("/home/mypremserver/MexicoCache/Cache/HSBC_JSON_DATA.txt");
			formatData(jsonFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void formatData(String jsonFile) throws Exception, IOException {
//		U.log(jsonFile);
		
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		String website="https://www.hsbc.com.mx/";
		String companyData[]=U.getValues(jsonFile, "{", "}");
		for (String company : companyData) {
			U.log(company);
			String name="Hsbc Bank Mexico "+U.getSectionValue(company, "\"name\": \"", "\"");;
			String phone=U.getSectionValue(company, "\"tel\": \"", "\"");
			String colonia=U.getSectionValue(company, "\"colony\": \"", "\"");
			String address=U.getSectionValue(company, "\"address\": \"", "\"");
			String lat=U.getSectionValue(company, "\"lat\": \"", "\"");
			String lon=U.getSectionValue(company, "\"lng\": \"", "\"");
			String postalCode=U.getSectionValue(company, "\"cp\": \"", "\"");
			String city=U.getSectionValue(company, "\"city\": \"", "\"");
			String state=U.matchState(U.getSectionValue(company, "\"state\": \"", "\""));
			String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(address),U.toTitleCase(colonia),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,lat,lon,"https://piblinkandlaunch.hsbc.com.mx/1/2/es/contacto/sucursales",U.getTodayDate()};
			writer.writeNext(out);
			U.log(Arrays.toString(out));
//			break;
		}
		FileUtil.writeAllText(U.getCachePath()+"HSBC_Branches_2.csv", sw.toString());
		sw.close();
		writer.close();
	}
	
}
