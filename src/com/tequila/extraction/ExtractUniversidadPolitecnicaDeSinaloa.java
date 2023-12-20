package com.tequila.extraction;

import java.io.StringWriter;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractUniversidadPolitecnicaDeSinaloa {
	static HashSet<String> contactPerson = new HashSet<>();
	final static String companyName = "Universidad Politecnica de Sinaloa";
	final static String mainUrl = "http://www.upsin.edu.mx/";
	final static String empCount = "";
	final static String openedIn = "2004";
	final static String mainFax = "";
	final static String phoneMain = "669-180-0695";
	final static String add[]= {"Carretera Municipal Libre Mazatlán Higueras Km 3","Genaro Estrada Calderon","Mazatlan","Puebla","72810"};
	public static void main(String[] args) {
		try {
			String adminHtml=U.getHTML("http://itculiacan.edu.mx/directorio/");
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String dataSec=U.getSectionValue(adminHtml, "Correo Electrónico</strong>", "</tbody>");
			String data[]=U.getValues(adminHtml, "<tr>", "</tr>");
			for (String d : data) {
//				if (!d.contains("@")) {
//					continue;
//				}else {
//					d+=".mx</p>";
//				}
//				String details[]=U.getValues(d, "<p>", "</p>");
//				if (details.length==3) {
					String name=U.getSectionValue(d, "<td valign=\"top\" width=\"174\">", "</td>");
					String email=U.getSectionValue(d, "data-cfemail=\"", "\"");
					String pos=U.getSectionValue(d, "<td style=\"padding-left: 30px;\" valign=\"top\" width=\"189\">", "</td>");
					String phone="6677133804 Ext. "+U.getSectionValue(d, "<td valign=\"top\" width=\"35\">", "</td>");
//					U.log(email);
					if (U.isEmpty(email)||U.isEmpty(name)) {
						continue;
					}
					String out[]= {U.toCapitalizeCase(name.trim()),U.toCapitalizeCase(pos.trim()),email.trim().toLowerCase(),U.formatNumbersAsCode(phone.trim())};
					//U.log(d);
					writer.writeNext(out);
//				}
				
				//break;
			}
			FileUtil.writeAllText("/home/mypremserver/MexicoCache/Cache/Instituto Tecnologico de Culiacan_temp.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
