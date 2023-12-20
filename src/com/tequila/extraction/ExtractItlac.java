package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractItlac {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="8221";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	static String mainUrl="https://web.itlac.mx/";
	static String deptPath[]= {"https://web.itlac.mx/institucion/directorio-telefonico/"};
	final static String compnanyName="Instituto Tecnológico De Lázaro Cárdenas";
	final static String mainFax="";
	final static String openedIn="1987";
	final static String empCount=null;
	final static String[] add= {"Av. Melchor Ocampo No. 2555","Cuarto Sector","Lázaro Cárdenas","Michoacán","60950"};
	final static String latLon[]= {"21.109249","-101.628048"};
	static String mainPhone= "753-537-1977;753-532-1040;753-537-5391;753-537-5392";
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			int i=0;
			HashSet<String> dupliData=new HashSet<>();
			String directoryPage=U.getHTML(deptPath[0]);
			String listingSec=U.getSectionValue(directoryPage, "<table border=\"2\" width=\"100%\">", "</tbody>");
			String listings[]=U.getValues(listingSec, "<tr", "</tr>");
			U.log(listings.length);
			for (String list : listings) {
				if (list.contains("RESPONSABLE</strong>"))continue;
				String dataList[]=U.getValues(list, "<td", "</td>");
				if (dataList.length<2) {
					continue;
				}
				
				String contactPerson=dataList[1].replace("style=\"width: 56%;\">", "");;
				String title=dataList[2].replace("style=\"width: 29%;\">", "");
				String email=dataList[3].replaceAll("style=\"width: 10%;\">|<p> </p>", "");
				String phone=mainPhone;
//				U.log(contactPerson);
//				U.log(title);
				if (email==null||email.trim().length()<5) {
					continue;
				}
				U.log(email);
				U.log(email.trim().length());
				email=decodeEmailSpamBot(email);
				title=title!=null?U.removeHtml(title):"";
				contactPerson=contactPerson!=null?U.removeHtml(contactPerson):"";
				if (email==null||email.trim().length()==0||contactPerson==null||contactPerson.trim().length()==0) {
					continue;
				}
				if (dupliData.add(contactPerson)) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(compnanyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),U.toTitleCase(add[4]),U.formatNumbersAsCode(phone),null,mainUrl,email,U.toTitleCase(contactPerson),U.toTitleCase(title),null,null,null,latLon[0],latLon[1],deptPath[0],U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
				
//				break;
				
			}
//			U.log(listingSec);
			FileUtil.writeAllText(U.getCachePath()+"Instituto_Tecnologico_De_Lazaro_Cardenas.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private static String decodeEmailSpamBot(String emailSec){
		emailSec=org.apache.commons.text.StringEscapeUtils.unescapeHtml4(emailSec);
//		U.log(emailSec);
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
