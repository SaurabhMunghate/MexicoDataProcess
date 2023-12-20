package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractFarmaciasUnion {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5912";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> uniqueSet=new HashSet<>();
			String website="http://www.farmaciasunion.com/";
			String baseUrl="http://www.farmaciasunion.com/Pagina/Html/46";
			String baseHtml=U.getHTML(baseUrl);
			String html="";
			for (int j = 1; j < 22; j++) {
				html+=U.getHTML("http://www.farmaciasunion.com/Pagina/Sucursales?serv_recargaselectronicas=False&serv_pagodeservicios=False&serv_domicilio=False&serv_24horas=False&serv_cajeroautomatico=False&serv_consultorio=False&serv_mastv=False&serv_multijuegos=False&CurrentPage="+j+"&MaxPage=21&Sucursales=System.Collections.Generic.List%601%5BFUnion.Web.Models.sucursale%5D");
				
			}
			String listings[]=U.getValues(html, "<div id=\"sucursal-1", "</table>");
			U.log(listings.length);
			for (String listing : listings) {
				listing=StringEscapeUtils.unescapeHtml3(listing);
				U.log("-----\n"+listing);
				String companyName=U.getSectionValue(listing, "font-size:17px; \">", "</p>");
				U.log(companyName);
				String addresSec="<br />"+U.getSectionValue(listing, "<strong>Dirección</strong><br>", "Horario:").replaceAll("<br />\n", "<br>\n<br />");
				String addressVals[]=U.getValues(addresSec, "<br />", "<br>");
				String add[]= {"","","","",""};
				String phone="";
//				U.log(addresSec);
				for (String address : addressVals) {
					if (address.contains("Teléfono")) {
						phone=address.replace("Teléfono: ", "").trim();
					}else if (address.contains("Colonia")) {
						add[1]=address.replace("Colonia: ", "").trim();
					}else if (address.contains("C.P.")) {
						add[4]=address.replace("C.P.", "").trim();
					}else if (!U.findState(address).equals("-")&&address.contains(",")){
						U.log("--"+U.findState(address));
						String cityState[]=address.split(",");
						add[2]=cityState[0].trim();
						add[3]=cityState[1].trim();
//						U.log(address);
					}else {
						add[0]=add[0]+" "+address.trim();
					}
				}
				String latLonSec=" , ";
				if (listing.contains("<iframe width=\"252\"")) {
					latLonSec=U.getSectionValue(listing, "&ll=", "&spn");
				}
				
//				U.log(latLonSec);
				String latLon[]=latLonSec.split(",");
				if (add[0].startsWith(" ")) 
					add[0]=add[0].substring(1, add[0].length());
				U.log(Arrays.toString(add));
				U.log(phone);
				if (uniqueSet.add((companyName+add[0]).toLowerCase())) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,latLon[0],latLon[1],website,U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
//				U.log(addresSec);
				FileUtil.writeAllText(U.getCachePath()+"FarmaciasUnion_Branches.csv", sw.toString());
				sw.close();
				writer.close();
//				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
