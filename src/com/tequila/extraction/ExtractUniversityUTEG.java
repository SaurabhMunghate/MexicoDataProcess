package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractUniversityUTEG {
	static String mainUrl="http://www.uteg.edu.mx";
	static String deptPath[]= {"http://www.uteg.edu.mx/?page_id=13727"};
	final static String compnanyName="Centro Universitario UTEG";
	final static String mainFax="";
	final static String openedIn="1968";
	final static String empCount="";
	static String[]add= null;
//	static String mainPhone= "353-11-79600;353-53-20762;353-53-20913;353-53-20575";
	public static void main(String[] args) {
		try {
			loadAddressMap();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String baseHtml=U.getHTML(deptPath[0]);
			String dataSecs[]=U.getValues(baseHtml, "<div class=\"fw-team-name\">", "</noscript>");
			for (String dataSec : dataSecs) {
				U.log(dataSec);
				String contactName=U.getSectionValue(dataSec, "<h6>", "</h6>");
				String title=U.getSectionValue(dataSec, "<span>", "</span>");
				String phone=U.getSectionValue(dataSec, "<p class=\"street-address\" style=\"text-align: center;\">", "</p>");
				String email=U.removeHtml(U.getSectionValue(dataSec, "class=\"tel\" style=\"text-align: center;\">", "</p>"))	;
				String campusNameSec=U.getSectionValue(dataSec, "<div class=\"text-center\">", "<span");
				String campusAddKey=U.getSectionValue(dataSec, "href=\"http://www.uteg.edu.mx/?", "\"");
				U.log(campusAddKey);
				add=addressMap.get(campusAddKey);
				U.log(phone);
//				U.log(Arrays.toString());
				U.log(campusNameSec);
				String out[]= {compnanyName,add[0],add[1],add[2],add[3],add[4],U.formatNumbersAsCode(StringEscapeUtils.unescapeHtml4(phone.replace("/", ";"))),"",mainUrl,email.toLowerCase(),contactName,title,empCount,openedIn,add[5],add[6],deptPath[0],U.getTodayDate()};
				writer.writeNext(out);
//				break;
			}
//				
			FileUtil.writeAllText(U.getCachePath()+"UniversityData/"+compnanyName+".csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static HashMap<String, String[]> addressMap=new HashMap<>();
	private static void loadAddressMap() {
		
		addressMap.put("fw-portfolio=campus-2-2", new String[] {"Av. Héroes Ferrocarrileros 1325","La Aurora","Guadalajara","Jalisco","44460","20.653814","-103.345871"});
		addressMap.put("fw-portfolio=alcalde", new String[] {"Av. Fray Antonio Alcalde 788","Centro Barranquitas","Guadalajara","Jalisco","44280","20.684048","-103.352942"});
		addressMap.put("fw-portfolio=americas-2", new String[] {"Av. Américas No.425 Esq. Manuel Acuña","Ladrón de Guevara","Guadalajara","Jalisco","44600","20.684688","-103.373906"});
		addressMap.put("fw-portfolio=lazaro-cardenas", new String[] {"Calz. Lázaro Cárdenas 2275","Las Torres","Guadalajara","Jalisco","44920","20.656450","-103.379145"});
		addressMap.put("fw-portfolio=castillo", new String[] {"Legalidad No. 46","Lomas de Independencia","Guadalajara","Jalisco","44240","20.713580","-103.322259"});
		addressMap.put("fw-portfolio=pedro-moreno-2", new String[] {"Av Enrique Díaz de León Sur 80","Americana","Guadalajara","Jalisco","44160","20.675351","-103.359757"});
		addressMap.put("fw-portfolio=olimpica-2", new String[] {"Blvd. Gral. Marcelino García Barragán 1610","Atlas","Guadalajara","Jalisco","44870","20.652613","-103.326039"});
		addressMap.put("fw-portfolio=revolucion", new String[] {"Calle Manuel López Cotilla 759","Americana","Guadalajara","Jalisco","44160","20.673952","-103.356206"});
		addressMap.put("fw-portfolio=rio-nilo", new String[] {"Av Río Nilo 7546","Flores Magón","Tonalá","Jalisco","45403","20.634298","-103.272144"});
		addressMap.put("fw-portfolio=zapopan-2-2", new String[] {"Av. José Parres Arias 145","Zapopan Centro","Zapopan","Jalisco","45100","20.729842","-103.388654"});
		addressMap.put("fw-portfolio=cruz", new String[] {"Av Cruz del Sur 3328","Santa Eduwiges","Guadalajara","Jalisco","44580","20.638314","-103.394537"});
	}
}
