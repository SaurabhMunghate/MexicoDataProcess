package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.NiceRefreshHandler;
import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractElPorton {
	public static void main(String[] args) {
		extractData();
	}

	private static void extractData() {
		try {
			String baseUrl="http://porton.com.mx/wp-content/themes/elporton/ajax/getAllRestaurants.php";
			String baseHtml=U.getHTML(baseUrl);
//			U.log(baseHtml);
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String listings[]=U.getValues(baseHtml, "{\"name\":", "}");
//			U.log(listings.length);
			String header[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBOURHOOD","CITY","STATE","ZIP","PHONE","LATITUDE","LONGITUDE"};
			writer.writeNext(header);
			for (String listing : listings) {
				listing=StringEscapeUtils.unescapeJava(listing);
				U.log(listing);
				String storeName=U.getSectionValue(listing, "\"", "\"");
				String streetAdd=U.getSectionValue(listing, "\"street\":\"", "\"");
				String negih=U.getSectionValue(listing, "\"colony\":\"", "\"");
				String postalCode=Util.match(negih, "\\d{4,5}");
				negih=negih.replaceAll("\\d{4,5}", "").replaceAll("\\s+$", "").replaceAll(",$", "");
				String neighState[]=null;
				
				String phone=U.getSectionValue(listing, "\"delegation\":\"", "\"");
				if (negih.contains(",")) {
					neighState=negih.split(",");
				}else {
					neighState=new String[] {negih,phone};
					phone="";
				}
				String lat=U.getSectionValue(listing, "\"latitude\":\"", "\"");
				String lng=U.getSectionValue(listing, "\"longitude\":\"", "\"");
				String out[]= {"5812",storeName,streetAdd,neighState[0].trim(),neighState[1].trim(),"",postalCode,phone,lat,lng};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"El_Porton.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
