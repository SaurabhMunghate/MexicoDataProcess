package com.tequila.extraction;

import java.io.StringWriter;
import java.util.HashSet;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractHandm {
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
		try {
			HashSet<String> uniqueKey=new HashSet<>();
//			String webSite="https://www.hm.com/mx/";
			String companyName="H & M";
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int id=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="https://www.hm.com/mx/store-locator/mexico/";
			String baseHtml=U.getHTML(baseUrl);
			String listings[]=U.getValues(baseHtml, "<div class=\"store-dropdown bt", "</span>Get directions</a>");
			for (String lisiting : listings) {
				String storeURl=U.getSectionValue(lisiting, "</table><a href=\"", "\"");
//				if (!storeURl.contains("https://www.hm.com/mx/store-locator/mexico/benito-juarez-narvarte/parque-delta/"))continue;
				U.log(lisiting);
				U.log(storeURl);
				String storeHtml=U.getHTML(storeURl);
				String lat=U.getSectionValue(storeHtml, "lat:", ",");
				String lon=U.getSectionValue(storeHtml, "lng:", "}");
				String addressSec=U.getSectionValue(storeHtml, "<p>", "</p>");
				U.log(addressSec);
				String add[]= {"","","","",""};
				if (addressSec!=null) {
					addressSec=addressSec.replaceAll("<br>Mexico", "").replace("<br>", ", ");
					add=U.getAddress(addressSec);
					add[3]=U.findStateFromZip(add[4]);
				}
				String phone=U.getSectionValue(storeHtml, "icon icon-phone\"></span>", "</a>");
				U.log(addressSec);
				if(uniqueKey.add((companyName+add[0]).toLowerCase())){
					String out[] = {""+(id++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],U.formatNumbersAsCode(phone),null,storeURl,null,null,null,null,null,null,lat,lon,baseUrl,U.getTodayDate()};
					writer.writeNext(out);
				}
			}
			FileUtil.writeAllText(U.getCachePath()+"H_and_M_Stores_2.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
