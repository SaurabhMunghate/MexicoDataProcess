package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.log4j.BasicConfigurator;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractLanix {
	
	public static void main(String[] args) {
		//BasicConfigurator.configure();
		extractData();
	}
	private static String sicCode="5045";
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
			String baseUrl="https://lanix.com/mx/centro-de-soluciones-lanix";
			String baseHtml=U.getPageSource(baseUrl);
			String dataValues[]=U.getValues(baseHtml, "<div class=\"store\"", " </div>");
			U.log(dataValues.length);
			int i=0;
			for (String dataVal : dataValues) {
				U.log(dataVal);
				String shopName=U.getSectionValue(dataVal, "class=\"fa fa-bookmark\"></i>", "</h4>");
				U.log(shopName);
				String addressSec=U.getSectionValue(dataVal, "<span style=\"font-weight:bold;\">", "<span style=\"font-weight:bold; \">");
//				U.log(addressSec);
				String address=U.getSectionValue(addressSec, "<p class=\"first-element\">", "</p>");
				String addVals[]=U.getValues(addressSec, "<p>", "</p>");
				String neighb=addVals[0];
				String cityState[]=addVals[1].split(",");
				String postalCode=addVals[2];
				String contactNo=Util.match(dataVal, "\\(?\\d{3}\\)? \\d{3} \\d{4}|\\(\\d{3}\\) \\d{3}-\\d{4}|\\(\\d{2}\\) \\d{4} \\d{4}|\\(\\d{2}\\) \\d{4}-\\d{4}");
				String email=Util.match(dataVal, "[_a-zA-Z1-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*");
				U.log(email);
				U.log(contactNo);
				U.log(addVals.length);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase("Nuevos Y Usados Concesionarios De Coches Y Camiones"),U.toTitleCase(shopName),U.toTitleCase(address),U.toTitleCase(neighb),U.toTitleCase(cityState[0].trim()),U.toTitleCase(cityState[1].trim()),postalCode,U.formatNumbersAsCode(contactNo),null,"https://lanix.com/mx",email,null,null,null,null,null,"","","https://lanix.com/mx/centro-de-soluciones-lanix",U.getTodayDate()};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Lanix_Distributor1.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
