package com.tequila.extraction;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.sym.Name;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractFiraGob {
	public static void main(String[] args) {
		//new ExtractFiraGob().ExtractData();
		new ExtractFiraGob().formatData();
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void formatData() {
		try {
			String fileName="/home/mypremserver/MexicoCache/Cache/Fira.csv";
			List<String[]> input=U.readCsvFile(fileName);
			int counter=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			for (String[] record : input) {
				if (record[0].contains("SIC_SUB"))continue;
				String []sicDetails=Sic.sicInfo(record[0]);
				U.log(record[0]);
				String out[]= 
					{
						""+counter++,//id
						U.toCapitalizeCase(sicDetails[0]),
						U.toCapitalizeCase(sicDetails[1]),
						U.toCapitalizeCase(sicDetails[3]),
						U.toCapitalizeCase(sicDetails[4]),
						U.toCapitalizeCase(sicDetails[5]),
						U.toCapitalizeCase(sicDetails[6]),
						U.toCapitalizeCase(record[1]),
						U.toCapitalizeCase(record[2]),
						U.toCapitalizeCase(record[3]),
						U.toCapitalizeCase(record[4]),
						U.toCapitalizeCase(record[5]),
						U.toCapitalizeCase(record[6]),
						U.formatNumbersAsCode(record[7]),
						null,
						record[8].toLowerCase(),
						null,
						U.toCapitalizeCase(record[9]),
						null,
						null,
						null,
						null,
						record[10],
						record[11],
						record[12],
						U.getTodayDate()
					};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_Unique_INSERT.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void ExtractData() {
		try {
			ArrayList<String []> data=new ArrayList<>();
			HashMap<String, String[]>phoneData=new HashMap<>();
			String baseUrl="https://www.fira.gob.mx/OficinasXML/OficinasDireccion.jsp";
			String basehtml=U.getHTML(baseUrl);
			String values[]=U.getValues(basehtml, "<option value=\"", "\"");
			for (String val : values) {
				if (val.equals("0")) continue;
				String extractionUrl="https://www.fira.gob.mx/OficinasXML/CheckOficina.jsp?Of="+val;
				String listinhhtml=U.getHTML(extractionUrl);
				//U.log(listinhhtml.replaceAll("\\s{2,}", " "));
				String listingData[]=U.getValues(listinhhtml.replaceAll("\\s{2,}", " "), "<div class=\"panel panel-default\">", "</div> </div>");
				for (String listings : listingData) {
//					U.log(listings);
					String phoneSec=U.getSectionValue(listings, "<p class=\"list-group-item-text\">TEL.", "</p>");
					String name=U.getSectionValue(listings, "<strong>", "</strong>");
					String contact_Name=U.getSectionValue(listings, "<p class=\"list-group-item-text\"><strong>", "</strong>");
					if (contact_Name.contains("Oficina Central")) {
						contact_Name="";
					}
					String[]pData= {phoneSec,contact_Name};
					phoneData.put(name.trim(), pData);
				}
				//break;
			}
			String[] dataUrls= {
					"https://www.fira.gob.mx/Nd/regional.json",
					"https://www.fira.gob.mx/Nd/agencia.json",
					"https://www.fira.gob.mx/Nd/residencia.json",
					"https://www.fira.gob.mx/Nd/CENTRAL.json",
					"https://www.fira.gob.mx/Nd/CDTs.json"
					};
			String header[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","Neighbourhood","CITY","STATE","ZIP","PHONE","URL","CONTACT_PERSON","LATITUDE","LONIGTUDE","SOURCE_URL"};
			data.add(header);
			for (int i = 0; i < dataUrls.length; i++) {
				String html=U.getHTML(dataUrls[i]);
				String datas[]=U.getValues(html, "{", "}}");
				for (String listing : datas) {
					U.log("-=-=-=-=--=--=-=-=-=-==========");
					String listingName=U.getSectionValue(listing, "\"name\":\"", "\"");
					U.log(listingName);
					String addSec=U.getSectionValue(listing, "<p ", "</div>");
					addSec=StringEscapeUtils.unescapeHtml4(U.getSectionValue(listing, "<br/>", "</p>"));
					String latLon[]= {"",""};
					latLon[0]=Util.match(listing, "\"lat\":\\d{2,}.\\d+").replace("\"lat\":", "");
					latLon[1]=Util.match(listing, "\"lng\":-\\d{2,}.\\d+").replace("\"lng\":", "");
					U.log(latLon[0]);
					U.log(latLon[1]);
					
					String postalCode=Util.match(addSec, "<br/>\\d{4,5}");
					addSec=addSec.replace(postalCode, "");
					U.log(addSec);
					String tempadd[]=addSec.replace("<br/>", ",").split(",");
					String streetAdd="";
					String city="",state="";
					for (int j = 0; j < tempadd.length-2; j++) {
						streetAdd+=tempadd[j]+", ";
					}
					city=tempadd[tempadd.length-2];
					//U.log(tempadd[tempadd.length-1]);
					state=MXStates.getFullNameFromAbbr(tempadd[tempadd.length-1].trim());
					if (state==null&&tempadd[tempadd.length-1].trim()!=null) {
						state=tempadd[tempadd.length-1].trim().replace(".", "");
					}
					U.log(streetAdd.substring(0, streetAdd.length()-2));
					U.log(state);
					U.log(city);
					postalCode=postalCode.replace("<br/>", "");
					U.log(postalCode);
					String pData[]=phoneData.get(listingName);
					String phone=pData[0]!=null?pData[0].trim():"";
					String contactPerson=pData[1]!=null?pData[1].trim():"";
					String out[]= {"6111","Fira "+listingName,streetAdd,"",city,state,postalCode,phone.trim(),"https://www.fira.gob.mx",contactPerson,latLon[0],latLon[1],"https://www.fira.gob.mx/OficinasXML/OficinasDireccion.jsp"};
					data.add(out);
				}
				//break;
			}
			
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeAll(data);
			FileUtil.writeAllText(U.getCachePath()+"Fira.csv", sw.toString());
			writer.close();
			sw.close();
			U.log(data.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
