package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.text.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractGoMart {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5411";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		HashSet<String> dupliData=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String mainUrl="http://gomart.com.mx/";
		String srcUrl="http://gomart.com.mx/ubica-tu-tienda/";
		int totalListing=0;
		try {
			String srcHtml=U.getHTML(srcUrl);
			int i=0;
			String values[]=U.getValues(srcHtml, "<option class=\"level-0\" value=\"", "\"");
			for (String val : values) {
//				U.log(val);
				String listingJson=StringEscapeUtils.unescapeJson(U.getHTML("http://gomart.com.mx/wp-admin/admin-ajax.php?action=store_search&lat=0&lng=0&max_results=100&search_radius=50&filter="+val+"&autoload=1"));
				String listings[]=U.getValues(listingJson, "{\"add", "}");
//				U.log(listingJson);
				totalListing+=listings.length;
//				U.log(listings.length);
//				
				for (String listing : listings) {
					U.log(listing);
					String add[]= {"","","","",""};
					add[0]=U.getSectionValue(listing, "ress\":\"", "\"");
					add[2]=U.getSectionValue(listing, "\"city\":\"", "\"");
					add[3]=U.findState(U.getSectionValue(listing, "\"state\":\"", "\""));
					add[4]=U.getSectionValue(listing, "\"zip\":\"", "\"").replaceAll("C.P.|\\D+|\\s+", "");
					U.log(Arrays.toString(add));
					String phone=U.getSectionValue(listing, "\"phone\":\"", "\"");
					String lat=U.getSectionValue(listing, "\"lat\":\"", "\"");
					String lon=U.getSectionValue(listing, "\"lng\":\"", "\"");
					String fax=U.getSectionValue(listing, "\"fax\":\"", "\"");
					String email=U.getSectionValue(listing, "\"email\":\"", "\"");
					String url=U.getSectionValue(listing, "\"url\":\"", "\"");
					String name="go mart "+ U.getSectionValue(listing, "\"store\":\"", "\"");
					if (dupliData.add((name+add[0]).toLowerCase())) {
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),fax,mainUrl,email,null,null,null,null,"1984",lat,lon,srcUrl,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Gomart.csv", sw.toString());
			sw.close();
			writer.close();
			U.log("==>"+totalListing);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
