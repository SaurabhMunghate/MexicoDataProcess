package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractHonda {
	public static void main(String[] args) {
		//extractdata();
		formatData();
	}
	
	private static void formatData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String fileName="/home/mypremserver/DatabasesTequila/Priyanka/Honda_Distributor_Unique.csv";
			List<String[]> fileData=U.readCsvFile(fileName);
			for (String[] records : fileData) {
				U.log(Arrays.toString(records));
				if (records[0].contains("ID")) {
					continue;
				}
				String sicdetails[]=Sic.sicInfo(records[4]);
				if (records[4].contains("5511")) {
					sicdetails[6]="Nuevos Y Usados Concesionarios De Coches Y Camiones";
				}
				String out[]= {records[0],U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(records[7]),U.toTitleCase(records[8]),U.toTitleCase(records[9]),U.toTitleCase(records[10]),U.toTitleCase(records[11]),U.toTitleCase(records[12]),U.formatNumbersAsCode(records[13]),U.formatNumbersAsCode(records[14]),records[15].trim().toLowerCase(),records[16].trim().toLowerCase(),U.toTitleCase(records[17]),U.toTitleCase(records[18]),records[19],records[20],records[21],records[22],records[23],records[24],records[25]};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_Formatted.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractdata() {
		try {
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			
			String baseURl="https://www.honda.mx/contacto.php";
			String baseHtml=U.getHTML(baseURl);
			String optionsSec=U.getSectionValue(baseHtml, "<select name=\"dealer[section]\"", "</select>");
			String typeOptions[]=U.getValues(optionsSec, "<option value=\"", "\">");
			//U.log(optionsSec);
			int i=0;
			for (String typeOption : typeOptions) {
				if (typeOption.isEmpty()) continue;
				U.log(typeOption);
				String typeUrl="https://www.honda.mx/formularios/dealers.php?accion=estados&seccion="+typeOption;
				
				String stateListHtml=U.getHTML(typeUrl);
				String stateUrl="https://www.honda.mx/formularios/dealers.php";
				String stateoptions[]=U.getValues(stateListHtml, "\"id\":\"", "\"");
				for (String stateOption : stateoptions) {
					String pageLoad="accion=concesionarioEstados&seccion="+typeOption+"&opcion="+stateOption;
//					U.log(pageLoad);
					String listingHtml=StringEscapeUtils.unescapeJson(U.sendPostRequest(stateUrl, pageLoad));
//					U.log(listingHtml);
					String listings[]=U.getValues(listingHtml, "{\"id\":\"", "}");
//					U.log(listings.length);
					
					for (String listing : listings) {
						U.log(listing+"\n"+typeOption);
						String name=U.getSectionValue(listing, "\"razon\":\"", "\"");
						String streetAdd=U.getSectionValue(listing, "\"direccion\":\"", "\"");
						if (!dupliData.add((name+streetAdd).toLowerCase())) {
							continue;
						}
						String mexicdesc="";
						String neighbAdd=U.getSectionValue(listing, "\"colonia\":\"", "\"");
						String city=U.getSectionValue(listing, "\"ciudad\":\"", "\"");
						if (typeOption.trim().equals("4")) {
							sicCode="5571";
						}else if (typeOption.trim().equals("3")) {
							sicCode="5551";
						}else if (typeOption.trim().equals("5")) {
							sicCode="5251";
						}else {
							sicCode="5511";
						}
						String sicdetails[]=Sic.sicInfo(sicCode);
						if (sicCode.contains("5511")) {
							sicdetails[6]="Nuevos Y Usados Concesionarios De Coches Y Camiones";
						}
						
						String state=U.getSectionValue(listing, "\"estado\":\"", "\"");
						String zip=U.getSectionValue(listing, "\"cp\":\"", "\"");
						if (state.toLowerCase().equals("Cdmx".toLowerCase())) {
							state="Mexico City";
							city=MXStates.getMexicoCityMunicipalites(zip);
							U.log(name+" "+streetAdd);
							if (city==null&&zip.startsWith("53")) {
								state="Mexico State";
								city="Naucalpan de Juárez";
							}else if (city==null&&zip.startsWith("56")) {
								state="Mexico State";
								city="Texcoco de Mora";
							}else if (city==null&&zip.startsWith("97900")) {
								state="Yucatán";
								city="Mama";
							}
							U.log(city+zip);
						}else if (state.toLowerCase().contains("Estado De México".toLowerCase())) {
							state="Mexico State";
						}
						
						String phone=U.getSectionValue(listing, "\"tel\":\"", "\"");
						String fax=U.getSectionValue(listing, "\"fax\":\"", "\"");
						String email=U.getSectionValue(listing, "\"mail\":\"", "\"");
						if (email.contains(",")) {
							email=email.replace(",", ";");
						}
						String website=U.getSectionValue(listing, "\"web\":\"", "\"");
						String lat=U.getSectionValue(listing, "\"latitud\":\"", "\"");
						String lng=U.getSectionValue(listing, "\"longitud\":\"", "\"");
						U.log(state);
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(neighbAdd),U.toTitleCase(city),U.toTitleCase(state),zip.trim(),U.formatNumbersAsCode(phone),U.formatNumbersAsCode(fax),website,email,null,null,null,null,null,lat,lng,baseURl,U.getTodayDate()};
						U.log(Arrays.toString(out));
						writer.writeNext(out);
//						break;
					}
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Honda_Distributor.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
