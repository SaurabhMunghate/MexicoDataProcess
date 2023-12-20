package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractGrupoIndustrialBonasa {
	private static String urls[]= {
			"http://grupoindustrialbonasa.com.mx/centros-de-servicio.html",
			"http://grupoindustrialbonasa.com.mx/quieres-ser-distribuidor-bonasa.html",
			"http://grupoindustrialbonasa.com.mx/directorio-comercial.html"
			};
	public static void main(String[] args) {
		//extractData();
		formatData();
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void formatData() {
		try {
			String fileName="/home/mypremserver/MexicoCache/Cache/GrupoIndustrialBonasa.csv";
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			
			List<String[]> records=U.readCsvFile(fileName);
			int i=0;
			for (String[] record : records) {
				if (record[0].contains("SIC_SUB")) {
					continue;
				}
				String sicdetails[]=Sic.sicInfo(record[0]);
				U.log(sicdetails.length);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(record[1]),U.toTitleCase(record[2]),U.toTitleCase(record[3]),U.toTitleCase(record[4]),U.toTitleCase(record[5]),record[6],U.formatNumbersAsCode(record[7]),U.formatNumbersAsCode(record[8]),record[9],"",U.toTitleCase(record[10]),U.toTitleCase(record[11]),"","","",record[12],record[13],record[14],U.getTodayDate()};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_INSERT.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String header[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBOURHOOD","CITY","STATE","CODIGO_POSTAL","PHONE","WEBSITE","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE"};
			writer.writeNext(header);
			int count=0;
			String pageHtml=U.getHTML(urls[0]);
			//U.log(pageHtml);
			String secs[]=U.getValues(pageHtml, "['<h3>", "]");
			for (String dataSec : secs) {
				count++;
				U.log(dataSec);
				String comName=U.getSectionValue(dataSec, "\\\">", "</a>");
				U.log(comName);
				String addresSec=U.getSectionValue(dataSec, "</strong><br/>", "',");
				if (addresSec==null) {
					addresSec=U.getSectionValue(dataSec, "</h3>", "<br/>");
				}
				U.log(addresSec);
				String addAndNeigh[]=SplitNeighborhoodFromAdd.splitColonia(addresSec);
				String latlonSec=Util.match(dataSec, "\\d{2,3}.\\d+, -\\d{2,3}.\\d+");
				String latlon[]=latlonSec.split(",");
				String out[]= {"",comName,addAndNeigh[0],addAndNeigh[1],"","","","","http://grupoindustrialbonasa.com.mx","","",latlon[0],latlon[1].trim(),urls[0]};
				writer.writeNext(out);
//				if (count==2) break;
			}
			FileUtil.writeAllText(U.getCachePath()+"GrupoIndustrialBonasa2.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
