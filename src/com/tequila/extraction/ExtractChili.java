package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractChili {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5812";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			int i=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="http://www.chilis.com.mx/sucursales";
			String baseHtml=U.getHTML(baseUrl);
//			U.log(baseHtml);
			String dataSec=U.getSectionValue(baseHtml, "<div id=\"lista-estados\">", "</div>");
//			U.log(dataSec);
			String dataVal[]=U.getValues(dataSec, "<li>", "</li>");
			U.log(dataVal.length);
			for (String urlSec : dataVal) {
//				U.log(urlSec);
				String stateUrl=U.getSectionValue(urlSec, "href=\"", "\"");
//				U.log(stateUrl);
				String stateHtml=U.getHTML(stateUrl);
				String restaurantListSec=U.getSectionValue(stateHtml, "<div id=\"restaurant-selector\">", "<div id=\"content\">");
//				U.log(restaurantListSec);
				String restaurantSecs[]=U.getValues(restaurantListSec, "<li>", "</li>");
				for (String restaurants : restaurantSecs) {
//					
					String name=U.getSectionValue(restaurants, "<h3>", "</h3>");
					if (name==null) {
						name=U.getSectionValue(restaurants, "alt=\"", "\"");
					}
					if (name==null) {
						name=U.getSectionValue(restaurants, "<h3>", "</H3>");
					}
					if (name==null) {
						name=U.getSectionValue(restaurants, "<H3>", "</H3>");
					}
					
//					if (!name.contains("Chili's Culiacán")) {
//						continue;
//					}
					U.log(restaurants);
					U.log(name);
					String lat=U.getSectionValue(restaurants, "data-gmap-lat=\"", "\"");
					String lng=U.getSectionValue(restaurants, "data-gmap-long=\"", "\"");
					String address=U.getSectionValue(restaurants, "<div class=\"address\">", "</div>");
					String phoneSec=U.getSectionValue(restaurants, "<div class=\"phone\">", "</table>");
					if (phoneSec==null) {
						phoneSec=U.getSectionValue(restaurants, "<td><b>Teléfono:</b>", "</td>");
						if (phoneSec!=null) {
							phoneSec=phoneSec.replaceAll("y|ó", ";");
						}else {
							phoneSec="";
						}
						U.log(phoneSec);
					}
					String phones[]=U.getValues(phoneSec, "<td width=\"", "</td>");
					String phone="";
					
					for (String p : phones) {
						phone+=p.trim()+";";
						//U.log(phone);
						phone=U.removeHtml(phone.trim().replaceAll("\\d+%\">", ""));
					}
					while (phone.endsWith(";")) {
//						U.log("--=-=-=--"+phone);
						phone=phone.trim().substring(0,phone.trim().length()-1);
					}
					if (phoneSec.length()>5&&phone.length()<5) {
						phone=phoneSec;
					}
					String email=Util.match(restaurants, "[_a-zA-Z1-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*");
					U.log(email);
					U.log("--=-=-=--"+phone);
					if (phone.contains("<div")) {
						phone=U.getSectionValue(phone, "<br>", "</div>");
						if (phone==null) {
							phone=U.getSectionValue(phoneSec, "<td>", "</td>");
						}
					}
					
					//U.log(phone);
					String add[]=U.getAddress(address.replace("<br>", ",").replaceAll("\n", ","));
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(StringEscapeUtils.unescapeJava(U.removeHtml(add[0]))),U.toTitleCase(StringEscapeUtils.unescapeJava(add[1])),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],U.formatNumbersAsCode(U.removeHtml(phone)),null,"http://www.chilis.com.mx",email,null,null,null,null,"1992",lat,lng,baseUrl,U.getTodayDate(),StringEscapeUtils.unescapeJava(address).trim()};
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Chilis_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
