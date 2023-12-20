package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractSamsung_Stores {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5731";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time","Address Sec"
	};
	private static void extractData() {
		try {
			int i=0;
			HashSet<String> uniqueVal=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="https://www.samsung.com/mx/storelocator/_jcr_content/par.cm-g-store-locator-storelist/?nRadius=1000&latitude=23.634501&longitude=-102.552784&searchFlag=search&modelCode=&categorySubTypeCode=&localSearchCallYn=N";
			String baseHtml=U.getHTML(baseUrl);
			String listingSecs[]=U.getValues(baseHtml, "<div class=\"store-info\"", "<li class=\"\">");
			U.log(listingSecs.length);
			for (String listingSec : listingSecs) {
//				U.log(listingSec);
				listingSec=StringEscapeUtils.unescapeHtml4(listingSec);
				String storeName=U.getSectionValue(listingSec, "<h2 class=\"store-name\">", "</h2>");
				String phone=U.getSectionValue(listingSec, "<a class=\"telephone-link\" href=\"tel:", "\"");
				String addSec=U.getSectionValue(listingSec, "<p class=\"store-address\">", "</p>").trim();
				if (!uniqueVal.add((storeName+addSec).toLowerCase())) {
					continue;
				}
				String add[]=U.getAddress(addSec);
				U.log(Arrays.toString(add));
				String lat=U.getSectionValue(listingSec, "<input class=\"lat\" type=\"hidden\" value=\"", "\"");
				String lng=U.getSectionValue(listingSec, "<input class=\"long\" type=\"hidden\" value=\"", "\"");
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(storeName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,null,null,null,null,null,null,null,lat,lng,"https://www.samsung.com/mx/storelocator/",U.getTodayDate(),addSec};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Samsungs_Stores.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
