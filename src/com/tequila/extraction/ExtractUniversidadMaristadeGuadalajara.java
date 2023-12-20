package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractUniversidadMaristadeGuadalajara {
	static String mainUrl="http://umg.edu.mx";
	static String deptPath[]= {"https://umg.edu.mx/portal/directorio/"};
	final static String compnanyName="Universidad Marista de Guadalajara";
	final static String mainFax="";
	final static String openedIn="2004";
	final static String empCount="";
	final static String[]add= {"Calle Marcelino Champagnat 2981","Loma Bonita","Zapopan","Jalisco","45086"};
	static String mainPhone= "333-540-3900";
	public static void main(String[] args) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String mainHtml=U.getHTML(deptPath[0]);
			String listings[]=U.getValues(mainHtml, "<!-- .et_pb_toggle -->", "<!-- .et_pb_toggle_content -->");
			U.log(listings.length);
			for (String listing : listings) {
				U.log("-==-=--=-==--=-=-=");
				if (listing.contains("<h5 class=\"et_pb_toggle_title\">INFORMES Y ADMISIONES. </h5>"))continue;
				U.log(listing);
				String contactPersonName=U.getSectionValue(listing, "|", "|");
				String title="";
				U.log(title);
				U.log(contactPersonName);
//				U.log(detailHtml);
				String email=U.getSectionValue(listing, "href=\"mailto:", "\"");
				String Phone=U.getSectionValue(listing, "<p><b>", "</b>");
				if (Phone==null) {
					Phone=U.getSectionValue(listing, "<p><strong>", "</strong>");
				}
				Phone=Phone==null?mainPhone:mainPhone+" Ext. "+Phone;
				email=email==null?"":email;
				title=title==null?"":title;
				if (email.contains(",")) {
					email=email.replace(", ", ";");
				}
				U.log(email);
				U.log(contactPersonName);
				String out[]= {compnanyName,add[0],add[1],add[2],add[3],add[4],U.formatNumbersAsCode(Phone),"",mainUrl,email.toLowerCase(),contactPersonName,title,empCount,openedIn,"20.6340162","-103.4074073",deptPath[0],U.getTodayDate()};
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
