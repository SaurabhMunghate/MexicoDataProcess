package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class Extract9deagosto {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="6061";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			String companyName="Caja Popular 9 De Agosto Salamanca Scl De Cv";
			String website="http://9deagosto.com/";
			String openedYear="1928";
			String baseUrl="http://9deagosto.com/sucursales/";
			String basetHtml=U.getHTML(baseUrl);
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			int i=0;
			String businessLists[]=U.getValues(basetHtml, "<img class=\"size-medium wp-image-1122 alignleft\"", "<div class=\"textwidget\"></p>");
			for (String business : businessLists) {
				U.log(business);
				String phone=U.getSectionValue(business, "100vw, 300px\" />", "</h4>");
				
				if (Util.match(phone, "\\d+")==null) {
					phone=U.getSectionValue(business, "<h4 style=\"text-align: left;\">", "</h4>");
				}
				U.log(phone);
				String addressSec=U.getSectionValue(business, "</h4>", "<h4>Horario:</h4>");
				if (addressSec==null) {
					addressSec=U.getSectionValue(business.replace("Bellavista</h4>", ""), "</h4>", "</h4><h4 class=\"p1");
				}
				addressSec=addressSec.replace("</h4><h4>", ",").replaceAll("<h4>|</h4>", "");
				U.log(addressSec);
				String mapSec=U.getSectionValue(business, "<iframe src=\"", "\"");
				U.log(mapSec);
				String lng=U.getSectionValue(mapSec, "!2d", "!3d");
				String lat=U.getSectionValue(mapSec, "!3d", "!");
				U.log(lat);
				U.log(lng);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(addressSec),"","","","",U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,openedYear,lat,lng,baseUrl,U.getTodayDate()};
				writer.writeNext(out);
			//	break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Caja Popular 9 de Agosto.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
