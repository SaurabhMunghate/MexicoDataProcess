package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractADIACData {
	private static String baseURl="http://www.adiacjalisco.org.mx/index.php/socios-adiac-jalisco";
	private static HashSet<String> data=new HashSet<>();
	public static void main(String[] args) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String baseHtml=U.getHTML(baseURl);
			extractData(baseHtml);
			U.log(data.size());
			String header[]={"Sic_Sub","companyName","streetAdd","Neighborhood","city","state","zip","phone","fax","Website","email","lat","Lon"};
			writer.writeNext(header);
			for (String record : data) {
//				U.log(record);
				String companyName=U.getSectionValue(record, "title=\"", "\"");
				String detailUrl=U.getSectionValue(record, "<a href=\"", "\"");
				String detailHtml=U.getHTML("http://www.adiacjalisco.org.mx"+detailUrl);
				String streetAdd=U.getSectionValue(detailHtml, "<strong>Calle: </strong>", "</li>");
				String city=U.getSectionValue(detailHtml, "<strong>Ciudad: </strong>", "</li>");
				String state=U.getSectionValue(detailHtml, "<strong>Estado: </strong>", "</li>");
				String zip=U.getSectionValue(detailHtml, "<strong>C.P.: </strong>", "</li>");
				String phone=U.getSectionValue(detailHtml, "<strong>Teléfono: </strong>", "</li>");
				String fax=U.getSectionValue(detailHtml, "<strong>Fax: </strong>", "</li>");
				String email=decodeEmailSpamBot(U.getSectionValue(detailHtml, "<strong>Correo: </strong>", "</script>"));
				String latLon[]= {U.getSectionValue(detailHtml, "\"lat\":\"", "\""),U.getSectionValue(detailHtml, "\"lng\":\"", "\"")};
				String website=U.getSectionValue(detailHtml, "<strong>Website: </strong><a href=\"", "\"");
				String out[]={"",companyName,streetAdd,"",city,state,zip,U.formatNumbersAsCode(phone==null?"":phone),U.formatNumbersAsCode(fax==null?"":fax),website,email,latLon[0],latLon[1],"http://www.adiacjalisco.org.mx"+detailUrl};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"ADIAC_12.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void extractData(String html) throws IOException {
		String dataSecs[]=U.getValues(html, "<div class=\"teaser-item\">", "Leer más</a></span>");
		for (String da : dataSecs) {
			data.add(da);
		}
		String nextSecUrl=U.getSectionValue(html, "class=\"next\" href=\"", "\"");
		if (nextSecUrl!=null) {
			nextSecUrl="http://www.adiacjalisco.org.mx"+nextSecUrl;
			html=U.getHTML(nextSecUrl);
			extractData(html);
		}
	}
	private static String decodeEmailSpamBot(String emailSec){
		emailSec=org.apache.commons.lang3.StringEscapeUtils.unescapeHtml3(emailSec);
		//U.log(emailSec);
		String textSec=U.getSectionValue(emailSec, "addy_text", "document");
		String emailStrArr[]=U.getValues(textSec.replace("+", ""), " '", "'");
		//U.log(Arrays.toString(emailStrArr));
		String email="";
		for (String e : emailStrArr) {
			email+=e;
		}
		//U.log(textSec);
		return email;
	}
}
