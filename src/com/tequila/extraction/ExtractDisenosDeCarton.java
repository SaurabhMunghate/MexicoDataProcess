package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractDisenosDeCarton {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="2653";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
//	static String categoriesMAin[]= {"eat-drink",
//			"restaurant",
//			"snacks-fast-food",
//			"bar-pub",
//			"coffee-tea",
//			"coffee",
//			"tea",
//			"hotel",
//			"motel"};
	
	private static void extractData() {
		
		HashSet<String> dupliData=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String mainUrl="https://www.disenosdecarton.com.mx/";
		String srcUrl="https://www.disenosdecarton.com.mx/sucursales";
		try {
			String srcHtml=U.getHTML(srcUrl);
			String stateLists[]=U.getValues(srcHtml, "<a class=\"uk-position-cover uk-position-z-index\" href=\"", "\"");
			int i=0;
			for (String stateList : stateLists) {
				U.log((mainUrl+stateList).replace("mx//", "mx/"));
				String stateHtml=U.getHTML((mainUrl+stateList).replace("mx//", "mx/"));
//				U.log(stateHtml);
				
				String listings[]=U.getValues(U.removeComments(stateHtml), "<div class=\"uk-width-1-1 uk-width-medium-1-3 uk-width-large-1-3\">", "</div>");
				U.log(listings.length);
//				break;
				for (String listing : listings) {
					U.log(listing);
					if (listing.contains("uppercase\">&nbsp;</h5>")) {
						continue;
					}
					String addSec=U.getSectionValue(listing, "<i class=\"uk-text-danger uk-icon-map-marker\">", "<i class=\"uk-text-danger uk-icon-phone-square\"></i>");
					String add[]= {"","","","",""};
					if (addSec!=null) {
						addSec=addSec.replace("</i>&nbsp;&nbsp;Dirección<br />", "").replace("<br />", ",");
						add=U.getAddress(addSec);
//						U.log(Arrays.toString(add));
					}
					String phone=U.getSectionValue(listing, "Teléfono<br />", "<br />");
					U.log(phone);
					String email=URLDecoder.decode(U.getSectionValue(listing, "href=\"mailto:", "\""),StandardCharsets.UTF_8.toString()).trim();
					String name=U.getSectionValue(listing, "\"uk-text-uppercase\">", "</h5>");
					U.log(name);
					U.log(email);
					String lat=Util.match(listing, "d\\d{2}\\.\\d+");
					String lon=Util.match(listing, "d-\\d{2,3}\\.\\d+");
					
					if(dupliData.add((add[0]+add[1]+add[2]+add[3]+phone).toLowerCase())){
						if (lat.contains("d")) {
							lat=lat.replaceAll("d", "");
							lon=lon.replaceAll("d", "");
						}
						U.log(lat+"  "+lon);
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase("Diseños De Cartón, S.A. De C.V."),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,mainUrl,email,null,null,null,null,null,lat,lon,srcUrl,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
//					U.log(addSec);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Disenos_De_Carton.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
