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

public class Extractitiztapalapa {
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
	static String mainUrl="http://www.iztapalapa.tecnm.mx/";
	static String deptPath[]= {"http://www.iztapalapa.tecnm.mx/directorio/"};
	final static String compnanyName="INSTITUTO TECNOLÓGICO DE IZTAPALAPA";
	final static String mainFax=null;
	final static String openedIn=null;
	final static String empCount=null;
	final static String[] add= {"Av. Telecomunicaciones S/N","Col. Chinampac De Juárez","Iztapalapa","Mexico City","09208"};
	final static String latLon[]= {"19.379729","-99.052816"};
	static String mainPhone= "555-773-8210";
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			int i=0;
			HashSet<String> dupliData=new HashSet<>();
			String directoryPage=U.getHTML(deptPath[0]);
			String listingSec=U.getSectionValue(directoryPage, "<section class=\"contenidopagina\">", "</section>");
			String listings[]=U.getValues(listingSec, "<tr>", "</tr>");
			U.log(listings.length);
			for (String list : listings) {
				if (list.contains("<b>Departamento</b>"))continue;
				String dataList[]=U.getValues(list, "<p align=\"left\">", "</p>");
				String contactPerson=dataList[1];
				String title=dataList[0];
				String email=dataList[2];
				email=email!=null?U.removeHtml(email.replaceAll("<br />|\n", ";").replace(";;", ";")):"";
				title=title!=null?U.removeHtml(title):"";
				contactPerson=contactPerson!=null?U.removeHtml(contactPerson):"";
				if (dupliData.add(contactPerson)) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(compnanyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),U.toTitleCase(add[4]),U.formatNumbersAsCode(mainPhone),null,mainUrl,email,U.toTitleCase(contactPerson),U.toTitleCase(title),null,null,null,latLon[0],latLon[1],deptPath[0],U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
				
//				break;
				
			}
//			U.log(listingSec);
			FileUtil.writeAllText(U.getCachePath()+"Instituto_Tecnologico_De_Iztapalapa.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
