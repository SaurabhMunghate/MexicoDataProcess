package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;


@SuppressWarnings("deprecation")
public class ExtractUniversidaddelaCienegadelEstadodeMichoacandeOcampo {
	static String mainUrl="http://ucienegam.mx";
	static String deptPath[]= {"http://ucienegam.mx/index.php/directorio/"};
	final static String compnanyName="Universidad De La Cienega Del Estado De Michoacan De Ocampo";
	final static String mainFax="";
	final static String openedIn="2006";
	final static String empCount="";
	final static String[]add= {"Avenida Universidad Sur 3000","Lomas de Universidad","Sahuayo","Michoacán","59103"};
	static String mainPhone= "353-11-79600;353-53-20762;353-53-20913;353-53-20575";
	public static void main(String[] args) {
		try {
			final int MAXPAGE=13;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String mainHtml=U.getHTML(deptPath[0]);
			for (int i = 2; i <= MAXPAGE; i++) {
				mainHtml+=U.getHTML(deptPath[0]+"page/"+i+"/");
			}
			String listings[]=U.getValues(mainHtml, "<div class=\"img-wrap\">", "</article>");
			U.log(listings.length);
			for (String listing : listings) {
				//U.log(listing);
				String contactPersonName=U.getSectionValue(listing, "title=\"", "\"");
				String title=U.getSectionValue(listing, "<div class=\"staff-title\">", "</div>");
				U.log(title);
				U.log(contactPersonName);
				String detailUrl=U.getSectionValue(listing, "href=\"", "\"").trim();
				String detailHtml=U.getHTML(detailUrl);
//				U.log(detailHtml);
				String email=U.getSectionValue(detailHtml, "<a class=\"value\" href=\"mailto:", "\"");
				String Phone=U.getSectionValue(detailHtml, "<i class=\"icon-phone12\"></i><span class=\"value\">", "</span>");
				Phone=Phone==null?mainPhone:Phone;
				email=email==null?"":email;
				title=title==null?"":title;
				if (email.contains(",")) {
					email=email.replace(", ", ";");
				}
				U.log(email);
				U.log(contactPersonName);
				String out[]= {compnanyName,add[0],add[1],add[2],add[3],add[4],U.formatNumbersAsCode(Phone),"",mainUrl,email.toLowerCase(),contactPersonName,title,empCount,openedIn,"20.0142251","-102.7448456",deptPath[0],U.getTodayDate()};
				for (int i = 0; i < out.length; i++) {
					out[i]=U.removeHtml(StringEscapeUtils.unescapeHtml3(out[i]));
				}
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"UniversityData/"+compnanyName+".csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
