package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractBenedettisData {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5731";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		String mainUrl="https://www.benedettis.com/sucursales/";
		HashSet<String> dupliData=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		try {
			String baseHtml=U.getHTML(mainUrl);
			String optionsSec=U.getSectionValue(baseHtml, "<option value=\"\">", "</select>");
			String stateVals[]=U.getValues(optionsSec, "<option value=\"", "\"");
			String postUrl="https://www.benedettis.com/wp-admin/admin-ajax.php";
			for (String stateVal : stateVals) {
				String postData="action=guias&estado="+stateVal;
				String postHtml=U.sendPostRequest(postUrl, postData);
				U.log(postHtml);
				String listings[]=U.getValues(postHtml, "<div class=\"suc_item\">", "</a>");
				for (String listing : listings) {
					U.log(listing);
					String phone=U.getSectionValue(listing, "<p class=\"tel_store\">", "</p>");
					String state=U.getSectionValue(listing, "class=\"est_store\">", "</p>");
					String city=U.getSectionValue(listing, "lass=\"del_store\">", "</p>");
					String postalCode=U.getSectionValue(listing, "class=\"cp_store\">", "</p>");
					String colonia=U.getSectionValue(listing, "class=\"col_store\">", "</p>");
					String address=U.getSectionValue(listing, "class=\"cal_store\">", "</p>");
					String latlonSec=U.getSectionValue(listing, "/place/", "/");
					U.log(latlonSec);
					String latLon[]=latlonSec.split(",");
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase("Benedettis"),U.toTitleCase(address),U.toTitleCase(colonia),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(phone),null,"https://www.benedettis.com",null,null,null,null,null,null,latLon[0],latLon[1],"https://www.benedettis.com/sucursales/",U.getTodayDate()};
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Benedettis_Store.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}
