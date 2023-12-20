package com.tequila.extraction;

import java.io.StringWriter;
import java.util.List;
import java.util.Arrays;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractFetasa {
	private static String baseUrl="/home/mypremserver/MexicoCache/Cache/ADIAC_Unique.csv";
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
			String fileName="/home/mypremserver/MexicoCache/Cache/Fetasa_Unique.csv";
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
						U.formatNumbersAsCode(record[8]),
						record[9].toLowerCase(),
						record[10].toLowerCase(),
						null,
						null,
						null,
						null,
						U.toCapitalizeCase(record[13]),
						record[11],
						record[12],
						record[14],
						U.getTodayDate()
					};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace("_Unique.csv", "_Unique_INSERT.csv"), sw.toString());
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
			String basehtml=U.getHTML(baseUrl);
			String mainSecs[]=U.getValues(basehtml, "<img class=\"alignleft", "</script>");
			String header[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","EMAIL","URL","OPENED_IN","MIN_EMP_COUNT","MAX_EMP_COUNT","LATITUDE","LONGITUDE"};
			writer.writeNext(header);
			for (String sec : mainSecs) {
				String name=U.getSectionValue(sec, "<h4 style=\"color: #F86B35; margin-top:0px\" >", "</h4>");
				String addSec=U.removeHtml(U.getSectionValue(sec, "<div align=\"justify\">", "</div>").replace("<br>", ","));
				U.log(addSec);
				String address[]=addSec.split(",");
				String add[]= {"","","","",""};
				add[0]=address[0].trim();
				add[1]=address[1].trim();
				add[2]=address[2].trim();
				add[3]=address[3].trim();
				add[4]=Util.match(address[5],"\\d{4,}");
				U.log(Arrays.toString(add)+"\n-=-=-=-=-==");
				String phone=U.getSectionValue(sec, "color: #111111\"><div align=\"justify\">", "</div>").replace(",", ";");
				//U.log(sec);
				String laLonSec=Util.match(sec, "\\d{2}.\\d+,-\\d{2,3}.\\d+");
//				U.log(laLonSec);
				String latLon[]=laLonSec.split(",");
				String out[]= {"3325","Fetasa",add[0],add[1],add[2],add[3],add[4],U.formatNumbersAsCode(phone),"contacto@fetasa.com","http://www.fetasa.com.mx","1974","201","500",latLon[0],latLon[1]};
				writer.writeNext(out);
//				U.log(Arrays.toString(latLon));
				//break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Fetasa.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
