package com.tequila.extraction;

import java.io.StringWriter;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.U;

public class ExtractBancoCompartamosSA {
	String companyName="Banco Compartamos, S.A.";
	private static String sicCode="6021";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		String postrurl="https://localizadorcompartamos.com/officelocator/pages/AdvanceSearch1";
		HashSet<String> dupliData=new HashSet<>();
		HashSet<String> intit=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String postData="zip=&name=&category=&lat=19.68555&lng=-99.180345&flag=&ipaddress=";
		try {
			String output=U.sendPostRequest(postrurl, postData);
			U.log(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
